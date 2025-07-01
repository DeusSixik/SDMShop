package net.sixik.sdmshop.shop.exceptions;

public class EntryNotFoundException extends RuntimeException {

  public EntryNotFoundException() {
    this("Shop Entry not found");
  }

  public EntryNotFoundException(String message) {
    super(message);
  }
}
