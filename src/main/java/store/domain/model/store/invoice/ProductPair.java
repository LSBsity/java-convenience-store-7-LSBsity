package store.domain.model.store.invoice;

import store.common.constant.InvoicePrintConst;
import store.domain.model.product.Product;

public class ProductPair {

    private final Product product;
    private final int size;

    private ProductPair(Product product, int size) {
        this.product = product;
        this.size = size;
    }

    public static ProductPair of(Product product, int size) {
        return new ProductPair(product, size);
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
        return String.format(InvoicePrintConst.PURCHASED_PRODUCT, this.product.getName(), this.getSize(), this.calculateTotalPrice());
    }

    public String printGift() {
        return String.format(InvoicePrintConst.GIFT_PRODUCT, this.product.getName(), this.getSize());
    }

    public boolean expired() {
        return !this.product.isPromotionActive();
    }
}
