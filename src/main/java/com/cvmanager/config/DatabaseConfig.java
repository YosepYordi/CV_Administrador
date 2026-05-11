package com.cvmanager.config;

import com.cvmanager.utils.DBConnection;

public final class DatabaseConfig {
    private DatabaseConfig() {}
    public static String databaseName() {
        return DBConnection.property("db.name", "cvmanager_db");
    }
}
