package net.sixik.sdmshop.shop.exceptions;

public class TabNotFoundException extends RuntimeException {

    public TabNotFoundException() {
        this("Shop Tab not found!");
    }

    public TabNotFoundException(String message) {
        super(message);
    }
}
