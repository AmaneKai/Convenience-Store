package com.konbini.service.tax;

public class VATTaxStrategy implements TaxStrategy {
    private static final double TAX_RATE = 0.12; // 12% VAT
    
    @Override
    public String getName() {
        return "Value Added Tax (VAT)";
    }
    
    @Override
    public double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }
}
