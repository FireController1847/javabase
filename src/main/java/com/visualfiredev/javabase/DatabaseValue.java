package com.visualfiredev.javabase;

/**
 * Represents a value in a database. Requires the corresponding ColumnSchema.
 */
public class DatabaseValue {

    // Constructor Arguments
    private String columnName;
    private Object data;

    /**
     * Creates a new database value using the column schema as a basis and the object as the data.
     *
     * @param columnName The name of the column for this DatabaseValue.
     * @param data The data.
     */
    public DatabaseValue(String columnName, Object data) {
        this.columnName = columnName;
        this.data = data;
    }

    /**
     * Fetches the name of the column associated with this DatabaseValue.
     * @return The name of the column associated with this DatabaseValue.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Fetches the data.
     * @return The data.
     */
    public Object getData() {
        return data;
    }

}
