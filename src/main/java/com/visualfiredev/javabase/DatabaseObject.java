package com.visualfiredev.javabase;

import com.visualfiredev.javabase.schema.TableSchema;

/**
 * A utility class that can be extended from to provide an easy way to map objects to DatabaseValues and vice-versa.
 *
 * Any extensions of this object **must contain a blank constructor**.
 */
public class DatabaseObject {

    // Instance Variables
    private TableSchema tableSchema;

    /**
     * Constructs a new DatabaseObject with an existing TableSchema.
     *
     * @param tableSchema The TableSchema to be used when converting this object to {@link DatabaseValue}'s.
     */
    public DatabaseObject(TableSchema tableSchema) {
        this.tableSchema = tableSchema;
    }

    /**
     * A blank constructor to provide functionality that JavaBase requires.
     */
    public DatabaseObject() { }

    /**
     * A method to provide a layer of abstraction from the {@link DatabaseValue#fromObject(TableSchema, Object)} method.
     *
     * @return An array of {@link DatabaseValue}'s corresponding to this object's fields.
     * @throws Exception Thrown if there is an internal error while mapping the values.
     */
    public DatabaseValue[] toValues() throws Exception {
        try {
            return DatabaseValue.fromObject(tableSchema, this);
        } catch (Exception e) {
            throw new Exception("There was an internal error while attempting to map the values of this DatabaseObject to an array of DatabaseValues.", e);
        }
    }

    /**
     * A method to provide a layer of abstraction from the {@link DatabaseValue#toObject(TableSchema, DatabaseValue[], Object)} method.
     *
     * @param tableSchema The TableSchema to be provided to the created object.
     * @param values The values that should be inserted into this object.
     * @return A new DatabaseObject with the specified values.
     * @throws Exception Thrown if there is an internal error while mapping the values.
     */
    public static DatabaseObject fromValues(TableSchema tableSchema, DatabaseValue[] values) throws Exception {
        try {
            return DatabaseValue.toObject(tableSchema, values, new DatabaseObject(tableSchema));
        } catch (Exception e) {
            throw new Exception("There was an internal error while attempting to map the array of DatabaseValue's to this DatabaseObject.");
        }
    }

    /**
     * Fetches the TableSchema from this object.
     * @return This object's TableSchema.
     */
    public TableSchema getTableSchema() {
        return tableSchema;
    }

}
