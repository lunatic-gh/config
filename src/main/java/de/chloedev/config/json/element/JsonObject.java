package de.chloedev.config.json.element;

import de.chloedev.config.json.JsonParser;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public class JsonObject extends JsonElement {
  private final Map<String, JsonElement> members;

  public JsonObject() {
    this.members = new LinkedHashMap<>();
  }

  public JsonObject(String jsonString) {
    this();
    JsonElement root = JsonParser.parse(jsonString);
    if (!(root instanceof JsonObject)) {
      throw new IllegalArgumentException("JSON is not an object");
    }
    this.members.putAll(((JsonObject) root).members);
  }

  public JsonObject put(String key, JsonElement value) {
    String[] parts = key.split("/");
    JsonObject cursor = this;
    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      JsonElement child = cursor.members.get(part);
      if (!(child instanceof JsonObject)) {
        JsonObject next = new JsonObject();
        cursor.members.put(part, next);
        cursor = next;
      } else {
        cursor = (JsonObject) child;
      }
    }
    cursor.members.put(parts[parts.length - 1], value);
    return this;
  }

  public JsonObject put(String key, String value) {
    return put(key, new JsonPrimitive(value));
  }

  public JsonObject put(String key, Number value) {
    return put(key, new JsonPrimitive(value));
  }

  public JsonObject put(String key, Boolean value) {
    return put(key, new JsonPrimitive(value));
  }

  public JsonObject put(String key) {
    return put(key, new JsonPrimitive((Object) null));
  }

  public JsonElement get(String key) {
    String[] parts = key.split("/");
    JsonObject cursor = this;
    for (int i = 0; i < parts.length - 1; i++) {
      JsonElement child = cursor.members.get(parts[i]);
      if (!(child instanceof JsonObject)) {
        return null;
      }
      cursor = (JsonObject) child;
    }
    return cursor.members.get(parts[parts.length - 1]);
  }

  public boolean has(String key) {
    return get(key) != null;
  }

  public JsonElement remove(String key) {
    String[] parts = key.split("/");
    JsonObject cursor = this;
    for (int i = 0; i < parts.length - 1; i++) {
      JsonElement child = cursor.members.get(parts[i]);
      if (!(child instanceof JsonObject)) {
        return null;
      }
      cursor = (JsonObject) child;
    }
    return cursor.members.remove(parts[parts.length - 1]);
  }

  public int size() {
    return members.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    Iterator<Map.Entry<String, JsonElement>> iter = members.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, JsonElement> e = iter.next();
      sb.append("\"")
          .append(escapeString(e.getKey()))
          .append("\":")
          .append(e.getValue().toString());
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    sb.append("}");
    return sb.toString();
  }

  public String toString(int indentSize) {
    return toString(indentSize, 0);
  }

  @SuppressWarnings("DuplicatedCode")
  protected String toString(int indentSize, int depth) {
    if (indentSize == 0) {
      return toString();
    }
    StringBuilder sb = new StringBuilder();
    sb.append("{\n");
    Iterator<Map.Entry<String, JsonElement>> iter = members.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, JsonElement> e = iter.next();
      sb.append(" ".repeat(Math.max(0, (depth + 1) * indentSize)));
      sb.append("\"").append(escapeString(e.getKey())).append("\": ");
      JsonElement child = e.getValue();
      if (child instanceof JsonObject) {
        sb.append(((JsonObject) child).toString(indentSize, depth + 1));
      } else if (child instanceof JsonArray) {
        sb.append(((JsonArray) child).toString(indentSize, depth + 1));
      } else {
        sb.append(child.toString());
      }
      if (iter.hasNext()) {
        sb.append(",");
      }
      sb.append("\n");
    }
    sb.append(" ".repeat(Math.max(0, depth * indentSize)));
    sb.append("}");
    return sb.toString();
  }

  public Set<Map.Entry<String, JsonElement>> entrySet() {
    return members.entrySet();
  }
}
