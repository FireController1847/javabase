package com.visualfiredev.javabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents the result of an operation that returns data in a database.
 */
public class DatabaseResult {

    // Constructor Arguments
    private ArrayList<DatabaseValue> values;
    private int columnCount;

    /**
     * Creates a new DatabaseResult using the list of values provided.
     * @param columnCount The amount of columns there are in this result. Used to calculate rows.
     * @param values The list of {@link com.visualfiredev.javabase.DatabaseValue}s to include in this result.
     */
    public DatabaseResult(int columnCount, DatabaseValue... values) {
        this.columnCount = columnCount;
        this.values = new ArrayList<>(Arrays.asList(values));
    }

    /**
     * Creates a new DatabaseResult using the ResultSet from a SELECT statement.
     *
     * @param set The ResultSet to use.
     * @throws SQLException Thrown if a generic SQLException happens.
     */
    public DatabaseResult(ResultSet set) throws SQLException {
        this.values = new ArrayList<>();
        ResultSetMetaData meta = set.getMetaData();
        this.columnCount = meta.getColumnCount();
        while (set.next()) {
            for (int i = 1; i <= this.columnCount; i++) {
                values.add(new DatabaseValue(meta.getColumnName(i), set.getObject(i)));
            }
        }
    }

    /**
     * Attempts to tie each row of this {@link com.visualfiredev.javabase.DatabaseResult} to the specified object's
     * non-transient fields by creating new instances of it and reflexively applying values.
     *
     * <p>
     *     This method should only be used when you are building the TableSchema and ColumnSchema manually. It
     *     does not have advanced functionality and will attempt to parse ALL fields of a class to the
     *     corresponding columns in this DatabaseResult. **This method ignores case**, so if your fields
     *     are called `id` and the column in the database is `Id`, it will still map it.
     *
     *     In the case that it fails, it will still return the instances of the objects, however you
     *     will notice that none of the values have mapped. This is a limitation of this method and
     *     is not going to be fixed.
     *
     *     It is strongly recommended to use the annotation system instead.
     * </p>
     *
     * TODO: Link to annotation system
     *
     * @param clazz The class to create a new instance from.
     * @param <T> The type that the ArrayList should be casted to at the end.
     * @return An ArrayList of all of the created objects with the corresponding values inserted.
     */
    public <T> ArrayList<T> toObject(Class<T> clazz) throws Exception {
        // Fetch Constructor
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor();
            constructor.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new Exception("Class " + clazz.getSimpleName() + " must have a blank constructor!", e);
        }

        // Get Fields
        HashMap<String, Field> columns = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            columns.put(field.getName().toLowerCase(), field);
        }

        // Map Fields & Objects
        ArrayList<T> objects = new ArrayList<>();
        for (int i = 1; i <= this.getRowCount(); i++) {
            DatabaseValue[] row = this.getValuesForRow(i);

            T instance;
            try {
                instance = constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new Exception("There was an internal error while trying to create a new instance of " + clazz.getSimpleName() + ".", e);
            }

            for (DatabaseValue value : row) {
                // TODO: This is wasteful, why call it every row? I need to filter this on the first
                //       iteration and then use the full array on further iterations.
                if (columns.containsKey(value.getColumnName().toLowerCase())) {
                    Field field = columns.get(value.getColumnName().toLowerCase());
                    field.set(instance, value.getData());
                }
            }

            objects.add(instance);
        }

        return objects;
    }

    /**
     * Filters the values and fetches all values for the specified column name.
     * @param columnName The column to fetch values for.
     * @return The array of values for the column specified.
     */
    public DatabaseValue[] getValuesForColumn(String columnName) {
        return values.parallelStream().filter(e -> e.getColumnName().equals(columnName)).toArray(DatabaseValue[]::new);
    }

    /**
     * Filters the values and fetches the values for the specified row index (not 0-based, 1-based).
     *
     * @param row The row (1-based) to fetch values for.
     * @return The array of {@link com.visualfiredev.javabase.DatabaseValue}s for the corresponding row.
     * @throws Exception Thrown if the row input is less than one and greater than the row count.
     */
    public DatabaseValue[] getValuesForRow(int row) throws Exception {
        if (row <= 0 || row > this.getRowCount()) {
            throw new Exception("Row input is less than one and greater than the row count!");
        }

        DatabaseValue[] rowValues = new DatabaseValue[this.columnCount];
        for (int i = 0; i < this.columnCount; i++) {
            rowValues[i] = values.get(this.columnCount * (row - 1) + i);
        }
        return rowValues;
    }

    /**
     * Returns all values as the were inserted into the array.<br>
     * <p>
     *     Generally, the value of arrays is generated in the same order as the columns are
     *     in the database, however this is not always the case, so do not rely on this as
     *     a consistent result.
     *
     *     If you wish to have that functionality, please feel free to make a pull request.
     * </p>
     * @return The array of all the values for this result.
     */
    public DatabaseValue[] getValues() {
        return values.parallelStream().toArray(DatabaseValue[]::new);
    }

    /**
     * Returns the column count for this DatabaseResult.
     * @return The column count for this DatabaseResult.
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Calculates the row count for this DatabaseResult.
     * @return The calculation for the row count for this DatabaseResult.
     */
    public int getRowCount() {
        return values.size() / columnCount;
    }

}
