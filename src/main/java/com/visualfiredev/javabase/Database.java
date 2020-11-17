package com.visualfiredev.javabase;

import com.visualfiredev.javabase.schema.ColumnSchema;
import com.visualfiredev.javabase.schema.TableSchema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * A generic database that can be used for all database types.
 */
public class Database {

    // Constructor Arguments
    private String prefix;
    private String host;
    private String database;
    private DatabaseType type;

    // Connection
    private Connection connection;

    /**
     * Constructs a new database.
     *
     * @param prefix A prefix that will be prepended to every table.
     * @param host The IP address of which this database is being hosted. Use 'localhost' for a local database.
     * @param database The name of the database on a MySQL or MariaDB server, or the file path of an SQLite file.
     * @param type The type of database being used.
     */
    public Database(@NotNull String prefix, @NotNull String host, @NotNull String database, @NotNull DatabaseType type) {
        this.prefix = prefix;
        this.host = host;
        this.database = database;
        this.type = type;
    }

    /**
     * Constructs a new database with no table prefix.
     *
     * @param host The IP address of which this database is being hosted. Use 'localhost' for a local database.
     * @param database The name of the database on a MySQL or MariaDB server, or the file path of an SQLite file.
     * @param type The type of database being used.
     */
    public Database(@NotNull String host, @NotNull String database, @NotNull DatabaseType type) {
        this("", host, database, type);
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
        }
    }

    /**
     * Creates a table (depending on if it exists or not) using the specified {@link com.visualfiredev.javabase.schema.TableSchema}.
     * TODO: Make this return a "table" object and not void
     *
     * @param tableSchema The {@link com.visualfiredev.javabase.schema.TableSchema} the table should be based on.
     * @throws NotConnectedException Thrown if there is no connection to the database.
     * @throws SQLException Thrown if creating the table failed.
     * @throws UnsupportedDatabaseTypeException Thrown if any {@link com.visualfiredev.javabase.schema.ColumnSchema}'s do not support this type of database.
     */
    public void createTable(TableSchema tableSchema) throws NotConnectedException, SQLException, UnsupportedDatabaseTypeException {
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
            throw new SQLException("Invalid Table Schema! SQL Statement Created: " + sql, e);
        }
    }

    // TODO: Create table like? God I have so much to work do...

    /**
     * Returns true if the database is connected, otherwise returns false.
     * TODO: Cache the result of isValid, that way we can use this in every request made.
     *       Simply caching for five seconds could drastically improve execution time.
     *       Of course, that'll mean that there is a period of time where SQL requests
     *       may timeout instead of isConnected returning false, but I think that's a
     *       sacrifice I'm willing to make for the sake of performance.
     *
     * @throws SQLException Thrown if a generic access SQL error occurs.
     * @return True if the database is connected, otherwise returns false.
     */
    public boolean isConnected() throws SQLException {
        if (connection != null && connection.isValid(3)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the prefix that is appended to every table on this database.
     * @return The prefix that is appended to every table on this database.
     */
    @NotNull
    public String getPrefix() {
        return prefix;
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
