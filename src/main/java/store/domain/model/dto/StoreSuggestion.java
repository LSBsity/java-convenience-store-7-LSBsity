package store.domain.model.dto;

import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.Product;

import java.util.Collections;
import java.util.List;

public class StoreSuggestion {

    private final List<Product> products;
    private int userRequestSize;

    private SuggestionType suggestion = SuggestionType.NONE;
    private int offerSize = 0;

    private StoreSuggestion(List<Product> products, int requestSize) {
        this.products = products;
        this.userRequestSize = requestSize;
    }

    public static StoreSuggestion of(List<Product> products, int userRequestSize) {
        return new StoreSuggestion(products, userRequestSize);
    }

    public int getUserRequestSize() {
        return userRequestSize;
    }

    public SuggestionType getSuggestionType() {
        return this.suggestion;
    }

    public int getOfferSize() {
        return this.offerSize;
    }

    public void changeUserRequestSize(final int userRequestSize) {
        this.userRequestSize = userRequestSize;
    }

    public void addUserRequestSize() {
        this.userRequestSize += 1;
    }

    public boolean isPromoted() {
        return products.stream()
                .anyMatch(Product::isPromotedProduct) && products.stream()
                .anyMatch(Product::isPromotionActive);
    }

    public boolean isActive() {
        return products.stream().anyMatch(Product::isPromotionActive);
    }

    public void changeSuggestion(final SuggestionType suggestion) {
        this.suggestion = suggestion;
    }

    public void changeOfferSize(final int offerSize) {
        this.offerSize = offerSize;
    }

    public int getPromotionDefaultQuantity() {
        return products.stream()
                .filter(Product::isPromotedProduct)
                .map(Product::getPromotionDefaultQuantity)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_PRODUCT_NOT_EXIST_ERROR));
    }

    public int getPromotionAvailableStockQuantity() {
        return products.stream()
                .filter(Product::isPromotedProduct)
                .map(Product::getCurrentQuantity)
                .findFirst()
                .orElse(0);
    }

    public boolean isAlreadySuggested() {
        return this.suggestion != SuggestionType.NONE;
    }

    public String getProductName() {
        return this.products.stream()
                .map(Product::getName)
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_ERROR));
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(this.products);
    }

    public int normalProductStockQuantity() {
        return this.products.stream()
                .filter(i -> !i.isPromotedProduct())
                .mapToInt(Product::getCurrentQuantity)
                .sum();
    }

    public void decreasePromotionStock(final int size) {
        Product promotionProduct = getPromotionProduct();
        promotionProduct.decreaseQuantity(size);
    }

    public void decreaseNormalStock(int size) {
        Product defaultProduct = getDefaultProduct();
        defaultProduct.decreaseQuantity(size);
    }

    public void decreaseAllNormalStock() {
        Product defaultProduct = getDefaultProduct();
        defaultProduct.decreaseAll();
    }

    private Product getPromotionProduct() {
        return this.products.stream()
                .filter(Product::isPromotedProduct)
                .findFirst()
                .get();
    }


    private Product getDefaultProduct() {
        return this.products.stream()
                .filter(product -> !product.isPromotedProduct())
                .findFirst()
                .get();
    }

}
