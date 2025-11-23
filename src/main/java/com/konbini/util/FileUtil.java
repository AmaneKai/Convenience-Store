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

/**
 * Utility class for file and directory operations.
 * Provides common file system operations including directory creation,
 * file copying, backup creation, and path manipulation.
 */
public class FileUtil {

    /**
     * Creates a directory if it doesn't already exist.
     *
     * @param directoryPath the path of the directory to create
     * @return true if the directory exists or was successfully created, false otherwise
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        boolean temp = true;

        if (!directory.exists()) {
            temp = directory.mkdirs();
        }

        return temp;
    }

    /**
     * Copies a file from source to destination.
     * Creates destination directory if it doesn't exist and replaces existing files.
     *
     * @param sourceFile the source file to copy
     * @param destFile the destination file
     * @return true if the copy was successful, false otherwise
     */
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

    /**
     * Deletes a file from the file system.
     *
     * @param filePath the path of the file to delete
     * @return true if the file was successfully deleted, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * Checks if a file exists at the specified path.
     *
     * @param filePath the path to check
     * @return true if the file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Creates a backup of a file with today's date as suffix.
     * Backup files are named with the pattern: originalname.YYYY-MM-DD.bak
     *
     * @param filePath the path of the file to backup
     * @return true if the backup was successfully created, false otherwise
     */
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

    /**
     * Extracts the file extension from a filename.
     *
     * @param fileName the filename to extract extension from
     * @return the file extension, or empty string if no extension exists
     */
    public static String getFileExtension(String fileName) {
        String temp = "";
        int lastIndexOf = fileName.lastIndexOf(".");

        if (lastIndexOf != -1) {
            temp = fileName.substring(lastIndexOf + 1);
        }

        return temp;
    }

    /**
     * Gets the filename without its extension.
     *
     * @param fileName the filename to process
     * @return the filename without extension
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String temp = fileName;
        int lastIndexOf = fileName.lastIndexOf(".");

        if (lastIndexOf != -1) {
            temp = fileName.substring(0, lastIndexOf);
        }

        return temp;
    }

    /**
     * Ensures the receipts directory exists in the application's working directory.
     * Creates the directory if it doesn't exist.
     *
     * @return the absolute path to the receipts directory
     */
    public static String ensureReceiptsDirectory() {
        String directoryPath = System.getProperty("user.dir")
                + File.separator + "receipts";
        createDirectoryIfNotExists(directoryPath);
        return directoryPath;
    }

    /**
     * Ensures the data directory exists in the application's working directory.
     * Creates the directory if it doesn't exist.
     *
     * @return the absolute path to the data directory
     */
    public static String ensureDataDirectory() {
        String directoryPath = System.getProperty("user.dir")
                + File.separator + "data";
        createDirectoryIfNotExists(directoryPath);
        return directoryPath;
    }

    /**
     * Converts a relative path to an absolute path based on the working directory.
     *
     * @param relativePath the relative path to convert
     * @return the absolute path
     */
    public static String getAbsolutePath(String relativePath) {
        return System.getProperty("user.dir") + File.separator + relativePath;
    }

    /**
     * Combines multiple path elements into a single path string.
     * Handles proper separator placement between elements.
     *
     * @param elements the path elements to combine
     * @return the combined path string
     */
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