package de.chloedev.config.json.element;

import de.chloedev.config.json.JsonParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonElement {
  private final List<JsonElement> elements;

  public JsonArray() {
    this.elements = new ArrayList<>();
  }

  public JsonArray(String jsonString) {
    this.elements = new ArrayList<>();
    JsonElement root = JsonParser.parse(jsonString);
    if (!(root instanceof JsonArray)) {
      throw new IllegalArgumentException("JSON is not an array");
    }
    this.elements.addAll(((JsonArray) root).elements);
  }

  public JsonArray add(JsonElement element) {
    elements.add(element);
    return this;
  }

  public JsonArray add(String value) {
    return add(new JsonPrimitive(value));
  }

  public JsonArray add(Number value) {
    return add(new JsonPrimitive(value));
  }

  public JsonArray add(Boolean value) {
    return add(new JsonPrimitive(value));
  }

  public JsonArray add(JsonObject value) {
    return add(new JsonPrimitive(value));
  }

  public JsonArray add(JsonArray value) {
    return add(new JsonPrimitive(value));
  }

  public JsonArray add() {
    return add(new JsonPrimitive((Object) null));
  }

  public JsonArray addAt(int index, JsonElement element) {
    elements.set(index, element);
    return this;
  }

  public JsonArray addAt(int index, String value) {
    return addAt(index, new JsonPrimitive(value));
  }

  public JsonArray addAt(int index, Number value) {
    return addAt(index, new JsonPrimitive(value));
  }

  public JsonArray addAt(int index, Boolean value) {
    return addAt(index, new JsonPrimitive(value));
  }

  public JsonArray addAt(int index) {
    return addAt(index, new JsonPrimitive((Object) null));
  }

  public JsonElement get(int index) {
    return elements.get(index);
  }

  public JsonElement remove(int index) {
    return elements.remove(index);
  }

  public int size() {
    return elements.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    Iterator<JsonElement> iter = elements.iterator();
    while (iter.hasNext()) {
      sb.append(iter.next().toString());
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    sb.append("]");
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
    sb.append("[\n");
    Iterator<JsonElement> iter = elements.iterator();
    while (iter.hasNext()) {
      JsonElement child = iter.next();
      sb.append(" ".repeat(Math.max(0, (depth + 1) * indentSize)));
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
    sb.append("]");
    return sb.toString();
  }
}
