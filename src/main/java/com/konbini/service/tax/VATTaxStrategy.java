package com.konbini.service.tax;

/**
 * Concrete implementation of the TaxStrategy interface for calculating Value Added Tax (VAT).
 * This strategy applies a fixed, standard VAT rate (12%) to the transaction subtotal.
 */
public class VATTaxStrategy implements TaxStrategy {
    /**
     * The fixed VAT rate used for calculation (12%).
     */
    private static final double TAX_RATE = 0.12; // 12% VAT

    /**
     * Retrieves the descriptive name of this tax strategy.
     *
     * @return The string "Value Added Tax (VAT)".
     */
    @Override
    public String getName() {
        return "Value Added Tax (VAT)";
    }

    /**
     * Calculates the VAT amount by multiplying the transaction subtotal by the fixed tax rate.
     *
     * @param subtotal The transaction subtotal (the base amount for tax calculation).
     * @return The calculated VAT amount.
     */
    @Override
    public double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }
}
