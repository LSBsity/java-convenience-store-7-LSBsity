package store.domain.model.store;

import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.Suggestion;

public class PromotionService {

    public void applyPromotions(StoreSuggestion suggestion) {
        if (isEligibleForAdditionalPurchase(suggestion)) {
            System.out.println("PromotionService.applyPromotions");
            applyAdditionalPurchaseOffer(suggestion);
        }
        if (isInsufficientPromotionStock(suggestion)) {
            System.out.println("PromotionService.applyPromotions");
            applyInsufficientPromotionStockOffer(suggestion);
        }
    }

    private boolean isEligibleForAdditionalPurchase(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity();
        int requestSize = suggestion.getUserRequestSize();
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();
        int remainder = requestSize % promotionDefaultSize;

        if (remainder == 0 && promotionAvailableStockQuantity >= requestSize) {
            suggestion.changeSuggestion(Suggestion.ALREADY_ELIGIBLE);
            return false;
        }
        return isEligibleButNotCorrectQuantity(remainder, promotionDefaultSize, promotionAvailableStockQuantity, requestSize);
    }

    private boolean isEligibleButNotCorrectQuantity(int remainder, int promotionDefaultSize, int stock, int requestSize) {
        return remainder == promotionDefaultSize - 1 && stock >= requestSize + 1;
    }

    private void applyAdditionalPurchaseOffer(StoreSuggestion suggestion) {
        suggestion.changeSuggestion(Suggestion.ADDITIONAL_FREE_PRODUCT);
        suggestion.changeOfferSize(1);
    }

    private boolean isInsufficientPromotionStock(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity();
        int requestSize = suggestion.getUserRequestSize();
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();
        int remainder = requestSize % promotionDefaultSize;

        if (remainder == 0 && promotionAvailableStockQuantity >= requestSize) {
            suggestion.changeSuggestion(Suggestion.ALREADY_ELIGIBLE);
            return false;
        }

        if (promotionAvailableStockQuantity <= requestSize) {
            int forAskUserToBuyQuantity = calculateQuantityForAskUserNotAppliedPromotion(
                    promotionAvailableStockQuantity, promotionDefaultSize, requestSize);
            suggestion.changeOfferSize(forAskUserToBuyQuantity);
            return true;
        }

        return false;
    }

    private int calculateQuantityForAskUserNotAppliedPromotion(int promotionAvailableStockQuantity, int promotionDefaultSize, int requestSize) {
        int noAvailablePromotionRemainder = promotionAvailableStockQuantity % promotionDefaultSize;
        int availablePromotionQuantity = promotionAvailableStockQuantity - noAvailablePromotionRemainder;
        return requestSize - availablePromotionQuantity;
    }

    private void applyInsufficientPromotionStockOffer(StoreSuggestion suggestion) {
        suggestion.changeSuggestion(Suggestion.INSUFFICIENT_PROMOTION_STOCK);
    }

    private boolean isNotApplicable(StoreSuggestion suggestion) {
        return suggestion.isAlreadySuggested() || !suggestion.isPromoted();
    }
}