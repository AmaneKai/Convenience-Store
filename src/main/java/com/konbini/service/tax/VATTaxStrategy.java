package com.konbini.service.tax;

/**
 * Concrete implementation of the TaxStrategy interface for calculating Value Added Tax (VAT).
 * This strategy applies a fixed, standard VAT rate (12%) to the transaction subtotal.
 */
public class VATTaxStrategy implements TaxStrategy {
    /**
     * The standard VAT rate (12%) applied to transactions.
     */
    private static final double TAX_RATE = 0.12; // 12% VAT

    /**
     * {@inheritDoc}
     * Returns "Value Added Tax (VAT)" as the tax name.
     */
    @Override
    public String getName() {
        return "Value Added Tax (VAT)";
    }

    /**
     * {@inheritDoc}
     * Calculates VAT as 12% of the transaction subtotal.
     *
     * @param subtotal the transaction subtotal before tax
     * @return the VAT amount (12% of subtotal)
     */
    @Override
    public double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }
}