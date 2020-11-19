package com.visualfiredev.javabase.schema;

import com.visualfiredev.javabase.DataType;
import com.visualfiredev.javabase.DatabaseType;
import com.visualfiredev.javabase.UnsupportedDatabaseTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines a schema for a column of a table.
 */
public class ColumnSchema {

    // Constructor Arguments
    private String name;
    private DataType dataType;
    private Object value;

    // Column Options
    // TODO: Not all column options are supported.
    private Object defaultValue = null;
    private boolean isPrimaryKey = false;
    private boolean isUniqueKey = false;
    private boolean isAutoIncrement = false;
    private boolean isNotNull = false;

    /**
     * Creates a new column schema using the specified name, datatype, and value. Other arguments can set after construction.
     *
     * @param name The name of this column.
     * @param dataType The {@link com.visualfiredev.javabase.DataType} this column uses.
     * @param value The value associated with this datatype as formatted for the corresponding {@link com.visualfiredev.javabase.DatabaseType}. For example, a VARCHAR(30) would have an integer value of 30.
     */
    public ColumnSchema(@NotNull String name, @NotNull DataType dataType, @Nullable Object value) {
        this.name = name;
        this.dataType = dataType;
        this.value = value;
    }

    /**
     * Creates a new column schema using the specified name and datatype with no value. Other arguments can set after construction.
     *
     * @param name The name of this column.
     * @param dataType The {@link com.visualfiredev.javabase.DataType} this column uses.
     */
    public ColumnSchema(@NotNull String name, @NotNull DataType dataType) {
        this(name, dataType, null);
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
     * Returns the default value of this ColumnSchema.
     * @return The default value of this ColumnSchema.
     */
    @Nullable
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns whether or not this column is a primary key.
     * @return Whether or not this column is a primary key.
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * Returns whether or not this column is a unique key, or false if this column is already a primary key.
     * @return Whether or not this column is a unique key, or false if this column is already a primary key.
     */
    public boolean isUniqueKey() {
        return !isPrimaryKey && isUniqueKey;
    }

    /**
     * Returns whether or not this column auto increments.
     * @return Whether or not this column auto increments.
     */
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    /**
     * Returns whether or not this column is not null.
     * @return Whether or not this column is not null.
     */
    public boolean isNotNull() {
        return isNotNull;
    }

    /**
     * Returns the name of this column.
     * @return The name of this column.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link com.visualfiredev.javabase.DataType} of this column.
     * @return The {@link com.visualfiredev.javabase.DataType} of this column.
     */
    @NotNull
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Returns a string associated with the value for this object
     * @return A string associated with the value for this object.
     */
    @Nullable
    public String getValue() {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * Sets the default value of this column schema.
     * @param defaultValue The default value of this column schema.
     * @return The ColumnSchema.
     */
    public ColumnSchema setDefaultValue(@Nullable Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Set to true if you want this column to be a primary key, otherwise set to false.
     * @param primaryKey Whether or not this column should be a primary key.
     * @return The ColumnSchema.
     */
    public ColumnSchema setPrimaryKey(boolean primaryKey) {
        this.isPrimaryKey = primaryKey;
        return this;
    }

    /**
     * Set to true if you want this column to be a unique key, otherwise set to false.
     * @param uniqueKey Whether or not this column should be a unique key.
     * @return The ColumnSchema.
     */
    public ColumnSchema setUniqueKey(boolean uniqueKey) {
        isUniqueKey = uniqueKey;
        return this;
    }

    /**
     * Set to true if you want this column to auto increment, otherwise set to false.
     * @param autoIncrement Whether or not this column should auto increment.
     * @return The ColumnSchema.
     */
    public ColumnSchema setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
        return this;
    }

    /**
     * Sets whether or not this column should have the "NOT NULL" tag.
     * @param notNull Whether or not this column should have the "NOT NULL" tag.
     * @return The ColumnSchema.
     */
    public ColumnSchema setNotNull(boolean notNull) {
        isNotNull = notNull;
        return this;
    }

    /**
     * Sets the name of this column schema.
     *
     * @param name The name this column schema should be set to.
     * @return The ColumnSchema.
     */
    public ColumnSchema setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the {@link com.visualfiredev.javabase.DataType} of this column schema.
     * @param dataType The {@link com.visualfiredev.javabase.DataType} this column schema should be set to.
     * @return The ColumnSchema.
     */
    public ColumnSchema setDataType(@NotNull DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Sets the value for the {@link com.visualfiredev.javabase.DataType} of this column schema.
     * @param value The value for the {@link com.visualfiredev.javabase.DataType} of this column schema.
     * @return The ColumnSchema.
     */
    public ColumnSchema setValue(@NotNull Object value) {
        this.value = value;
        return this;
    }

    /**
     * Converts this column to a string that can be used with the specified {@link com.visualfiredev.javabase.DatabaseType}.
     *
     * @param databaseType The {@link com.visualfiredev.javabase.DatabaseType} that this string should be made for.
     * @return The stringified version of this column schema.
     * @throws UnsupportedDatabaseTypeException Thrown if this {@link com.visualfiredev.javabase.schema.ColumnSchema} does not support the specified {@link com.visualfiredev.javabase.DatabaseType}.
     */
    public String toString(DatabaseType databaseType) throws UnsupportedDatabaseTypeException {
        // Confirm Support
        if (!this.supportsDatabaseType(databaseType)) {
            throw new UnsupportedDatabaseTypeException(this.getDataType(), databaseType);
        }

        // Create String & Add Name
        StringBuilder sql = new StringBuilder(this.getName());

        // Append DataType
        sql.append(" ").append(this.getDataType());

        // Append Value (If Exists)
        String val = this.getValue();
        if (val != null) {
            sql.append("(").append(val).append(")");
        }

        // Append Not Null
        if (this.isNotNull()) {
            sql.append(" NOT NULL");
        }

        // Primary Key
        if (this.isPrimaryKey()) {
            sql.append(" PRIMARY KEY");
        }

        // Unique Key
        if (this.isUniqueKey()) {
            if (databaseType == DatabaseType.SQLite) {
                sql.append(" UNIQUE");
            } else {
                sql.append(" UNIQUE KEY");
            }
        }

        // Auto Increment
        if (this.isAutoIncrement()) {
            if (databaseType == DatabaseType.SQLite) {
                sql.append(" AUTOINCREMENT");
            } else {
                sql.append(" AUTO_INCREMENT");
            }
        }

        // Default Value
        Object defValue = this.getDefaultValue();
        if (defValue != null) {
            if (defValue instanceof Number) {
                sql.append(" DEFAULT ").append(defValue);
            } else {
                sql.append(" DEFAULT '").append(defValue).append("'");
            }
        }

        return sql.toString();
    }

}
