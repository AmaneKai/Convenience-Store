package com.konbini.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtil {
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        boolean temp = true;

        if (!directory.exists()) {
            temp = directory.mkdirs();
        }

        return temp;
    }

    public static boolean copyFile(File sourceFile, File destFile) {
        boolean temp = false;

        if (sourceFile == null || destFile == null || !sourceFile.exists()) {
            return temp;
        }

        try {
            // Ensure destination directory exists
            File parentDir = destFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp = true;
        } catch (IOException e) {
            System.err.println("Failed to copy file: " + e.getMessage());
        }

        return temp;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean createBackup(String filePath) {
        boolean temp = false;

        if (fileExists(filePath)) {
            try {
                String backupPath = filePath + "." + LocalDate.now()
                        .format(DateTimeFormatter.ISO_DATE) + ".bak";
                Files.copy(Paths.get(filePath), Paths.get(backupPath));
                temp = true;
            } catch (IOException e) {
                System.err.println("Error creating backup of file: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            }
        }

        return temp;
    }

    public static String getFileExtension(String fileName) {
        String temp = "";
        int lastIndexOf = fileName.lastIndexOf(".");

        if (lastIndexOf != -1) {
            temp = fileName.substring(lastIndexOf + 1);
        }

        return temp;
    }

    public static String getFileNameWithoutExtension(String fileName) {
        String temp = fileName;
        int lastIndexOf = fileName.lastIndexOf(".");

        if (lastIndexOf != -1) {
            temp = fileName.substring(0, lastIndexOf);
        }

        return temp;
    }
    public static String ensureReceiptsDirectory() {
        String directoryPath = System.getProperty("user.dir")
                + File.separator + "receipts";
        createDirectoryIfNotExists(directoryPath);
        return directoryPath;
    }

    public static String ensureDataDirectory() {
        String directoryPath = System.getProperty("user.dir")
                + File.separator + "data";
        createDirectoryIfNotExists(directoryPath);
        return directoryPath;
    }

    public static String getAbsolutePath(String relativePath) {
        return System.getProperty("user.dir") + File.separator + relativePath;
    }

    public static String combinePath(String... elements) {
        StringBuilder path = new StringBuilder();
        int j = 0;

        if (elements.length > 0) {
            path.append(elements[0]);

            for (j = 1; j < elements.length; j++) {
                String element = elements[j];

                if (!path.toString().endsWith(File.separator) && !element.startsWith(File.separator)) {
                    path.append(File.separator);
                } else if (path.toString().endsWith(File.separator) && element.startsWith(File.separator)) {
                    element = element.substring(1);
                }

                path.append(element);
            }
        }

        return path.toString();
    }
}