package com.visualfiredev.javabase;

/**
 * Thrown when something that requires being connected to the database is no longer connected.
 */
public class NotConnectedException extends Exception {

    /**
     * Constructs a new NotConnectedException.
     */
    public NotConnectedException() {
        super("There is no connection to the database! Was the connection lost?");
    }

}
