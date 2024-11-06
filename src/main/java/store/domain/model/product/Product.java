package store.domain.model.product;

import store.common.constant.StoreConst;
import store.domain.model.promotion.Promotion;

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

    public static Product createCopyOfProduct(Product first) {
        return new Product(first.name, first.price, 0, Promotion.createNone(), false);
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

    @Override
    public String toString() {
        String promotionMessage = StoreConst.EMPTY;
        if (this.isPromotedProduct()) {
            promotionMessage = this.getPromotion().getPromotionName();
        }

        String quantityMessage = this.getQuantity() + StoreConst.QUANTITY_UNIT;
        if (this.getQuantity() == 0) {
            quantityMessage = StoreConst.NOT_INSTOCK_MSG;
        }

        return String.format("-%s %,d원 %s %s",
                this.name,
                this.getPrice(),
                quantityMessage,
                promotionMessage
        );
    }
}