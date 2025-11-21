package com.konbini.view;

import com.konbini.dto.CartDTO;

public interface CartView extends BaseView {
    void displayCartMenu();

    int getCartMenuChoice();

    void displayCart(CartDTO cart);
}