package com.visualfiredev.javabase;

import com.visualfiredev.javabase.schema.ColumnSchema;
import com.visualfiredev.javabase.schema.TableSchema;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

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
     * Attempts to tie each non-transient variable of the object to a column in the specified TableSchema.
     *
     * <p>
     *     This method should only be used when you are building the TableSchema and ColumnSchema manually. It
     *     does not have advanced functionality and will attempt to parse ALL fields of a class into the
     *     corresponding columns and resulting DatabaseValues.
     *
     *     In the situation that a column is named "ID" in any case and also has "AUTO_INCREMENT" marked as true,
     *     it will not include that in the resulting array.
     *
     *     In the case that it fails, it will return a blank array of DatabaseValues. This is a limitation
     *     of this method and is not going to be fixed.
     *
     *     It is strongly recommended to use the annotation system instead.
     * </p>
     *
     * TODO: TODO: Link to annotation system
     *
     * @param tableSchema The schema in which columns will be detected from.
     * @param instance The instance in which the values should be pulled from.
     * @param <T> The type of object that the instance is.
     * @return An array of DatabaseValues[] which can be used for insertion or other purposes.
     */
    public static <T> DatabaseValue[] fromObject(TableSchema tableSchema, T instance) throws Exception {
        // Get Fields
        ArrayList<Field> fields = new ArrayList<>();
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            fields.add(field);
        }

        ArrayList<DatabaseValue> values = new ArrayList<>();
        for (Field field : fields) {
            ColumnSchema column = tableSchema.getColumnIgnoreCase(field.getName());
            if (column != null && !column.getName().toLowerCase().equals("id")) {
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
