package store.domain.model.product;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.promotion.Promotion;

import java.time.LocalDate;

public class Product {

    private final String name;
    private final int price;
    private int quantity = 0;
    private final Promotion promotion;
    private final boolean isPromotedProduct;
    private final boolean isFromFile;

    private Product(String name, int price, int quantity, Promotion promotion, boolean isPromotedProduct, boolean isFromFile) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
        this.isPromotedProduct = isPromotedProduct;
        this.isFromFile = isFromFile;
    }

    public static Product of(String name, int price, int quantity, Promotion promotion, boolean isPromotedProduct, boolean isFromFile) {
        return new Product(name, price, quantity, promotion, isPromotedProduct, isFromFile);
    }

    public static Product createCopyOfProduct(Product first) {
        return new Product(first.name, first.price, 0, Promotion.createNone(), false, false);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getCurrentQuantity() {
        return this.quantity;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public boolean isPromotedProduct() {
        return this.isPromotedProduct;
    }

    public int getPromotionDefaultQuantity() {
        return this.promotion.getDefaultDefaultQuantity();
    }

    public boolean isPromotionActive() {
        return this.promotion.isAvailable(LocalDate.now());
    }

    @Override
    public String toString() {
        String promotionMessage = StoreConst.EMPTY;
        if (this.isPromotedProduct()) {
            promotionMessage = this.getPromotion().getPromotionName();
        }

        String quantityMessage = this.getCurrentQuantity() + StoreConst.QUANTITY_UNIT;
        if (this.getCurrentQuantity() == 0) {
            quantityMessage = StoreConst.NOT_INSTOCK_MSG;
        }

        return String.format(StoreConst.PRODUCT_FORMAT,
                this.name,
                this.getPrice(),
                quantityMessage,
                promotionMessage
        );
    }

    public void decreaseQuantity(int size) {
        if (this.quantity - size >= 0) {
            this.quantity -= size;
        }
    }

    public void decreaseAll() {
        this.quantity = 0;
    }

    public boolean isFromFile() {
        return this.isFromFile;
    }
}