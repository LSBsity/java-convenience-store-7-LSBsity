package store.domain.model.dto;

import store.domain.model.product.Product;

import java.util.List;

public class ConfirmedWishList {

    private final List<Product> products;
    private final int userRequestSize;

    private ConfirmedWishList(List<Product> products, int userRequestSize) {
        this.products = products;
        this.userRequestSize = userRequestSize;
    }

    public static ConfirmedWishList of(List<Product> products, int userRequestSize) {
        return new ConfirmedWishList(products, userRequestSize);
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
}
