package com.konbini.service.tax;

/**
 * Defines the contract for applying various tax calculations to a transaction.
 * Each implementation represents a specific tax rule (e.g., VAT, Sales Tax)
 * and is responsible for providing its name and calculating the tax amount
 * based on the transaction's subtotal.
 */
public interface TaxStrategy {
    /**
     * Retrieves the descriptive name of the tax strategy.
     * This name is used for record-keeping in the final Transaction object.
     *
     * @return The name of the tax (e.g., "Value Added Tax (VAT)").
     */
    String getName();

    /**
     * Calculates the monetary tax amount to be applied to a purchase.
     *
     * @param subtotal The transaction subtotal (the base amount on which tax is calculated).
     * @return The calculated tax amount in the local currency.
     */
    double calculateTax(double subtotal);
}
