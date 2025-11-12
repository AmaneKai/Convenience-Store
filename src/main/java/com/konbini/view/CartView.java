package com.konbini.view;

import com.konbini.model.Cart;

/**
 * Defines the user interface contract specifically for managing and displaying the shopping cart.
 * It extends BaseView to inherit fundamental display and input capabilities.
 */
public interface CartView extends BaseView {
    /**
     * Displays the primary menu options available within the cart management section
     * (e.g., add item, remove item, view cart, checkout).
     */
    void displayCartMenu();

    /**
     * Prompts the user for and retrieves the selection from the cart menu.
     * The implementation must ensure the input is a valid menu option.
     *
     * @return The integer corresponding to the user's selected menu item.
     */
    int getCartMenuChoice();

    /**
     * Displays the current contents of the shopping cart, including all items,
     * quantities, prices, and calculated subtotals.
     *
     * @param cart The Cart object to be displayed.
     */
    void displayCart(Cart cart);
}
