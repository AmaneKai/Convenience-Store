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
 * Utility class for file operations
 */
public class FileUtil {
    
   public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
    
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
    
   public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }
    
   public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
    
   public static boolean createBackup(String filePath) {
        if (!fileExists(filePath)) {
            return false;
        }
        
        try {
            String backupPath = filePath + "." + LocalDate.now()
                .format(DateTimeFormatter.ISO_DATE) + ".bak";
            Files.copy(Paths.get(filePath), Paths.get(backupPath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
   public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No extension
        }
        return fileName.substring(lastIndexOf + 1);
    }
    
   public static String getFileNameWithoutExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return fileName; // No extension
        }
        return fileName.substring(0, lastIndexOf);
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
        if (elements.length == 0) {
            return "";
        }
        
        StringBuilder path = new StringBuilder(elements[0]);
        
        for (int i = 1; i < elements.length; i++) {
            String element = elements[i];
            // Ensure we don't double up separators
            if (!path.toString().endsWith(File.separator) && !element.startsWith(File.separator)) {
                path.append(File.separator);
            }
            path.append(element);
        }
        
        return path.toString();
    }
}
