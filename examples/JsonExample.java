import de.chloedev.config.json.element.JsonObject;
import de.chloedev.config.json.element.JsonPrimitive;
import de.chloedev.config.json.io.JsonConfiguration;
import java.io.File;
import java.io.IOException;

/** This is an example on how you can use the json parts of this library. */
public class JsonExample {

  public static void main(String[] args) {
    // Same as with e.g. 'Gson' or 'org.json', you can utilize
    // different types of json objects, those being:
    // - JsonObject | the most-used json type. It is what holds your keyed data:
    // - JsonArray | the json list type. it can hold all other types of json elements, including
    // objects and arrays themselves.
    // - JsonPrimitive | Primitive data values, those being Numbers, Strings, Booleans, and the
    // keyword 'null'

    // Let's for example create a new json object
    JsonObject myObject = new JsonObject();
    // Let's store the following data:
    // {
    //   "db_login": {
    //     "username": "myUsername",
    //     "password": "mySuperSecurePassword",
    //     "address": "127.0.0.1",
    //     "port": 3306
    //   }
    // }
    myObject.put("db_login/username", "myUsername");
    myObject.put("db_login/password", "mySuperSecurePassword");
    myObject.put("db_login/address", "127.0.0.1");
    myObject.put("db_login/port", 3306);
    // Now you may see the weird slash in there.
    // Other than with other known libraries, here you can use slashes to go into a nested object.
    // This makes it easier to edit nested values, as you don't need to add every object yourself.

    // You can also create a JsonConfiguration.
    // A Configuration allows you to do way fancier stuff than with pure objects and arrays.
    // For example, let's load a new Configuration from a file:
    // Note that "FromFile" might throw an exception (e.g. if the file couldn't be parsed, or didn't
    // exist), so we need to wrap it into a try-catch block.
    try {
      File myFile = new File("path/to/my/file.json");
      JsonConfiguration myConfigFromFile = JsonConfiguration.fromFile(myFile);
      // Let's imagine the file has the same syntax as the object we created above.
      // Let's change the port.
      myConfigFromFile.set("db_login/port", new JsonPrimitive(3306));
      // Now let's re-save it to the file to apply our changes
      // the second param "2" is the amount of indents we save it with.
      // If you want to store it as actual DATA, you might want to use 0 (Single-Line).
      // If you want to store it as human-readable, 2 and 4 are the most standard ones.
      myConfigFromFile.writeToFile(myFile, 2);
      // Done. The file should now be updated ^-^
    } catch (IOException e) {
      // File failed to load, handle it accordingly.
    }
  }
}
