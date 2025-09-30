package com.konbini;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtilTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    public void testCreateDirectoryIfNotExists() {
        String dirPath = tempDir.resolve("testDir").toString();
        
        // Directory doesn't exist yet
        assertFalse(new File(dirPath).exists());
        
        // Create directory
        boolean created = FileUtil.createDirectoryIfNotExists(dirPath);
        
        // Verify directory was created
        assertTrue(created);
        assertTrue(new File(dirPath).exists());
        
        // Try creating it again - should return true but not recreate
        boolean createdAgain = FileUtil.createDirectoryIfNotExists(dirPath);
        assertTrue(createdAgain);
    }
    
    @Test
    public void testFileExists() {
        String filePath = tempDir.resolve("testFile.txt").toString();
        
        // File doesn't exist yet
        assertFalse(FileUtil.fileExists(filePath));
        
        // Create file
        try {
            Files.createFile(Path.of(filePath));
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        // Verify file exists
        assertTrue(FileUtil.fileExists(filePath));
    }
    
    @Test
    public void testGetFileExtension() {
        assertEquals("txt", FileUtil.getFileExtension("file.txt"));
        assertEquals("jpg", FileUtil.getFileExtension("image.jpg"));
        assertEquals("", FileUtil.getFileExtension("file"));
        assertEquals("gz", FileUtil.getFileExtension("archive.tar.gz"));
    }
    
    @Test
    public void testGetFileNameWithoutExtension() {
        assertEquals("file", FileUtil.getFileNameWithoutExtension("file.txt"));
        assertEquals("image", FileUtil.getFileNameWithoutExtension("image.jpg"));
        assertEquals("file", FileUtil.getFileNameWithoutExtension("file"));
        assertEquals("archive.tar", FileUtil.getFileNameWithoutExtension("archive.tar.gz"));
    }
    
    @Test
    public void testEnsureReceiptsDirectory() {
        String receiptsDir = FileUtil.ensureReceiptsDirectory();
        
        // Verify directory exists
        assertTrue(new File(receiptsDir).exists());
        assertTrue(new File(receiptsDir).isDirectory());
    }
    
    @Test
    public void testEnsureDataDirectory() {
        String dataDir = FileUtil.ensureDataDirectory();
        
        // Verify directory exists
        assertTrue(new File(dataDir).exists());
        assertTrue(new File(dataDir).isDirectory());
    }
}
