package com.visualfiredev.javabase;

import com.visualfiredev.javabase.schema.ColumnSchema;
import com.visualfiredev.javabase.schema.TableSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * A generic database that can be used for all database types.
 * TODO: SQLite doesn't cache stuff for us, so we need to add some sort of transaction cache to improve speed.
 */
public class Database {

    // Constructor Arguments
    private String host;
    private String database;
    private DatabaseType type;

    // Connection
    private Connection connection;
    private long lastConnectionCheck;
    private boolean isConnected;

    /**
     * Constructs a new database.
     *
     * @param host The IP address of which this database is being hosted. Use 'localhost' for a local database.
     * @param database The name of the database on a MySQL or MariaDB server, or the file path of an SQLite file.
     * @param type The type of database being used.
     */
    public Database(@NotNull String host, @NotNull String database, @NotNull DatabaseType type) {
        this.host = host;
        this.database = database;
        this.type = type;
    }

    /**
     * Returns an already existing connection or creates one with the specified username and password.
     *
     * @param username If a MySQL or MariaDB server, the username to login with. Ignored if the database type is SQLite.
     * @param password If a MySQL or MariaDB server, the password to login with. Ignored if the database type is SQLite.
     * @throws MissingDriverException Thrown if the connector for the specified database type is not installed.
     * @throws ConnectionFailedException Thrown if the connection to the database has failed.
     * @throws IOException Thrown if the SQLite file fails to create.
     * @throws SQLException Thrown if a generic SQL access error occurs.
     * @return The existing connection or creates a new one.
     */
    public Database connect(@NotNull String username, @NotNull String password) throws MissingDriverException, ConnectionFailedException, SQLException, IOException {
        // Check For Existing Connection
        if (connection != null && this.isConnected()) {
            return this;
        }

        // TODO: Add official support for SQLite and MySQL

        // Load Driver
        ClassLoader classloader = this.getClass().getClassLoader();
        Driver driver;
        try {
            driver = (Driver) classloader.loadClass(type.getDriver()).getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new MissingDriverException(type);
        }

        // Create Properties for MySQL and MariaDB
        Properties properties = new Properties();
        if (type == DatabaseType.MySQL || type == DatabaseType.MariaDB) {
            properties.put("autoReconnect", true); // TODO: Make optional
            properties.put("user", username);
            properties.put("password", password);
            properties.put("useSSL", "false"); // TODO: SSL support
        }

        // Create Location
        String location = "//" + host + "/" + database;
        if (type == DatabaseType.SQLite) {
            location = database;

            // Create file while we're here
            new File(database).createNewFile();
        }

        // Connect
        try {
            connection = driver.connect("jdbc:" + type.toString().toLowerCase() + ":" + location, properties);
        } catch (SQLException e) {
            throw new ConnectionFailedException(e);
        }

        // Enable Auto Commit
        // TODO: Make this optional
        connection.setAutoCommit(true);

        // Return This
        return this;
    }

    /**
     * Returns an already existing connection or creates one with a blank username and password.
     *
     * @throws MissingDriverException Thrown if the connector for the specified database type is not installed.
     * @throws ConnectionFailedException Thrown if the connection to the database has failed.
     * @throws IOException Thrown if the SQLite file fails to create.
     * @throws SQLException Thrown if a generic access SQL error occurs.
     * @return The existing connection or creates a new one.
     */
    public Database connect() throws MissingDriverException, ConnectionFailedException, SQLException, IOException {
        return connect("", "");
    }

    /**
     * Disconnects from the database.
     *
     * @throws SQLException Thrown if closing the connection failed.
     */
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * Checks if the specified table already exists.
     *
     * @param tableSchema The TableSchema to use to determine if it exists.
     * @return True if it exists, otherwise false.
     * @throws SQLException Thrown if there an issue checking if it exists.
     */
    public boolean doesTableExist(TableSchema tableSchema) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet set = meta.getTables(null, null, tableSchema.getName(), null);
        int count = 0;
        while (set.next()) {
            count++;
        }
        return count > 0;
    }

    /**
     * Creates a table (depending on if it exists or not) using the specified {@link com.visualfiredev.javabase.schema.TableSchema}.
     * TODO: Make this return a "table" object and not void
     *
     * @param tableSchema The {@link com.visualfiredev.javabase.schema.TableSchema} the table should be based on.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if creating the table failed.
     * @throws UnsupportedDatabaseTypeException Thrown if any {@link com.visualfiredev.javabase.schema.ColumnSchema}'s do not support this type of database.
     * @throws UnsupportedFeatureException Thrown if a feature was enabled that this database does not support.
     */
    public void createTable(TableSchema tableSchema) throws NotConnectedException, SQLException, UnsupportedDatabaseTypeException, UnsupportedFeatureException {
        // Ensure Connected
        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        // Create Statement
        Statement statement = connection.createStatement();

        // Create SQL
        String sql = tableSchema.toString(type);

        System.out.println(sql);

        // Execute
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Invalid TableSchema or possible library error! SQL Statement Created: " + sql, e);
        }
    }

    // TODO: Create table like? God I have so much to work do...

    /**
     * Drops a table from the database.
     *
     * @param tableSchema The table to be dropped.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if dropping the table failed.
     */
    public void dropTable(TableSchema tableSchema) throws NotConnectedException, SQLException {
        // Ensure Connected
        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        // Create Statement
        Statement statement = connection.createStatement();

        // Create SQL
        String sql = "DROP TABLE " + tableSchema.getName();

        System.out.println(sql);

        // Execute
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Invalid TableSchema or possible library error! SQL Statement Created: " + sql, e);
        }
    }

    /**
     * Inserts data into the specified table using the {@link com.visualfiredev.javabase.schema.TableSchema} and the specified {@link com.visualfiredev.javabase.DatabaseValue}s.
     *
     * @param tableSchema The table that this data should be inserted to.
     * @param values The {@link com.visualfiredev.javabase.DatabaseValue}s that should be inserted.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if running the generated SQL statement failed.
     */
    public void insert(TableSchema tableSchema, DatabaseValue... values) throws NotConnectedException, SQLException {
        // Ensure Connected
        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        // Create Statement
        Statement statement = connection.createStatement();

        // Create SQL
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableSchema.getName());

        // Validate Column Names
        for (DatabaseValue value : values) {
            if (tableSchema.getColumn(value.getColumnName()) == null) {
                throw new SQLException("Invalid column name provided!");
            }
        }

        // Add Columns
        sql.append("(");
        for (int i = 0; i < values.length; i++) {
            sql.append(values[i].getColumnName());

            // Comma? Are there more?
            if (i != values.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        // Add Values
        sql.append(" VALUES(");
        for (int i = 0; i < values.length; i++) {
            Object data = values[i].getData();
            if (data instanceof String) {
                sql.append("'").append(String.valueOf(data).replace("'", "\\'")).append("'");
            } else {
                sql.append(data);
            }

            // Comma? Are there more?
            if (i != values.length - 1) {
                sql.append(", ");
            }
        }

        // Close
        sql.append(");");

        System.out.println(sql);

        // Execute
        try {
            statement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            throw new SQLException("Invalid TableSchema, DatabaseValues, or possible library error! SQL Statement Created: " + sql, e);
        }
    }

    /**
     * Inserts data into the specified table using a {@link DatabaseObject}.
     *
     * @param object The {@link DatabaseObject} that contains the values that should be inserted.
     * @throws Exception Thrown if there is an error while mapping values for the DatabaseObject.
     */
    public void insert(DatabaseObject object) throws Exception {
        this.insert(object.getTableSchema(), object.toValues());
    }

    /**
     * Selects data from the database using the specified expression with the specified limit.
     *
     * <p>
     *     It is virtually impossible to provide cross-compatible statements for a "WHERE" expression,
     *     so it is passed as **RAW SQL** in this method.
     *
     *     Please keep in mind that the WHERE statement is dependent on which database you are using, so
     *     this method is **not cross-compatible**. Please ensure you know which database you are using,
     *     likely using the {@link Database#getType()} method, when using the "WHERE" clause.
     *
     *     If you only want to select certain rows, use the {@link TableSchema#clone()} method and remove
     *     the columns you do not want to include in the result, or create a new {@link TableSchema} with the name
     *     and columns you want to include. They do not need to be exact copies, as long as the names
     *     are exactly the same as what is in the database.
     *
     *     If you wish not to include a "WHERE" expression, use {@link Database#selectAll(TableSchema)} instead.
     *
     *     Since this is a simple library, at the moment we do not provide functionality to JOIN or select
     *     from multiple tables without having multiple select statements. This may change in the future.
     * </p>
     *
     * @param tableSchema The table and columns to select from.
     * @param where The platform-dependent SQL statement for a "WHERE" clause.
     * @param limit The limit of the results. Set to -1 to disable.
     * @return A DatabaseResult.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if running the generated SQL statement failed.
     */
    public DatabaseResult select(TableSchema tableSchema, String where, int limit) throws NotConnectedException, SQLException {
        // Ensure Connected
        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        // Create Statement
        Statement statement = connection.createStatement();

        // Create SQL
        StringBuilder sql = new StringBuilder("SELECT ");

        // Add Columns
        ArrayList<ColumnSchema> columns = tableSchema.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i).getName());

            // Comma? Are there more?
            if (i != columns.size() - 1) {
                sql.append(", ");
            }
        }

        // From The Table
        sql.append(" FROM ").append(tableSchema.getName());

        // Where...
        sql.append(" WHERE ").append(where);

        // Limit
        if (limit > -1) {
            sql.append(" LIMIT ").append(limit);
        }

        System.out.println(sql);

        // Execute
        ResultSet set;
        try {
            set = statement.executeQuery(sql.toString());
        } catch (SQLException e) {
            throw new SQLException("Invalid TableSchema or possible library error! SQL Statement Created: " + sql, e);
        }

        // Create DatabaseResult & Return
        return new DatabaseResult(set);
    }

    /**
     * Selects data from the database using the specified expression with a limit of 100.
     * See {@link Database#select(TableSchema, String, int)} for more information.
     *
     * @param tableSchema The table and columns to select from.
     * @param where The platform-dependent SQL statement for a "WHERE" clause.
     * @return A DatabaseResult.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if running the generated SQL statement failed.
     */
    public DatabaseResult select(TableSchema tableSchema, String where) throws NotConnectedException, SQLException {
        return select(tableSchema, where, 100);
    }

    /**
     * Selects data from the database using the specified expression and the specified limit, creating new instances of the specified class.
     * See {@link Database#select(TableSchema, String, int)} for more information.
     *
     * @param tableSchema The table and columns to select from.
     * @param where The platform-dependent SQL statement for a "WHERE" clause.
     * @param limit The limit of the results. Set to -1 to disable.
     * @param clazz The class to create new instances from.
     * @param <T> The type of object to be returned.
     * @return An ArrayList of the objects.
     * @throws Exception Thrown if there is an error while mapping values for the DatabaseObject.
     */
    public <T extends DatabaseObject> ArrayList<T> select(TableSchema tableSchema, String where, int limit, Class<T> clazz) throws Exception {
        return select(tableSchema, where, limit).toObjects(tableSchema, clazz);
    }

    /**
     * Selects data from the database using the specified expression and a limit of 100, creating new instances of the specified class.
     * See {@link Database#select(TableSchema, String, int)} for more information.
     *
     * @param tableSchema The table and columns to select from.
     * @param where The platform-dependent SQL statement for a "WHERE" clause.
     * @param clazz The class to create new instances from.
     * @param <T> The type of object to be returned.
     * @return An ArrayList of the objects.
     * @throws Exception Thrown if there is an error while mapping values for the DatabaseObject.
     */
    public <T extends DatabaseObject> ArrayList<T> select(TableSchema tableSchema, String where, Class<T> clazz) throws Exception {
        return select(tableSchema, where, 100).toObjects(tableSchema, clazz);
    }

    /**
     * Selects all the data from the table using the columns in the TableSchema stopping at the specified limit. Set the limit to -1 to disable.
     *
     * @param tableSchema The {@link com.visualfiredev.javabase.schema.TableSchema} that data should be selected from.
     * @param limit The limit. By default is 100.
     * @return A {@link com.visualfiredev.javabase.DatabaseResult}.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if running the generated SQL statement failed.
     */
    public DatabaseResult selectAll(TableSchema tableSchema, int limit) throws NotConnectedException, SQLException {
        // Ensure Connected
        if (!this.isConnected()) {
            throw new NotConnectedException();
        }

        // Create Statement
        Statement statement = connection.createStatement();

        // Create SQL
        StringBuilder sql = new StringBuilder("SELECT ");

        // Add Columns
        ArrayList<ColumnSchema> columns = tableSchema.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i).getName());

            // Comma? Are there more?
            if (i != columns.size() - 1) {
                sql.append(", ");
            }
        }

        // From The Table
        sql.append(" FROM ").append(tableSchema.getName());

        // Limit
        if (limit > -1) {
            sql.append(" LIMIT ").append(limit);
        }

        System.out.println(sql);

        // Execute
        ResultSet set;
        try {
            set = statement.executeQuery(sql.toString());
        } catch (SQLException e) {
            throw new SQLException("Invalid TableSchema or possible library error! SQL Statement Created: " + sql, e);
        }

        // Create DatabaseResult & Return
        return new DatabaseResult(set);
    }

    /**
     * Selects all the data from the table stopping at 100 results.
     *
     * @param tableSchema The {@link com.visualfiredev.javabase.schema.TableSchema} that data should be selected from.
     * @return A {@link com.visualfiredev.javabase.DatabaseResult}.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if running the generated SQL statement failed.
     */
    public DatabaseResult selectAll(TableSchema tableSchema) throws NotConnectedException, SQLException {
        return this.selectAll(tableSchema, 100);
    }

    /**
     * Selects all the data from the table stopping at the specified limit, mapping it to the specified class. Set the limit to -1 to disable.
     *
     * @param tableSchema The {@link com.visualfiredev.javabase.schema.TableSchema} that data should be selected from.
     * @param limit The limit. By default is 100.
     * @param clazz The class that all instances should be created from.
     * @param <T> The type of object to be returned.
     * @return An array of new {@link DatabaseObject} instances.
     * @throws Exception Thrown if there is an error while mapping values for the DatabaseObject.
     */
    public <T extends DatabaseObject> ArrayList<T> selectAll(TableSchema tableSchema, int limit, Class<T> clazz) throws Exception {
        return this.selectAll(tableSchema, limit).toObjects(tableSchema, clazz);
    }

    /**
     * Selects all the data from the table stopping at 100 results, mapping to to the specified class.
     *
     * @param tableSchema The {@link com.visualfiredev.javabase.schema.TableSchema} that data should be selected from.
     * @param clazz The class that all instances should be created from.
     * @param <T> The type of object to be returned.
     * @return An array of new {@link DatabaseObject} instances.
     * @throws Exception Thrown if there is an error while mapping values for the DatabaseObject.
     */
    public <T extends DatabaseObject> ArrayList<T> selectAll(TableSchema tableSchema, Class<T> clazz) throws Exception {
        return this.selectAll(tableSchema, 100).toObjects(tableSchema, clazz);
    }

    /**
     * Executes an SQL update directly on the connection. See {@link Statement#executeUpdate(String)}
     * 
     * @param sql The SQL update to be executed.
     * @throws SQLException Thrown if something goes wrong.
     */
    public void rawUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    /**
     * Executes an SQL query directly on the connection. See {@link Statement#executeQuery(String)}
     * 
     * @param sql The SQL query to be executed.
     * @return The Java ResultSet in response (optionally, turn it into a DatabaseResult by passing it in the constructor, ex `new DatabaseResult(resultSet)`).
     * @throws SQLException Thrown if something goes wrong.
     */
    public ResultSet rawQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * Executes an SQL statement directly on the connection. See {@link Statement#execute(String)}
     * 
     * @param sql The SQL statement to be executed.
     * @return Whether or not the statement succeeded.
     * @throws SQLException Thrown if something goes wrong.
     */
    public boolean raw(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.execute(sql);
    }

    /**
     * Returns true if the database is connected, otherwise returns false.
     * TODO: Cache the result of isValid, that way we can use this in every request made.
     *       Simply caching for five seconds could drastically improve execution time.
     *       Of course, that'll mean that there is a period of time where SQL requests
     *       may timeout instead of isConnected returning false, but I think that's a
     *       sacrifice I'm willing to make for the sake of performance.
     *
     * @return True if the database is connected, otherwise returns false.
     */
    public boolean isConnected() {

        // Connection Cache
        long now = new Date().getTime();
        if (lastConnectionCheck != 0 || now - lastConnectionCheck >= 5000) { // TODO: HARD-CODED 5000 MILLISECONDS CACHE
            lastConnectionCheck = now;

            try {
                isConnected = connection != null && connection.isValid(3);
            } catch (SQLException e) {
                isConnected = false;
            }
        }

        // Return IsConnected
        return isConnected;
    }

    /**
     * Returns the IP address that this database is being hosted on.
     * @return The IP address that this database is being hosted on.
     */
    @NotNull
    public String getHost() {
        return host;
    }

    /**
     * Returns the name of this database or the file location of this database.
     * @return The name of this database or the file location of this database.
     */
    @NotNull
    public String getDatabase() {
        return database;
    }

    /**
     * Returns the corresponding {@link com.visualfiredev.javabase.DatabaseType} of this database.
     * @return The corresponding {@link com.visualfiredev.javabase.DatabaseType} of this database.
     */
    @NotNull
    public DatabaseType getType() {
        return type;
    }

    /**
     * Returns the connection for this database.
     * @return The connection for this database.
     */
    @Nullable
    public Connection getConnection() {
        return connection;
    }

}
