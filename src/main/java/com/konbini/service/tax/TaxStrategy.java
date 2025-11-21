package com.konbini.service.tax;

public interface TaxStrategy {

    String getName();

    double calculateTax(double subtotal);
}
