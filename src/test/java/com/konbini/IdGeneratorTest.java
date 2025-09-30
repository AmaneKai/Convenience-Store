package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.util.IdGenerator;

import java.util.HashSet;
import java.util.Set;

public class IdGeneratorTest {
    
    @Test
    public void testGenerateId() {
        // Get the singleton instance
        IdGenerator idGenerator = IdGenerator.getInstance();
        
        // Generate some IDs
        String productId1 = idGenerator.generateId("product");
        String productId2 = idGenerator.generateId("product");
        String customerId1 = idGenerator.generateId("customer");
        
        // Verify ID format
        assertTrue(productId1.startsWith("PRO"));
        assertTrue(productId2.startsWith("PRO"));
        assertTrue(customerId1.startsWith("CUS"));
        
        // Verify IDs are sequential
        int productNum1 = Integer.parseInt(productId1.substring(3));
        int productNum2 = Integer.parseInt(productId2.substring(3));
        assertEquals(productNum1 + 1, productNum2);
        
        // Verify IDs are unique
        assertNotEquals(productId1, productId2);
        assertNotEquals(productId1, customerId1);
        assertNotEquals(productId2, customerId1);
    }
    
    @Test
    public void testGenerateMultipleIds() {
        // Get the singleton instance
        IdGenerator idGenerator = IdGenerator.getInstance();
        
        // Generate multiple IDs of the same type
        int count = 100;
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            ids.add(idGenerator.generateId("test"));
        }
        
        // Verify all IDs are unique
        assertEquals(count, ids.size());
        
        // Verify all IDs have the correct format
        for (String id : ids) {
            assertTrue(id.startsWith("TES"));
            assertEquals(7, id.length()); // "TES" + 4 digits
        }
    }
}
