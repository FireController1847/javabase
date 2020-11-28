package com.visualfiredev.javabase.schema;

import com.visualfiredev.javabase.DatabaseType;
import com.visualfiredev.javabase.UnsupportedDatabaseTypeException;
import com.visualfiredev.javabase.UnsupportedFeatureException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Defines a schema for a table.
 */
public class TableSchema {

    // Constructor Arguments
    private String name;
    private ArrayList<ColumnSchema> columns;

    // Table Options
    private boolean ifNotExists = false;
    private boolean orReplace = false;

    /**
     * Creates a new table schema using the specified name and columns. Other arguments can be set after construction.
     *
     * @param name The name of this column.
     * @param columns An array of {@link com.visualfiredev.javabase.schema.ColumnSchema}'s this table uses.
     */
    public TableSchema(@NotNull String name, @NotNull ColumnSchema... columns) {
        this.name = name;
        this.columns = new ArrayList<>(Arrays.asList(columns));
    }

    /**
     * Returns whether or not this schema includes "IF NOT EXISTS"
     * @return Whether or not this schema includes "IF NOT EXISTS"
     */
    public boolean isIfNotExists() {
        return ifNotExists;
    }

    /**
     * Returns whether or not this schema includes "OR REPLACE"
     * @return Whether or not this schema includes "OR REPLACE"
     */
    public boolean isOrReplace() {
        return orReplace;
    }

    /**
     * Returns the name of this table.
     * @return The name of this table.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an ArrayList of ColumnSchemas added to this TableSchema.
     * @return An ArrayList of ColumnSchemas added to this TableSchema.
     */
    public ArrayList<ColumnSchema> getColumns() {
        return columns;
    }

    /**
     * Fetches a ColumnSchema by its name.
     * @param name The name of the ColumnSchema.
     * @return The corresponding ColumnSchema or null if not found.
     */
    public ColumnSchema getColumn(String name) {
        for (ColumnSchema column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    /**
     * Fetches a ColumnSchema by its name ignoring the case.
     * @param name The name of the ColumnSchema.
     * @return The corresponding ColumnSchema or null if not found.
     */
    public ColumnSchema getColumnIgnoreCase(String name) {
        for (ColumnSchema column : columns) {
            if (column.getName().toLowerCase().equals(name.toLowerCase())) {
                return column;
            }
        }
        return null;
    }

    /**
     * Sets whether or not this table should include "IF NOT EXISTS". Takes priority over "OR REPLACE"
     *
     * @param ifNotExists Whether or not this table should include "IF NOT EXISTS"
     * @return The TableSchema.
     */
    public TableSchema setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
        return this;
    }

    /**
     * Sets whether or not this table should include "OR REPLACE"<br>
     * WARNING: Unsupported by SQLite.
     *
     * @param orReplace Whether or not this table should include "OR REPLACE"
     * @return The TableSchema.
     */
    public TableSchema setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
        return this;
    }

    /**
     * Sets the name of this table schema.
     *
     * @param name The name this table schema should be set to.
     * @return The TableSchema
     */
    public TableSchema setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Adds a column to this TableSchema.
     *
     * @param column The column to be added to this TableSchema.
     * @return The TableSchema.
     */
    public TableSchema addColumn(ColumnSchema column) {
        this.columns.add(column);
        return this;
    }

    /**
     * Removes a column from this TableSchema.
     *
     * @param column The column to be removed from this TableSchema.
     * @return The TableSchema.
     */
    public TableSchema removeColumn(ColumnSchema column) {
        this.columns.remove(column);
        return this;
    }

    /**
     * Converts this table schema to a "CREATE TABLE" sql string.
     *
     * @param databaseType The {@link com.visualfiredev.javabase.DatabaseType} that this string should be made for.
     * @return The stringified version of this table schema.
     * @throws UnsupportedDatabaseTypeException Thrown if any of the {@link com.visualfiredev.javabase.schema.ColumnSchema}'s do not support this type of database.
     * @throws UnsupportedFeatureException Thrown if a feature was enabled that this database does not support.
     */
    @NotNull
    public String toString(@NotNull DatabaseType databaseType) throws UnsupportedDatabaseTypeException, UnsupportedFeatureException {
        // Validate Features
        if (databaseType == DatabaseType.SQLite && orReplace) {
            throw new UnsupportedFeatureException(databaseType, "CREATE TABLE OR REPLACE");
        }

        // Create String
        StringBuilder sql = new StringBuilder("CREATE ");

        // If Not Exists
        if (ifNotExists) {
            sql.append("TABLE IF NOT EXISTS ");
        } else if (orReplace) {
            sql.append("OR REPLACE TABLE ");
        } else {
            sql.append("TABLE ");
        }

        // Name
        sql.append(name);

        // Columns
        sql.append(" ( ");
        for (int i = 0; i < columns.size(); i++) {
            ColumnSchema column = columns.get(i);
            sql.append(column.toString(databaseType));

            // Comma? Are there more?
            if (i != columns.size() - 1) {
                sql.append(", ");
            }
        }

        // Close
        sql.append(" );");

        // Return String
        return sql.toString();
    }
}
