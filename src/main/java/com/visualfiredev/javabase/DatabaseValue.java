package com.visualfiredev.javabase;

import com.visualfiredev.javabase.schema.ColumnSchema;
import com.visualfiredev.javabase.schema.TableSchema;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

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
     * Attempts to tie the non-transient fields in the class to a {@link DatabaseValue}[] (or a row in the database).
     *
     * <p>
     *     This method uses reflection to try and determine how to map the Field names of the class to an array of
     *     DatabaseValue's ColumnNames. It may throw various exceptions relating to reflection if there is no
     *     alignment between the TableSchema and the T object.
     *
     *     If a field in the class has the name `id` in any case and the corresponding ColumnSchema states that it
     *     is auto-increment, it will not be included in the final array of DatabaseValue's.
     *
     *     **This is a case-insensitive operation, so column `EXAMPLE` will be mapped to a field named `example`.**
     * </p>
     *
     * @param tableSchema The {@link TableSchema} that should be used to determine what columns the object has.
     * @param instance The instance of the object to pull data from.
     * @param <T> The class that the object is an instance of.
     * @return An array of {@link DatabaseValue}'s representing the fields in the object.
     * @throws Exception Generically thrown if something goes wrong.
     */
    public static <T> DatabaseValue[] fromObject(TableSchema tableSchema, T instance) throws Exception {
        // Get Fields
        ArrayList<Field> fields = Database.getNonTransientFields(instance.getClass());

        // Map Fields to Values
        ArrayList<DatabaseValue> values = new ArrayList<>();
        for (Field field : fields) {
            ColumnSchema column = tableSchema.getColumnIgnoreCase(field.getName());
            if (column != null && !(column.isPrimaryKey() && column.isAutoIncrement())) {
                try {
                    values.add(new DatabaseValue(column.getName(), field.get(instance)));
                } catch (ReflectiveOperationException e) {
                    throw new Exception("There was an internal error while trying to get the value of a field.", e);
                }
            }
        }

        return values.toArray(new DatabaseValue[0]);
    }

    /**
     * Attempts to tie the DatabaseValue[] (or a row in the database) to the non-transient fields in the class.
     *
     * <p>
     *     This method uses reflection to try and determine how to map the DatabaseValue's ColumnName to the
     *     Field names of the class. It may throw various exceptions relating to reflection if there is no
     *     alignment between the TableSchema and the T object.
     *
     *     When a field is a boolean value and the database contains an integer, the field will be TRUE if
     *     the integer is greater than zero, otherwise it will be FALSE if it the integer is less than or
     *     equal to zero.
     *
     *     **This is a case-insensitive operation, so column `EXAMPLE` will be mapped to a field named `example`.**
     * </p>
     *
     * @param tableSchema The {@link TableSchema} that should be used to determine what columns the object has.
     * @param values The values that should be inserted into the fields.
     * @param instance The instance of the object to insert data into.
     * @param <T> The class that the object is an instance of.
     * @return The object with the newly filled values.
     * @throws Exception Generically thrown if something goes wrong.
     */
    public static <T> T toObject(TableSchema tableSchema, DatabaseValue[] values, T instance) throws Exception {
        // Get Fields
        HashMap<String, Field> fields = new HashMap<>();
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            fields.put(field.getName().toLowerCase(), field);
        }

        // Map Values to Fields
        for (DatabaseValue value : values) {
            ColumnSchema column = tableSchema.getColumnIgnoreCase(value.getColumnName());
            if (column != null) {
                try {
                    Field field = fields.get(column.getName().toLowerCase());
                    if (field != null) {
                        // Check for boolean
                        if (value.getData() instanceof Integer && field.getType().isAssignableFrom(boolean.class)) {
                            Integer data = (Integer) value.getData();
                            if (data > 0) {
                                field.set(instance, true);
                            } else {
                                field.set(instance, false);
                            }

                        // Handle Floats
                        } else if (value.getData() instanceof Double && field.getType().isAssignableFrom(float.class)) {
                            field.set(instance, (float) ((double) value.getData()));

                        // Handle Bytes
                        } else if (value.getData() instanceof Integer && field.getType().isAssignableFrom(byte.class)) {
                            field.set(instance, (byte) ((int) value.getData()));

                        // Everything Else
                        } else {
                            field.set(instance, value.getData());
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("There was an internal error while attempting to insert the value for a field! Do the types line up?", e);
                }
            }
        }

        return instance;
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

    @Override
    public String toString() {
        return "DatabaseValue{" +
                "columnName='" + columnName + '\'' +
                ", data=" + data +
                '}';
    }
}
