package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.service.tax.TaxStrategy;
import com.konbini.service.tax.VATTaxStrategy;

public class TaxStrategyTest {
    
    @Test
    public void testVATTaxStrategy() {
        TaxStrategy vatStrategy = new VATTaxStrategy();
        
        double subtotal = 100.0;
        double expectedTax = 12.0; // 12% of 100.0
        
        assertEquals(expectedTax, vatStrategy.calculateTax(subtotal), 0.001);
        assertEquals("Value Added Tax (VAT)", vatStrategy.getName());
    }
}
