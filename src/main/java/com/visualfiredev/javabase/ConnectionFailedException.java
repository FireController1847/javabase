package com.visualfiredev.javabase;

import java.sql.SQLException;

/**
 * Used to indicate that a connection to the database has failed.
 */
public class ConnectionFailedException extends Exception {

    /**
     * Constructs a new ConnectionFailedException.
     *
     * @param e The exception that the database has generated.
     */
    public ConnectionFailedException(SQLException e) {
        super("The database has failed to connect.", e);
    }

}
