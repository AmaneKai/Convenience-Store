package com.konbini.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class providing static helper methods for common file system operations,
 * including directory management, file copying, deletion, path manipulation, and backups.
 */
public class FileUtil {

    /**
     * Checks if a directory exists, and if not, attempts to create it along with any necessary parent directories.
     *
     * @param directoryPath The path of the directory to check and create.
     * @return True if the directory already exists or was successfully created, false otherwise.
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    /**
     * Copies the content of one file to another using FileChannels for efficient transfer.
     * If the destination file does not exist, it is created.
     *
     * @param sourceFile The source file to copy from.
     * @param destFile The destination file to copy to.
     * @throws IOException if an I/O error occurs during file creation or transfer.
     */
    public static void copyFile(File sourceFile, File destFile)
        throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile)
            .getChannel();
             FileChannel destination = new FileOutputStream(destFile)
                 .getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    /**
     * Deletes the file located at the specified path.
     *
     * @param filePath The path of the file to delete.
     * @return True if the file was successfully deleted, false otherwise (e.g., file doesn't exist or permission denied).
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * Checks if a file or directory exists at the specified path.
     *
     * @param filePath The path to check.
     * @return True if the file or directory exists, false otherwise.
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Creates a backup copy of a specified file. The backup file is named by appending
     * the current date and ".bak" to the original file path.
     *
     * @param filePath The path of the file to back up.
     * @return True if the backup was created successfully, false if the original file does not exist or if an IOException occurs.
     */
    public static boolean createBackup(String filePath) {
        if (!fileExists(filePath)) {
            return false;
        }

        try {
            String backupPath = filePath + "." + LocalDate.now()
                .format(DateTimeFormatter.ISO_DATE) + ".bak";
            // Use Files.copy for simplicity and modern approach
            Files.copy(Paths.get(filePath), Paths.get(backupPath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extracts the extension from a file name.
     *
     * @param fileName The full name of the file (e.g., "data.json").
     * @return The file extension (e.g., "json"), or an empty string if no extension is found.
     */
    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No extension
        }
        return fileName.substring(lastIndexOf + 1);
    }

    /**
     * Extracts the file name without its extension.
     *
     * @param fileName The full name of the file (e.g., "data.json").
     * @return The file name without the extension (e.g., "data"), or the original name if no extension is found.
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return fileName; // No extension
        }
        return fileName.substring(0, lastIndexOf);
    }

    /**
     * Ensures the application's "receipts" directory exists in the current working directory.
     * If it doesn't exist, it attempts to create it.
     *
     * @return The absolute path to the receipts directory.
     */
    public static String ensureReceiptsDirectory() {
        String directoryPath = System.getProperty("user.dir")
            + File.separator + "receipts";
        createDirectoryIfNotExists(directoryPath);
        return directoryPath;
    }

    /**
     * Ensures the application's "data" directory exists in the current working directory.
     * This is typically used for persistent storage of customer, product, and transaction files.
     *
     * @return The absolute path to the data directory.
     */
    public static String ensureDataDirectory() {
        String directoryPath = System.getProperty("user.dir")
            + File.separator + "data";
        createDirectoryIfNotExists(directoryPath);
        return directoryPath;
    }

    /**
     * Converts a relative path (relative to the user's current working directory) to an absolute path.
     *
     * @param relativePath The path relative to the current working directory.
     * @return The absolute path.
     */
    public static String getAbsolutePath(String relativePath) {
        return System.getProperty("user.dir") + File.separator + relativePath;
    }

    /**
     * Combines multiple path elements into a single, correctly-formatted path string,
     * ensuring proper usage of the system's file separator between elements.
     *
     * @param elements A variable list of strings representing parts of the path.
     * @return The combined path string.
     */
    public static String combinePath(String... elements) {
        if (elements.length == 0) {
            return "";
        }


        StringBuilder path = new StringBuilder(elements[0]);

        for (int i = 1; i < elements.length; i++) {
            String element = elements[i];
            // Ensure we don't double up separators and add one if missing
            if (!path.toString().endsWith(File.separator) && !element.startsWith(File.separator)) {
                path.append(File.separator);
            } else if (path.toString().endsWith(File.separator) && element.startsWith(File.separator)) {
                 // Remove leading separator from the element if path already has one

                element = element.substring(1);
            }
            path.append(element);
        }

        return path.toString();
    }
}
