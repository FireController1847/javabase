package com.visualfiredev.javabase;

/**
 * Used to indicate that the data type is invalid for this type of database.
 */
public class UnsupportedDatabaseTypeException extends Exception {

    /**
     * Constructs a new UnsupportedDatabaseTypeException.
     *
     * @param dataType The {@link com.visualfiredev.javabase.DataType} that does not support this {@link com.visualfiredev.javabase.DatabaseType}.
     * @param databaseType The {@link com.visualfiredev.javabase.DatabaseType} that this {@link com.visualfiredev.javabase.DataType} does not support.
     */
    public UnsupportedDatabaseTypeException(DataType dataType, DatabaseType databaseType) {
        super("The data type " + dataType.toString() + " does not support the following database type: " + databaseType.toString());
    }

}
