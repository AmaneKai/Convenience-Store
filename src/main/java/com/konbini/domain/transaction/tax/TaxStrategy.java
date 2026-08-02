package com.konbini.domain.transaction.tax;

import java.math.BigDecimal;

/**
 * Strategy contract for computing tax on a transaction subtotal.
 */
public interface TaxStrategy {

    /**
     * Returns the display name of this tax strategy.
     *
     * @return the tax name
     */
    String getName();

    /**
     * Computes the tax amount for a subtotal.
     *
     * @param subtotal the transaction subtotal
     * @return the tax amount
     */
    BigDecimal calculateTax(BigDecimal subtotal);
}
