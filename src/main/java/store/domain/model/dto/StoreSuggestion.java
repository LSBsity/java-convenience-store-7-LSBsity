package store.domain.model.dto;

import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.Product;

import java.util.Collections;
import java.util.List;

public class StoreSuggestion {

    private final List<Product> products;
    private int userRequestSize;

    private Suggestion suggestion = Suggestion.NONE;
    private int offerSize = 0;

    public StoreSuggestion(List<Product> products, int requestSize) {
        this.products = products;
        this.userRequestSize = requestSize;
    }


    public int getUserRequestSize() {
        return userRequestSize;
    }

    public Suggestion getSuggestion() {
        return this.suggestion;
    }

    public int getOfferSize() {
        return this.offerSize;
    }

    public void changeUserRequestSize(int userRequestSize) {
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

    public void changeSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public void changeOfferSize(int offerSize) {
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
        return this.suggestion != Suggestion.NONE;
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
}
