package com.konbini.service.tax;

/**
 * Strategy interface for implementing various tax calculation algorithms.
 * Allows for flexible tax calculation based on different tax rules,
 * rates, and regional requirements.
 *
 * Implementations of this interface can provide different types of tax
 * calculations such as VAT, sales tax, or region-specific taxes.
 */
public interface TaxStrategy {

    /**
     * Gets the display name of this tax strategy.
     * This name is typically used in receipts and transaction records
     * to identify the type of tax applied.
     *
     * @return the name of the tax strategy (e.g., "VAT", "Sales Tax")
     */
    String getName();

    /**
     * Calculates the tax amount based on the transaction subtotal.
     *
     * @param subtotal the transaction subtotal before taxes and discounts
     * @return the calculated tax amount
     */
    double calculateTax(double subtotal);
}