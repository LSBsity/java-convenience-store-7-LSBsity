package store.domain.model.store;


import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.Suggestion;
import store.domain.model.promotion.UserAnswer;

public class StockService {

    public void updateStock(ConfirmedProduct confirmedProduct) {
        StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
        UserAnswer userAnswer = confirmedProduct.getUserAnswer();

        additionalFreeProductHandling(storeSuggestion);
        insufficientPromotionStockHandling(storeSuggestion, userAnswer);
        excessiveAdditionalPurchaseHandling(storeSuggestion, userAnswer);
        alreadyEligibleHandling(storeSuggestion);
        noneHandling(storeSuggestion);
    }

    private static void noneHandling(StoreSuggestion storeSuggestion) {
        if (storeSuggestion.getSuggestion() != Suggestion.NONE) return;

        int userRequestSize = storeSuggestion.getUserRequestSize();
        int normalProductStockQuantity = storeSuggestion.normalProductStockQuantity();
        if (!storeSuggestion.isActive() && normalProductStockQuantity < userRequestSize) {
            storeSuggestion.decreaseAllNormalStock();
            storeSuggestion.decreasePromotionStock(userRequestSize - normalProductStockQuantity);
        }
        if (normalProductStockQuantity >= userRequestSize) {
            storeSuggestion.decreaseNormalStock(userRequestSize);
        }
    }

    private static void alreadyEligibleHandling(StoreSuggestion storeSuggestion) {
        if (storeSuggestion.getSuggestion() != Suggestion.ALREADY_ELIGIBLE) return;

        storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
    }

    private static void excessiveAdditionalPurchaseHandling(StoreSuggestion storeSuggestion, UserAnswer userAnswer) {
        if (storeSuggestion.getSuggestion() != Suggestion.EXCESSIVE_ADDITIONAL_PURCHASE) return;
        if (userAnswer == UserAnswer.NO) {
            storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
            return;
        }
        storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize() - 1);
        storeSuggestion.decreaseNormalStock(1);
    }

    private static void insufficientPromotionStockHandling(StoreSuggestion storeSuggestion, UserAnswer userAnswer) {
        if (storeSuggestion.getSuggestion() != Suggestion.INSUFFICIENT_PROMOTION_STOCK) return;
        Values stockValues = Values.getValues(storeSuggestion);
        if (userAnswer == UserAnswer.YES) {
            storeSuggestion.decreasePromotionStock(stockValues.maximum());
            if (stockValues.normalDecrease() > storeSuggestion.normalProductStockQuantity()) {
                storeSuggestion.decreaseAllNormalStock();
                storeSuggestion.decreasePromotionStock(stockValues.normalDecrease() - stockValues.normalStockQuantity());
                return;
            }
            storeSuggestion.decreaseNormalStock(stockValues.normalDecrease());
            return;
        }
        storeSuggestion.decreasePromotionStock(stockValues.maximum());
    }


    private static void additionalFreeProductHandling(StoreSuggestion storeSuggestion) {
        if (storeSuggestion.getSuggestion() != Suggestion.ADDITIONAL_FREE_PRODUCT) return;

        storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
    }
}
