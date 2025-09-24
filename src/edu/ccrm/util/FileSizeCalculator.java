package edu.ccrm.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileSizeCalculator {

    // Recursive method using Files.walk (preferred)
    public static long computeTotalSize(Path dir) {
        if (!Files.exists(dir)) return 0;
        try {
            return Files.walk(dir)
                .filter(Files::isRegularFile)
                .mapToLong(p -> {
                    try { return Files.size(p); }
                    catch (IOException e) { return 0; }
                })
                .sum();
        } catch (IOException e) {
            throw new RuntimeException("Error computing size", e);
        }
    }

    // Pure recursive version (demonstrates recursion)
    public static long computeRecursive(Path path) {
        if (Files.isRegularFile(path)) {
            try { return Files.size(path); }
            catch (IOException e) { return 0; }
        }
        if (!Files.isDirectory(path)) return 0;
        try {
            return Files.list(path)
                .mapToLong(FileSizeCalculator::computeRecursive)
                .sum();
        } catch (IOException e) {
            return 0;
        }
    }
}