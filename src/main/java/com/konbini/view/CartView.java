package com.konbini.view;

import com.konbini.model.Cart;

public interface CartView extends BaseView {
    void displayCartMenu();
    int getCartMenuChoice();
    void displayCart(Cart cart);
}
