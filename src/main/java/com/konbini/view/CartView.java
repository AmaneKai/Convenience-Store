package com.konbini.view;

import com.konbini.dto.CartDTO;

/**
 * Defines the user interface contract specifically for managing and displaying the shopping cart.
 * It extends BaseView to inherit fundamental display and input capabilities.
 * 
 * IMPORTANT: This interface uses CartDTO exclusively - no model imports.
 * Controllers are responsible for converting Cart models to CartDTOs.
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
     * @param cart The CartDTO object to be displayed.
     */
    void displayCart(CartDTO cart);
}