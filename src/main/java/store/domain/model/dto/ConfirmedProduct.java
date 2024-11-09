package store.domain.model.dto;

import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;


public class ConfirmedProduct {

    private final StoreSuggestion storeSuggestion;
    private final UserAnswer userAnswer;


    public ConfirmedProduct(StoreSuggestion storeSuggestion, UserAnswer userAnswer) {
        this.storeSuggestion = storeSuggestion;
        this.userAnswer = userAnswer;
    }

    public static ConfirmedProduct of(StoreSuggestion storeSuggestion, UserAnswer userAnswer) {
        return new ConfirmedProduct(storeSuggestion, userAnswer);
    }

    public StoreSuggestion getStoreSuggestion() {
        return storeSuggestion;
    }

    public SuggestionType getSuggestionType() {
        return this.storeSuggestion.getSuggestionType();
    }

    public UserAnswer getUserAnswer() {
        return userAnswer;
    }

    public boolean isPromotionActive() {
        return this.getProduct().isPromotionActive();
    }

    public int getPromotionStock() {
        return storeSuggestion.getProducts().stream()
                .filter(Product::isPromotionActive)
                .mapToInt(Product::getCurrentQuantity)
                .sum();
    }

    public int getUserRequestSize() {
        return storeSuggestion.getUserRequestSize();
    }

    public boolean isPromoted() {
        return storeSuggestion.getProducts().stream()
                .anyMatch(Product::isPromotedProduct);
    }

    public Product getPromotionProduct() {
        return storeSuggestion.getProducts().stream().filter(Product::isPromotedProduct).findFirst().get();
    }

    public boolean isAvailablePromotion() {
        return isPromoted() && isPromotionActive();
    }

    public Product getProduct() {
        return this.storeSuggestion.getProducts().getFirst();
    }

    public int getOfferSize() {
        return this.storeSuggestion.getOfferSize();
    }

    public int getPromotionDefaultQuantity() {
        return this.storeSuggestion.getProducts().getFirst().getPromotionDefaultQuantity();
    }

    public void increaseUserRequestSize() {
        this.storeSuggestion.addUserRequestSize();
    }

    public void changeUserRequestSize(int size) {
        this.storeSuggestion.changeUserRequestSize(size);
    }
}
