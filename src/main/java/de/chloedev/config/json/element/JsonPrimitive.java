package de.chloedev.config.json.element;

public class JsonPrimitive extends JsonElement {
  private final Object value;

  public JsonPrimitive() {
    this.value = null;
  }

  public JsonPrimitive(String value) {
    this.value = value;
  }

  public JsonPrimitive(Number value) {
    this.value = value;
  }

  public JsonPrimitive(Boolean value) {
    this.value = value;
  }

  public JsonPrimitive(Object value) {
    if (value == null
        || value instanceof String
        || value instanceof Number
        || value instanceof Boolean) {
      this.value = value;
    } else {
      throw new IllegalArgumentException("Invalid primitive type");
    }
  }

  public boolean isString() {
    return value instanceof String;
  }

  public boolean isNumber() {
    return value instanceof Number;
  }

  public boolean isBoolean() {
    return value instanceof Boolean;
  }

  public boolean isNull() {
    return value == null;
  }

  public String getAsString() {
    if (isString()) {
      return (String) value;
    }
    return value == null ? "null" : value.toString();
  }

  public Number getAsNumber() {
    if (isNumber()) {
      return (Number) value;
    }
    throw new IllegalStateException("Not a number");
  }

  public Boolean getAsBoolean() {
    if (isBoolean()) {
      return (Boolean) value;
    }
    throw new IllegalStateException("Not a boolean");
  }

  @Override
  public String toString() {
    if (value == null) {
      return "null";
    } else if (isString()) {
      return "\"" + escapeString(getAsString()) + "\"";
    } else {
      return value.toString();
    }
  }
}
