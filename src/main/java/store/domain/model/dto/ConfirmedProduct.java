package store.domain.model.dto;

import store.domain.model.product.Product;

import java.util.List;

public class ConfirmedProduct {

    private final List<Product> products;
    private final int userRequestSize;

    private ConfirmedProduct(List<Product> products, int userRequestSize) {
        this.products = products;
        this.userRequestSize = userRequestSize;
    }

    public static ConfirmedProduct of(List<Product> products, int userRequestSize) {
        return new ConfirmedProduct(products, userRequestSize);
    }

    public boolean isPromotionActive() {
        return this.getProduct().isPromotionActive();
    }

    public int getPromotionStock() {
        return products.stream()
                .filter(Product::isPromotionActive)
                .mapToInt(Product::getCurrentQuantity)
                .sum();
    }

    public int getUserRequestSize() {
        return userRequestSize;
    }

    public boolean isPromoted() {
        return products.stream()
                .anyMatch(Product::isPromotedProduct);
    }

    public boolean isAvailablePromotion() {
        return isPromoted() && isPromotionActive();
    }

    public Product getProduct() {
        return this.products.getFirst();
    }

    public int getPromotionDefaultQuantity() {
        return this.products.getFirst().getPromotionDefaultQuantity();
    }
}
