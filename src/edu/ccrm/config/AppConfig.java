package edu.ccrm.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
    private static volatile AppConfig instance;
    private final Path dataDir;

    private AppConfig() {
        this.dataDir = Paths.get("data");
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public Path getDataDir() {
        return dataDir;
    }
}