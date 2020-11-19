package com.visualfiredev.javabase;

/**
 * Used to indicate that the data type parsed is invalid.
 */
public class UnsupportedDataTypeException extends Exception {

    /**
     * Constructs a new UnsupportedDataTypeException.
     *
     * @param type A string representing the invalid data type.
     */
    public UnsupportedDataTypeException(String type) {
        super("Attempted to parse unsupported data type: " + type + ". Either this library does not support this data type or it is an invalid data type.");
    }

}
