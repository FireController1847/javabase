package com.visualfiredev.javabase;

/**
 * Defines the different types of databases this library can handle and their corresponding drivers.
 */
public enum DatabaseType {

    /**
     * Adds support for MySQL and the corresponding driver. Requires the MySQL connector.<br>
     * The recommended connector is <a href="https://github.com/mysql/mysql-connector-j" target="_blank">MySQL Connector J</a>.
     */
    MySQL("com.mysql.jdbc.Driver"),

    /**
     * Adds support for MariaDB and the corresponding driver. Requires the MariaDB connector.<br>
     * The recommended connector is <a href="https://github.com/mariadb-corporation/mariadb-connector-j" target="_blank">MariaDB Connector J</a>.
     */
    MariaDB("org.mariadb.jdbc.Driver"),

    /**
     * Adds support for SQLite and the corresponding driver. Requires the SQLite connector.<br>
     * The recommended connector is <a href="https://github.com/xerial/sqlite-jdbc" target="_blank">SQlite JDBC</a>.
     */
    SQLite("org.sqlite.JDBC");

    // Instance Variables
    private String driver;

    // Constructor
    DatabaseType(String driver) {
        this.driver = driver;
    }

    // Getters

    /**
     * Returns the string defining the class location for this driver.
     * @return The string defining the class location of this driver.
     */
    public String getDriver() {
        return driver;
    }

}
