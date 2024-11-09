package store.domain.model.store;

import store.domain.model.dto.StoreSuggestion;

public record Values(int promotionAvailableStockQuantity, int maximum, int normalDecrease, int normalStockQuantity) {
    static Values getValues(StoreSuggestion storeSuggestion) {
        int promotionAvailableStockQuantity = storeSuggestion.getPromotionAvailableStockQuantity(); // 총 프로모션 재고
        int defaultSize = storeSuggestion.getPromotionDefaultQuantity(); // 프로모션 디폴트 개수
        int maximum = promotionAvailableStockQuantity / defaultSize * defaultSize; // 프로모션 적용 가능한 최대 개수
        int userRequestSize = storeSuggestion.getUserRequestSize(); // 유저 구매 요청 개수
        int normalDecrease = userRequestSize - maximum; // 프로모션 재고에서 가능한 만큼 뺀 뒤 일반 재고에서 빼야 할 재고
        int normalStockQuantity = storeSuggestion.normalProductStockQuantity(); // 총 일반 재고

        Values stockValues = new Values(promotionAvailableStockQuantity, maximum, normalDecrease, normalStockQuantity);
        return stockValues;
    }
}