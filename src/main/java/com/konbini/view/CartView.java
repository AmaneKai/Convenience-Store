package com.konbini.view;

import com.konbini.dto.CartDTO;

/**
 * View interface for shopping cart operations and display.
 * Extends BaseView to provide cart-specific user interactions
 * including cart menu display and cart content visualization.
 */
public interface CartView extends BaseView {

    /**
     * Displays the shopping cart management menu to the user.
     * Typically includes options for adding items, removing items,
     * updating quantities, viewing cart, and checkout.
     */
    void displayCartMenu();

    /**
     * Gets the user's selection from the cart menu.
     *
     * @return the user's menu choice as an integer
     */
    int getCartMenuChoice();

    /**
     * Displays the contents of a shopping cart to the user.
     * Shows cart items, quantities, prices, subtotal, and customer information.
     *
     * @param cart the CartDTO containing cart information to display
     */
    void displayCart(CartDTO cart);
}