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
        return product.getPrice() * getSize();
    }

    public boolean inNotPromoted() {
        return !this.product.isPromotedProduct();
    }

    public String printPurchased() {
        return String.format("%-5s\t\t\t%2d\t\t%-,5d\n", this.product.getName(), this.getSize(), this.calculateTotalPrice());
    }

    public String printGift() {
        return String.format("%-5s\t\t\t%-5d\n", this.product.getName(), this.getSize());
    }
}
