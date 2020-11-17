package com.visualfiredev.javabase.schema;

import com.visualfiredev.javabase.DataType;
import com.visualfiredev.javabase.DatabaseType;

/**
 * Defines a schema for a column of a table.
 */
public class ColumnSchema {

    // Constructor Arguments
    private String name;
    private DataType dataType;

    /**
     * Creates a new column schema using the specified name and datatype. Other arguments can set after construction.
     * TODO: Add support for a variety of column options.
     *
     * @param name The name of this column.
     * @param dataType The {@link com.visualfiredev.javabase.DataType} this column uses.
     */
    public ColumnSchema(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    /**
     * Checks whether or not this ColumnSchema supports the passed {@link com.visualfiredev.javabase.DatabaseType}.
     *
     * @param databaseType The {@link com.visualfiredev.javabase.DatabaseType} to check if is supported.
     * @return True if the {@link com.visualfiredev.javabase.DataType} associated with this column schema supports the passed database type, otherwise false.
     */
    public boolean supportsDatabaseType(DatabaseType databaseType) {
        if (!dataType.supportsDatabaseType(databaseType)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the name of this column.
     * @return The name of this column.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link com.visualfiredev.javabase.DataType} of this column.
     * @return The {@link com.visualfiredev.javabase.DataType} of this column.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the name of this column schema.
     *
     * @param name The name this column schema should be set to.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the {@link com.visualfiredev.javabase.DataType} of this column schema.
     * @param dataType The {@link com.visualfiredev.javabase.DataType} this column schema should be set to.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

}
