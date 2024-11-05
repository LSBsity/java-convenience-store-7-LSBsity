package store.domain.model.product;

import store.domain.model.promotion.Promotion;

import java.util.Objects;

public class Product {

    private final String name;
    private final int price;
    private int quantity = 0;
    private final Promotion promotion;
    private final boolean isPromotedProduct;

    private Product(String name, int price, int quantity, Promotion promotion, boolean isPromotedProduct) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
        this.isPromotedProduct = isPromotedProduct;
    }

    public static Product of(String name, int price, int quantity, Promotion promotion, boolean isPromotedProduct) {
        return new Product(name, price, quantity, promotion, isPromotedProduct);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public boolean isPromotedProduct() {
        return this.isPromotedProduct;
    }
}
