package com.cvmanager.config;

import com.cvmanager.utils.DBConnection;

public final class AppConfig {
    private AppConfig() {}
    public static String institutionalDomain() {
        return DBConnection.property("institutional.email.domain", "@instituto.edu.pe");
    }
}
