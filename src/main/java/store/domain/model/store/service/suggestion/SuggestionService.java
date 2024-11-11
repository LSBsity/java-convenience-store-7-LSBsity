package store.domain.model.store.service.suggestion;

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
        if (isEligibleForAdditionalFreeProduct(suggestion))
            applyAdditionalFreeProduct(suggestion);

        if (isInsufficientPromotionStock(suggestion))
            applyInsufficientPromotionStock(suggestion);

        if (isExcessiveAdditionalPurchase(suggestion))
            applyExcessiveAdditionalPurchase(suggestion);
    }

    private static void applyExcessiveAdditionalPurchase(final StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);
        suggestion.changeOfferSize(1);
    }

    private static void applyInsufficientPromotionStock(final StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);
    }

    private static void applyAdditionalFreeProduct(final StoreSuggestion suggestion) {
        suggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);
        suggestion.changeOfferSize(1);
    }

    private static boolean isEligibleForAdditionalFreeProduct(final StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int remainder = getRemainder(suggestion);
        if (isAlreadyEligible(remainder, suggestion)) {
            suggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);
            return false;
        }
        return isEligible(remainder, suggestion.getPromotionDefaultQuantity()) && isEnoughStock(suggestion);
    }

    private static int getRemainder(final StoreSuggestion suggestion) {
        return suggestion.getUserRequestSize() % suggestion.getPromotionDefaultQuantity();
    }

    private static boolean isInsufficientPromotionStock(final StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) return false;

        int remainder = getRemainder(suggestion);
        if (isAlreadyEligible(remainder, suggestion)) {
            suggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);
            return false;
        }
        return isNotEnoughPromotionStock(suggestion);
    }

    private static boolean isNotEnoughPromotionStock(final StoreSuggestion suggestion) {
        int promotionAvailableStock = suggestion.getPromotionAvailableStockQuantity();
        int requestSize = suggestion.getUserRequestSize();
        if (promotionAvailableStock > requestSize) return false; // 프로모션 재고가 부족하지 않다면

        int forAskUserToBuyQuantity = getPossibleQuantity(promotionAvailableStock, suggestion.getPromotionDefaultQuantity(), requestSize);
        suggestion.changeOfferSize(forAskUserToBuyQuantity);
        return true;
    }

    private static int getPossibleQuantity(final int promotionAvailableStock, final int promotionDefaultSize, final int requestSize) {
        int noAvailablePromotionRemainder = promotionAvailableStock % promotionDefaultSize;
        int availablePromotionQuantity = promotionAvailableStock - noAvailablePromotionRemainder;
        int forAskUserToBuyQuantity = requestSize - availablePromotionQuantity;
        return forAskUserToBuyQuantity;
    }

    private static boolean isExcessiveAdditionalPurchase(final StoreSuggestion storeSuggestion) {
        if (isNotApplicable(storeSuggestion)) return false;

        int promotionDefaultQuantity = storeSuggestion.getPromotionDefaultQuantity();
        int userRequestSize = storeSuggestion.getUserRequestSize();
        return userRequestSize % promotionDefaultQuantity == 1 && promotionDefaultQuantity == 3; // 2+1인데 1개, 4개, 7개와 같이 애매하게 들고오는 경우
    }

    private static boolean isNotApplicable(final StoreSuggestion suggestion) {
        return suggestion.isAlreadySuggested() || !suggestion.isPromoted();
    }


    private static boolean isAlreadyEligible(final int remainder, final StoreSuggestion storeSuggestion) {
        return remainder == 0 && storeSuggestion.getPromotionAvailableStockQuantity() >= storeSuggestion.getUserRequestSize();
    }

    private static boolean isEnoughStock(final StoreSuggestion storeSuggestion) {
        return storeSuggestion.getPromotionAvailableStockQuantity() >= storeSuggestion.getUserRequestSize() + 1;
    }

    private static boolean isEligible(final int remainder, final int promotionDefaultSize) {
        return remainder == promotionDefaultSize - 1;
    }
}
