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
     * @param clazz The class to create a new instance of. This cannot be assumed from the extended type.
     * @param <T> The type of the object to be created.
     * @return A new DatabaseObject with the specified values.
     * @throws Exception Thrown if there is an internal error while mapping the values.
     */
    public static <T> T fromValues(TableSchema tableSchema, DatabaseValue[] values, Class<T> clazz) throws Exception {
        try {
            return DatabaseValue.toObject(tableSchema, values, clazz.getConstructor().newInstance());
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
