package com.visualfiredev.javabase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains an enum for every generic data type.
 * TODO: This is an incomplete list and needs to add support for far more data types.
 */
public enum DataType {

    /**
     * A null value. One of the five primary data types that all databases support. <br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    NULL(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * An integer value. One of the five primary data types that all databases support.
     * MySQL and MariaDB specify 4 bytes maximum. SQLite supports variable length up to 8 bytes. <br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    INTEGER(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-type.html" target="_blank">A bit value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    BIT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/integer-types.html" target="_blank">A tinyint value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    TINYINT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/integer-types.html" target="_blank">A smallint value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    SMALLINT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/integer-types.html" target="_blank">A mediumint value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    MEDIUMINT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/integer-types.html" target="_blank">A bigint value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    BIGINT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * A float value. One of the five primary data types that all databases support. <br>
     * SQLite states this is synonymous with a REAL. It is an 8-byte IEEE floating point. <br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    FLOAT(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/floating-point-types.html" target="_blank">A double value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    DOUBLE(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html" target="_blank">A decimal value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    DECIMAL(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html" target="_blank">A numeric value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    NUMERIC(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/datetime.html" target="_blank">A date value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    DATE(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/datetime.html" target="_blank">A datetime value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    DATETIME(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/datetime.html" target="_blank">A timestamp value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    TIMESTAMP(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/time.html" target="_blank">A time value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    TIME(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/year.html" target="_blank">A year value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    YEAR(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/char.html" target="_blank">A char value</a>. <br>
     * Is exactly the length as defined by the corresponding value. <br>
     * Supports MySQL and MariaDB.
     */
    CHAR(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/char.html" target="_blank">A varchar value</a>. <br>
     * Stored as variable length internally, supports up to the length as set by the corresponding value. <br>
     * Supports MySQL and MariaDB.
     */
    VARCHAR(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/binary-varbinary.html" target="_blank">A binary value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    BINARY(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/binary-varbinary.html" target="_blank">A varbinary value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    VARBINARY(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * A blob value. One of the five primary data types that all databases support. <br>
     * In MySQL and MariaDB this is equivalent to a binary string, and can be up to 16 bytes long. <br>
     * In SQLite, it can be upwards of over 2.1 gigabytes and stores raw binary data. <br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    BLOB(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html#data-types-storage-reqs-strings" target="_blank">A tinyblob value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    TINYBLOB(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html#data-types-storage-reqs-strings" target="_blank">A mediumblob value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    MEDIUMBLOB(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html#data-types-storage-reqs-strings" target="_blank">A longblob value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    LONGBLOB(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * A text value. One of the five primary data types that all databases support. <br>
     * This can be up to 16-bytes long for MySQL, MariaDB, and SQLite. <br>
     * Supports MySQL, MariaDB, and SQLite.
     */
    TEXT(DatabaseType.MySQL, DatabaseType.MariaDB, DatabaseType.SQLite),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html#data-types-storage-reqs-strings" target="_blank">A tinytext value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    TINYTEXT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html#data-types-storage-reqs-strings" target="_blank">A mediumtext value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    MEDIUMTEXT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html#data-types-storage-reqs-strings" target="_blank">A longtext value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    LONGTEXT(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/enum.html" target="_blank">An enum value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    ENUM(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/set.html" target="_blank">A set value</a>. <br>
     * Supports MySQL and MariaDB.
     */
    SET(DatabaseType.MySQL, DatabaseType.MariaDB),

    /**
     * <a href="https://dev.mysql.com/doc/refman/8.0/en/json.html" target="_blank">A json value</a>. <br>
     * MariaDB states this is equivalent to a LONGTEXT to add compatibility with MySQL. <br>
     * Supports MySQL and MariaDB.
     */
    JSON(DatabaseType.MySQL, DatabaseType.MariaDB);

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
    public boolean supportsDatabaseType(@NotNull DatabaseType typeIn) {
        for (int i = 0; i < supportedTypes.length; i++) {
            if (supportedTypes[i] == typeIn) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to parse a data type from the given string.
     *
     * @param value The string to be parsed.
     * @return The parsed DataType.
     * @throws UnsupportedDataTypeException Thrown if the string was unable to be parsed to a DataType.
     */
    @NotNull
    public static DataType parseDataType(@NotNull String value) throws UnsupportedDataTypeException {
        try {
            return DataType.valueOf(value);
        } catch (Exception e) {
            // Alias List!
            if (value.equals("INT")) {
                return DataType.INTEGER;
            } else if (value.equals("DEC") || value.equals("FIXED")) {
                return DataType.DECIMAL;
            } else if (value.equals("NUMBER")) {
                return DataType.DECIMAL;
            } else if (value.equals("BOOLEAN")) {
                return DataType.TINYINT;
            }
        }
        throw new UnsupportedDataTypeException(value);
    }

    /**
     * Returns the supported database types for this data type.
     * @return The supported database types for this data type.
     */
    @NotNull
    public DatabaseType[] getSupportedTypes() {
        return supportedTypes;
    }

}
