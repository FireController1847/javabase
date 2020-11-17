package com.visualfiredev.javabase;

/**
 * Contains an enum for every generic data type.
 * TODO: This is an incomplete list and needs to add support for far more data types.
 */
public enum DataType {

    /**
     * A null value. All databases support this value.<br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    NULL(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * An integer value. MySQL and MariaDB specify 4 bytes maximum. SQLite supports variable length up to 8 bytes.<br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    INTEGER(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * A float value. MySQL and MariaDB state this is synonymous with a DOUBLE. SQLite states this is synonymous with a REAL. It is an 8-byte IEEE floating point.<br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    FLOAT(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * A text value. This can be up to 16-bytes long for MySQL, MariaDB, and SQLite.
     * Supports MySQL, MariaDB, and SQLite.
     */
    TEXT(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * A binary value. In MySQL and MariaDB this is equivalent to a binary string, and can be up to 16 bytes long. In SQLite, it can be upwards of over 2.1 gigabytes and stores raw binary data.
     * Supports MySQL, MariaDB, and SQLite.
     */
    BLOB(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite);

    // Instance Variables
    private DatabaseType[] supportedTypes;

    // Constructor
    DataType(DatabaseType... supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    /**
     * Determines whether or not this DataType is valid for the corresponding DatabaseType.
     * @param typeIn The database type to compare.
     * @return True if this data type can be used in this database, false otherwise.
     */
    public boolean supportsDatabaseType(DatabaseType typeIn) {
        for (int i = 0; i < supportedTypes.length; i++) {
            if (supportedTypes[i] == typeIn) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the supported database types for this data type.
     * @return The supported database types for this data type.
     */
    public DatabaseType[] getSupportedTypes() {
        return supportedTypes;
    }

}
