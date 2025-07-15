package de.chloedev.config.json.io;

import de.chloedev.config.error.ElementNotFoundException;
import de.chloedev.config.json.element.JsonElement;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class ConfigurationBase {

  /**
   * Retrieves the value associated with the given key from the config
   *
   * @param key the key that the value is associated with
   * @return The retrieved value.
   * @throws ElementNotFoundException if no value is associated with the given key.
   */
  public abstract JsonElement get(String key) throws ElementNotFoundException;

  /**
   * See above. This allows to specify a default value that is returned instead when no value was
   * found
   *
   * @param key the key that the value is associated with
   * @param defaultValue the default value
   * @return The retrieved value.
   * @throws ElementNotFoundException if no value is associated with the given key.
   */
  public abstract JsonElement getOrDefault(String key, JsonElement defaultValue);

  /**
   * Assigns a value to the given key If the key already had a value assigned, this will overwrite
   * it.
   *
   * @param key the key to assign the value to
   * @param value the value to assign to the key
   */
  public abstract void set(String key, JsonElement value);

  /**
   * Removes the given key and it's assigned value from the config
   *
   * @param key the key to remove
   */
  public abstract void remove(String key);

  /** Clears the entire config. */
  public abstract void clear();

  /**
   * Retrieves all nested keys that have values assigned from the given root path.
   *
   * @param root the path to start searching at
   * @param recursive whether to search recursively, or only in the given nest
   * @return all found keys in the order that they were found.
   */
  public abstract Set<String> getKeys(String root, boolean recursive);

  public abstract Set<JsonElement> getValues(String root, boolean recursive);

  public abstract void forEach(BiConsumer<String, JsonElement> callback);

  public abstract String toString(int indentSize);

  @Override
  public String toString() {
    return toString(0);
  }
}
