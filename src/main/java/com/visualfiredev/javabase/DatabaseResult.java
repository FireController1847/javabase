package com.visualfiredev.javabase;

import com.visualfiredev.javabase.schema.TableSchema;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

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
     * Attempts to tie each row in the database (or an array of DatabaseValues) to the non-transient
     * fields in the specified class, creating a new instance for each row.
     *
     * <p>
     *     This method utilizes the {@link DatabaseValue#toObject(TableSchema, DatabaseValue[], Object)} method, which
     *     will apply an array of DatabaseValues to an object by attempting to map the field names to the specified
     *     {@link TableSchema}. Because of this, this method may throw various exceptions relating to reflection if there
     *     is no alignment between the TableSchema and the specified class object.
     *
     *     **This is a case-insensitive operation, so column `EXAMPLE` will be mapped to a field named `example`.**
     * </p>
     *
     * @param tableSchema The {@link TableSchema} that should be used to determine the columns from the object.
     * @param clazz The class in which new instances should be created from for mapping.
     * @param <T> The class that the object is an instance of.
     * @return An ArrayList of new instances of the specified object.
     * @throws Exception Generically thrown if something goes wrong.
     */
    public <T> ArrayList<T> toObjects(TableSchema tableSchema, Class<T> clazz) throws Exception {
        // Fetch Constructor
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor();
            constructor.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new Exception("Class " + clazz.getSimpleName() + " must have a blank constructor!", e);
        }

        // Map Fields & Objects
        ArrayList<T> objects = new ArrayList<>();
        for (int i = 1; i <= this.getRowCount(); i++) {
            DatabaseValue[] row = this.getValuesForRow(i);

            // Create Instance
            T instance;
            try {
                instance = constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new Exception("There was an internal error while trying to create a new instance of " + clazz.getSimpleName() + ".", e);
            }

            // Map Instance
            T object = DatabaseValue.toObject(tableSchema, row, instance);
            objects.add(object);
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
