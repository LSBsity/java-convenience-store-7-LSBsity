package store.domain.model.store.servce.suggestion;

import store.domain.model.dto.*;

import java.util.List;

public class SuggestionService {

    public void adjustUserRequestQuantity(final ConfirmedProduct confirmedProduct) {
        confirmedProduct.adjustRequestSizeByAnswer();
    }

    public void suggest(final List<StoreSuggestion> defaultSuggestion) {
        defaultSuggestion.forEach(this::applySuggestion);
    }

    private void applySuggestion(final StoreSuggestion suggestion) {
        if (isEligibleForAdditionalPurchase(suggestion)) {
            suggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);
            suggestion.changeOfferSize(1);
        }
        if (isInsufficientPromotionStock(suggestion)) {
            suggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);
        }
        if (isExcessiveAdditionalPurchase(suggestion)) {
            suggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);
            suggestion.changeOfferSize(1);
        }
    }

    private static boolean isEligibleForAdditionalPurchase(final StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity();
        int requestSize = suggestion.getUserRequestSize();
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();
        int remainder = requestSize % promotionDefaultSize;

        if (isAlreadyEligible(remainder, promotionAvailableStockQuantity, requestSize)) {
            suggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);
            return false;
        }
        return isEligible(remainder, promotionDefaultSize) && isEnoughStock(promotionAvailableStockQuantity, requestSize);
    }

    private static boolean isInsufficientPromotionStock(final StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity(); // 행사 재고
        int requestSize = suggestion.getUserRequestSize();                                     // 유저 요청 재고
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();                   // 행사 디폴트 사이즈

        int remainder = requestSize % promotionDefaultSize;
        if (isAlreadyEligible(remainder, promotionAvailableStockQuantity, requestSize)) {
            suggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);
            return false;
        }

        if (promotionAvailableStockQuantity <= requestSize) { // 프로모션 재고가 부족하다면
            int forAskUserToBuyQuantity = getPossibleQuantity(promotionAvailableStockQuantity, promotionDefaultSize, requestSize);
            suggestion.changeOfferSize(forAskUserToBuyQuantity);
            return true;
        }

        return false;
    }

    private static int getPossibleQuantity(final int promotionAvailableStockQuantity,
                                                                 final int promotionDefaultSize,
                                                                 final int requestSize) {
        int noAvailablePromotionRemainder = promotionAvailableStockQuantity % promotionDefaultSize;
        int availablePromotionQuantity = promotionAvailableStockQuantity - noAvailablePromotionRemainder;
        int forAskUserToBuyQuantity = requestSize - availablePromotionQuantity;
        return forAskUserToBuyQuantity;
    }

    private static boolean isExcessiveAdditionalPurchase(final StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int promotionDefaultQuantity = suggestion.getPromotionDefaultQuantity();
        int userRequestSize = suggestion.getUserRequestSize();
        return userRequestSize % promotionDefaultQuantity == 1 && promotionDefaultQuantity == 3; // 2+1인데 1개, 4개, 7개와 같이 애매하게 들고오는 경우
    }

    private static boolean isNotApplicable(final StoreSuggestion suggestion) {
        return suggestion.isAlreadySuggested() || !suggestion.isPromoted();
    }


    private static boolean isAlreadyEligible(final int remainder, final int promotionAvailableStockQuantity, final int requestSize) {
        return remainder == 0 && promotionAvailableStockQuantity >= requestSize;
    }

    private static boolean isEnoughStock(final int promotionAvailableStockQuantity, final int requestSize) {
        return promotionAvailableStockQuantity >= requestSize + 1;
    }

    private static boolean isEligible(final int remainder, final int promotionDefaultSize) {
        return remainder == promotionDefaultSize - 1;
    }
}
