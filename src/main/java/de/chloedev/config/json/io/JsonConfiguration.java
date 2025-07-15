package de.chloedev.config.json.io;

import de.chloedev.config.error.ElementNotFoundException;
import de.chloedev.config.json.JsonParser;
import de.chloedev.config.json.element.JsonArray;
import de.chloedev.config.json.element.JsonElement;
import de.chloedev.config.json.element.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class JsonConfiguration extends ConfigurationBase {
  private JsonElement root;

  private JsonConfiguration() {
    this.root = new JsonObject();
  }

  private JsonConfiguration(JsonElement root) {
    this.root = root;
  }

  private JsonConfiguration(String jsonStr) {
    JsonElement el = JsonParser.parse(jsonStr);
    if (!(el instanceof JsonObject) && !(el instanceof JsonArray)) {
      throw new IllegalArgumentException("'jsonStr' must be of type JsonObject or JsonArray.");
    }
    this.root = el;
  }

  public static JsonConfiguration fromObject(JsonObject object) {
    return new JsonConfiguration(object);
  }

  public static JsonConfiguration fromArray(JsonArray array) {
    return new JsonConfiguration(array);
  }

  public static JsonConfiguration fromString(String jsonStr) {
    return new JsonConfiguration(jsonStr);
  }

  public static JsonConfiguration fromFile(File file)
      throws IOException, OutOfMemoryError, SecurityException {
    String jsonStr = Files.readString(file.toPath(), StandardCharsets.UTF_8);
    return new JsonConfiguration(jsonStr);
  }

  @Override
  public JsonElement get(String key) throws ElementNotFoundException {
    if (key == null || key.isEmpty()) {
      return root;
    }
    String[] parts = key.split("/");
    JsonElement cur = root;
    for (String part : parts) {
      if (cur instanceof JsonObject obj) {
        if (!obj.has(part)) {
          throw new ElementNotFoundException("No such key: " + key);
        }
        cur = obj.get(part);

      } else if (cur instanceof JsonArray arr) {
        int idx;
        try {
          idx = Integer.parseInt(part);
        } catch (NumberFormatException ex) {
          throw new ElementNotFoundException("Expected array index but got '" + part + "'");
        }
        if (idx < 0 || idx >= arr.size()) {
          throw new ElementNotFoundException("Index out of bounds: " + idx);
        }
        cur = arr.get(idx);

      } else {
        throw new ElementNotFoundException("Cannot navigate into a primitive at '" + part + "'");
      }
    }
    return cur;
  }

  @Override
  public JsonElement getOrDefault(String key, JsonElement defaultValue) {
    try {
      return get(key);
    } catch (ElementNotFoundException ex) {
      return defaultValue;
    }
  }

  @Override
  public void set(String key, JsonElement value) {
    if (key == null || key.isEmpty()) {
      // replace whole root
      if (!(value instanceof JsonObject) && !(value instanceof JsonArray)) {
        throw new IllegalArgumentException("Root must be object or array");
      }
      this.root = value;
      return;
    }
    String[] parts = key.split("/");
    JsonElement cur = root;
    // walk/create up to the parent of the leaf
    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      JsonElement next;
      if (cur instanceof JsonObject obj) {
        next = obj.get(part);
        if (!(next instanceof JsonObject) && !(next instanceof JsonArray)) {
          // auto‐create object
          JsonObject newObj = new JsonObject();
          obj.put(part, newObj);
          cur = newObj;
        } else {
          cur = next;
        }

      } else if (cur instanceof JsonArray arr) {
        int idx = Integer.parseInt(part);
        next = arr.get(idx);
        if (!(next instanceof JsonObject) && !(next instanceof JsonArray)) {
          JsonObject newObj = new JsonObject();
          arr.addAt(idx, newObj);
          cur = newObj;
        } else {
          cur = next;
        }

      } else {
        throw new IllegalArgumentException("Cannot traverse into primitive at '" + part + "'");
      }
    }

    // finally set the leaf in its parent
    String leaf = parts[parts.length - 1];
    if (cur instanceof JsonObject obj) {
      obj.put(leaf, value);

    } else if (cur instanceof JsonArray arr) {
      int idx = Integer.parseInt(leaf);
      arr.addAt(idx, value);

    } else {
      throw new IllegalArgumentException("Cannot set value into a primitive at '" + leaf + "'");
    }
  }

  @Override
  public void remove(String key) {
    if (key == null || key.isEmpty()) {
      clear();
      return;
    }
    String[] parts = key.split("/");
    JsonElement cur = root;
    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      if (cur instanceof JsonObject obj) {
        cur = obj.get(part);
      } else if (cur instanceof JsonArray arr) {
        cur = arr.get(Integer.parseInt(part));
      } else {
        return; // nothing to remove
      }
    }
    String leaf = parts[parts.length - 1];
    if (cur instanceof JsonObject obj) {
      obj.remove(leaf);
    } else if (cur instanceof JsonArray arr) {
      arr.remove(Integer.parseInt(leaf));
    }
  }

  @Override
  public void clear() {
    if (root instanceof JsonObject) {
      root = new JsonObject();
    } else {
      root = new JsonArray();
    }
  }

  @Override
  public Set<String> getKeys(String path, boolean recursive) {
    JsonElement base;
    try {
      base = (path == null || path.isEmpty()) ? root : get(path);
    } catch (Exception ex) {
      return Set.of();
    }
    Set<String> out = new LinkedHashSet<>();
    collectKeys(base, (path == null ? "" : path), recursive, out);
    return out;
  }

  private void collectKeys(JsonElement el, String prefix, boolean recursive, Set<String> out) {
    if (el instanceof JsonObject obj) {
      for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
        String full = prefix.isEmpty() ? e.getKey() : prefix + "/" + e.getKey();
        out.add(full);
        if (recursive) {
          collectKeys(e.getValue(), full, true, out);
        }
      }
    } else if (el instanceof JsonArray arr) {
      for (int i = 0; i < arr.size(); i++) {
        String idx = String.valueOf(i);
        String full = prefix.isEmpty() ? idx : prefix + "/" + idx;
        out.add(full);
        if (recursive) {
          collectKeys(arr.get(i), full, true, out);
        }
      }
    }
  }

  @Override
  public Set<JsonElement> getValues(String path, boolean recursive) {
    Set<JsonElement> vals = new LinkedHashSet<>();
    for (String k : getKeys(path, recursive)) {
      vals.add(getOrDefault(k, null));
    }
    return vals;
  }

  @Override
  public void forEach(BiConsumer<String, JsonElement> callback) {
    if (root instanceof JsonObject obj) {
      obj.entrySet().forEach(e -> callback.accept(e.getKey(), e.getValue()));
    } else {
      JsonArray arr = (JsonArray) root;
      for (int i = 0; i < arr.size(); i++) {
        callback.accept(String.valueOf(i), arr.get(i));
      }
    }
  }

  public boolean writeToFile(File file, int indentSize) {
    try {
      Files.writeString(
          file.toPath(),
          this.root instanceof JsonObject
              ? ((JsonObject) this.root).toString(indentSize)
              : ((JsonArray) this.root).toString(indentSize),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  @Override
  public String toString(int indentSize) {
    return this.root instanceof JsonObject
        ? ((JsonObject) this.root).toString(indentSize)
        : ((JsonArray) this.root).toString(indentSize);
  }
}
