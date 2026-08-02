package com.konbini.domain.transaction.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Added Tax strategy at the standard 12% rate.
 */
public class VATTaxStrategy implements TaxStrategy {

    /**
     * The standard VAT rate applied to transactions.
     */
    public static final BigDecimal TAX_RATE = new BigDecimal("0.12");

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Value Added Tax (VAT)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
