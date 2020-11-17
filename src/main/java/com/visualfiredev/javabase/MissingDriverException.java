package com.visualfiredev.javabase;

/**
 * Used to indicate that a connector for a database type may not be installed.
 */
public class MissingDriverException extends Exception {

    /**
     * Constructs a new MissingDriverException.
     *
     * @param type The database type that is missing a driver.
     */
    public MissingDriverException(DatabaseType type) {
        super("There was an error loading the driver for " + type.toString() + ". Did you install the correct connector?");
    }

}
