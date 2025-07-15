package de.chloedev.config.error;

import java.io.Serial;

public class ElementNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = 4467945912046345107L;

  public ElementNotFoundException() {
    super();
  }

  public ElementNotFoundException(String message) {
    super(message);
  }

  public ElementNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ElementNotFoundException(Throwable cause) {
    super(cause);
  }
}
