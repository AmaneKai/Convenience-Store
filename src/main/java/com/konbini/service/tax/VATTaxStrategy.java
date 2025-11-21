package com.konbini.service.tax;

/**
 * Concrete implementation of the TaxStrategy interface for calculating Value Added Tax (VAT).
 * This strategy applies a fixed, standard VAT rate (12%) to the transaction subtotal.
 */
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
