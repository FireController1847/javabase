package com.visualfiredev.javabase;

/**
 * Used whenever a database does not support a specific feature.
 */
public class UnsupportedFeatureException extends Exception {

    /**
     * Constructs a new UnsupportedFeatureException using the specified database type and feature string.
     *
     * @param databaseType The {@link com.visualfiredev.javabase.DatabaseType} that does not support this feature.
     * @param feature The feature that is not supported.
     */
    public UnsupportedFeatureException(DatabaseType databaseType, String feature) {
        super(databaseType.toString() + " does not support the following feature: " + feature);
    }

    /**
     * Constructs a new UnsupportedFeatureException using the specified feature string and no specific database type.
     *
     * @param feature The feature that Javabase does not support.
     */
    public UnsupportedFeatureException(String feature) {
        super("Unsupported feature! " + feature);
    }

}
