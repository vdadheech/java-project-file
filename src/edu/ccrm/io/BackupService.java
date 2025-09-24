package edu.ccrm.io;

import edu.ccrm.config.AppConfig;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupService {
    private final Path dataDir;
    private final Path backupRoot;

    public BackupService() {
        this.dataDir = AppConfig.getInstance().getDataDir();
        this.backupRoot = dataDir.resolve("backups");
        try {
            Files.createDirectories(backupRoot);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create backup dir", e);
        }
    }

    public Path backup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path backupDir = backupRoot.resolve("backup_" + timestamp);
        try {
            Files.createDirectories(backupDir);
            try (var paths = Files.walk(dataDir)) {
                paths.filter(Files::isRegularFile)
                    .filter(p -> !p.toString().contains("backups")) // avoid recursive backup
                    .forEach(source -> {
                        Path target = backupDir.resolve(dataDir.relativize(source));
                        try {
                            Files.createDirectories(target.getParent());
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Failed to copy: " + source);
                        }
                    });
            }
            System.out.println("Backup completed: " + backupDir);
            return backupDir;
        } catch (IOException e) {
            throw new RuntimeException("Backup failed", e);
        }
    }
}