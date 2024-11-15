package store.domain.model.dto;

import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.service.suggestion.SuggestionType;


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

    public void updateStock(ConfirmedProduct confirmedProduct) {
        SuggestionType suggestionType = this.getSuggestionType();
        suggestionType.updateStock(confirmedProduct);
    }

    public UserAnswer getUserAnswer() {
        return userAnswer;
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

    private boolean isPromoted() {
        return storeSuggestion.getProducts().stream()
                .anyMatch(Product::isPromotedProduct);
    }

    public Product getPromotionProduct() {
        return storeSuggestion.getProducts().stream().filter(Product::isPromotedProduct).findFirst().get();
    }

    public boolean isAvailablePromotion() {
        return isPromoted();
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

    public void changeUserRequestSize(final int size) {
        this.storeSuggestion.changeUserRequestSize(size);
    }

    public void adjustRequestSizeByAnswer() {
        this.getSuggestionType().adjustRequestSizeByAnswer(this);
    }
}
