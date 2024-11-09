package store.domain.model.store;

import store.domain.model.dto.*;

import java.util.List;

public class SuggestionService {

    public void adjustUserRequestQuantity(final ConfirmedProduct confirmedProduct) {
        SuggestionType suggestion = confirmedProduct.getSuggestionType();

        suggestion.adjustRequestSizeByAnswer(confirmedProduct);
    }

    public void suggest(final List<StoreSuggestion> defaultSuggestion) {
        defaultSuggestion.forEach(this::applySuggestion);
    }

    private void applySuggestion(final StoreSuggestion suggestion) {
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

    private static boolean isEligibleForAdditionalPurchase(final StoreSuggestion suggestion) {
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

    private static boolean isEligibleButNotCorrectQuantity(final int remainder, final int promotionDefaultSize,
                                                           final int stock, final int requestSize) {
        return remainder == promotionDefaultSize - 1 && stock >= requestSize + 1;
    }

    private static void applyAdditionalPurchaseOffer(final StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);
        suggestion.changeOfferSize(1);
    }

    private static boolean isInsufficientPromotionStock(final StoreSuggestion suggestion) {
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

    private static int calculateQuantityForAskUserNotAppliedPromotion(final int promotionAvailableStockQuantity,
                                                                      final int promotionDefaultSize,
                                                                      final int requestSize) {
        int noAvailablePromotionRemainder = promotionAvailableStockQuantity % promotionDefaultSize;
        int availablePromotionQuantity = promotionAvailableStockQuantity - noAvailablePromotionRemainder;
        int forAskUserToBuyQuantity = requestSize - availablePromotionQuantity;
        return forAskUserToBuyQuantity;
    }

    private static void applyInsufficientPromotionStockOffer(final StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);
    }

    private static boolean isExcessiveAdditionalPurchase(final StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionDefaultQuantity = suggestion.getPromotionDefaultQuantity();
        int userRequestSize = suggestion.getUserRequestSize();
        return userRequestSize % promotionDefaultQuantity == 1 && promotionDefaultQuantity == 3; // 2+1인데 1개, 4개, 7개와 같이 애매하게 들고오는 경우
    }

    private static void applyExcessiveAdditionalPurchaseOffer(final StoreSuggestion suggestion) {
        suggestion.changeOfferSize(1);
        suggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);
    }

    private static boolean isNotApplicable(final StoreSuggestion suggestion) {
        return suggestion.isAlreadySuggested() || !suggestion.isPromoted();
    }
}
