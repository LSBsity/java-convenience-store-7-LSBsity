package store.domain.model.store;

import store.domain.model.dto.*;

import java.util.List;

public class SuggestionService {

    public void adjustUserRequestQuantity(ConfirmedProduct confirmedProduct) {
        SuggestionType suggestion = confirmedProduct.getSuggestionType();

        suggestion.adjustRequestSizeByAnswer(confirmedProduct);
    }

    public void suggest(List<StoreSuggestion> defaultSuggestion) {
        defaultSuggestion.forEach(this::applySuggestion);
    }

    private void applySuggestion(StoreSuggestion suggestion) {
        if (isEligibleForAdditionalPurchase(suggestion)) {
            applyAdditionalPurchaseOffer(suggestion);
        }
        if (isInsufficientPromotionStock(suggestion)) {
            applyInsufficientPromotionStockOffer(suggestion);
        }
        if (isExcessiveAdditionalPurchase(suggestion)) {
            applyExcessiveAdditionalPurchaseOffer(suggestion);
        }
    }

    private static boolean isEligibleForAdditionalPurchase(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity();
        int requestSize = suggestion.getUserRequestSize();
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();
        int remainder = requestSize % promotionDefaultSize;

        if (remainder == 0 && promotionAvailableStockQuantity >= requestSize) {
            suggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);
            return false;
        }
        return isEligibleButNotCorrectQuantity(remainder, promotionDefaultSize, promotionAvailableStockQuantity, requestSize);
    }

    private static boolean isEligibleButNotCorrectQuantity(int remainder, int promotionDefaultSize, int stock, int requestSize) {
        return remainder == promotionDefaultSize - 1 && stock >= requestSize + 1;
    }

    private static void applyAdditionalPurchaseOffer(StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);
        suggestion.changeOfferSize(1);
    }

    private static boolean isInsufficientPromotionStock(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity(); // 행사 재고
        int requestSize = suggestion.getUserRequestSize();                                     // 유저 요청 재고
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();                   // 행사 디폴트 사이즈

        int remainder = requestSize % promotionDefaultSize;
        if (remainder == 0 && promotionAvailableStockQuantity >= requestSize) {
            suggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);
            return false;
        }

        if (promotionAvailableStockQuantity <= requestSize) { // 수량이 맞아떨어지지 않는데 재고도 없으면
            int forAskUserToBuyQuantity = calculateQuantityForAskUserNotAppliedPromotion(promotionAvailableStockQuantity, promotionDefaultSize, requestSize);
            suggestion.changeOfferSize(forAskUserToBuyQuantity);
            return true;
        }
        return false;
    }

    private static int calculateQuantityForAskUserNotAppliedPromotion(int promotionAvailableStockQuantity, int promotionDefaultSize, int requestSize) {
        int noAvailablePromotionRemainder = promotionAvailableStockQuantity % promotionDefaultSize;
        int availablePromotionQuantity = promotionAvailableStockQuantity - noAvailablePromotionRemainder;
        int forAskUserToBuyQuantity = requestSize - availablePromotionQuantity;
        return forAskUserToBuyQuantity;
    }

    private static void applyInsufficientPromotionStockOffer(StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);
    }

    private static boolean isExcessiveAdditionalPurchase(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionDefaultQuantity = suggestion.getPromotionDefaultQuantity();
        int userRequestSize = suggestion.getUserRequestSize();
        return userRequestSize % promotionDefaultQuantity == 1 && promotionDefaultQuantity == 3; // 2+1인데 1개, 4개, 7개와 같이 애매하게 들고오는 경우
    }

    private static void applyExcessiveAdditionalPurchaseOffer(StoreSuggestion suggestion) {
        suggestion.changeOfferSize(1);
        suggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);
    }

    private static boolean isNotApplicable(StoreSuggestion suggestion) {
        return suggestion.isAlreadySuggested() || !suggestion.isPromoted();
    }
}
