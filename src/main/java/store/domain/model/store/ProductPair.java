package store.domain.model.store;

import store.domain.model.product.Product;

public class ProductPair {

    private final Product product;
    private final int size;

    public ProductPair(Product product, int size) {
        this.product = product;
        this.size = size;
    }

    public Product getProduct() {
        return product;
    }

    public int getPrice() {
        return this.product.getPrice();
    }

    public int getSize() {
        return size;
    }

    public int calculateTotalPrice() {
        return product.getPrice() * size;
    }

    public boolean inNotPromoted() {
        return !this.product.isPromotedProduct();
    }
}
