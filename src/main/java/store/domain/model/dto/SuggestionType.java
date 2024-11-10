package store.domain.model.dto;

import store.common.constant.StoreConst;
import store.domain.model.promotion.UserAnswer;

public enum SuggestionType {

    /**
     * 1+1, 2+1 제품에서 무료로 적용 가능한 개수를 맞춰 가지고 오지 않는 경우 <br> ex) 2+1 제품 -> 2개 들고 옴
     */
    ADDITIONAL_FREE_PRODUCT(StoreConst.ADDITIONAL_FREE_PRODUCT_MSG, true) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            if (confirmedProduct.getUserAnswer() == UserAnswer.NO) return; // 1개 무료 안 받음

            confirmedProduct.increaseUserRequestSize(); // 1+1, 2+1 적용
        }

        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
            storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
        }

    },
    /**
     * 프로모션 재고가 부족하여 1+1 (2개), 2+1 (3개)와 같은 혜택을 온전히 받을 수 없는 경우 <br> ex) 2+1콜라 재고 2개 -> 고객이 2+1콜라 3개 가져옴
     */
    INSUFFICIENT_PROMOTION_STOCK(StoreConst.INSUFFICIENT_PROMOTION_STOCK_MSG, true) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            if (confirmedProduct.getUserAnswer() == UserAnswer.YES) return; // 프로모션 안돼도 그냥 현재 요청만큼 구매

            int nonDiscountableQuantity = confirmedProduct.getOfferSize();
            int discountableQuantity = confirmedProduct.getUserRequestSize() - nonDiscountableQuantity;
            confirmedProduct.changeUserRequestSize(discountableQuantity);
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
            UserAnswer userAnswer = confirmedProduct.getUserAnswer();

            if (userAnswer == UserAnswer.YES) {
                decreaseInsufficientPromotionStockByUserAnswer(storeSuggestion);
                return;
            }
            int possiblePromotionQuantity = getPossiblePromotionQuantity(storeSuggestion);
            storeSuggestion.decreasePromotionStock(possiblePromotionQuantity);
        }

    },
    /**
     * 프로모션 2+1 제품이지만 ADDITIONAL_FREE_PRODUCT를 받지 못하는 경우 짝을 맞춰 구매할 수 있도록 함 <br>
     * ex) 2+1콜라 4, 7, 10 . .개를 들고올 시 3, 6, 9개로 변경 제안
     */
    EXCESSIVE_ADDITIONAL_PURCHASE(StoreConst.EXCESSIVE_ADDITIONAL_PURCHASE_MSG, true) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            if (confirmedProduct.getUserAnswer() == UserAnswer.YES) return; // 프로모션 안돼도 그냥 현재 요청만큼 구매

            int discountableQuantity = confirmedProduct.getUserRequestSize() - 1;
            confirmedProduct.changeUserRequestSize(discountableQuantity);
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
            UserAnswer userAnswer = confirmedProduct.getUserAnswer();

            if (userAnswer == UserAnswer.NO) {
                storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
                return;
            }

            storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize() - 1);
            storeSuggestion.decreaseNormalStock(1);
        }

    },
    /**
     * 프로모션 만큼 재고를 알맞게 들고 온 경우
     */
    ALREADY_ELIGIBLE(StoreConst.EMPTY_MSG, false) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            // No Action
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
            storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
        }

    },
    /**
     * 프로모션이 아닌 물건인 경우
     */
    NONE(StoreConst.EMPTY_MSG, false) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            // No Action
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();

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

    };

    /**
     * 편의점이 제시한 제안을 고객의 수락과 거절에 따른 고객 구매 요청 개수 변경
     */
    public abstract void adjustRequestSizeByAnswer(ConfirmedProduct confirmedProduct);

    /**
     * 고객의 응답과 프로모션 재고 별 수량에 따른 재고 차감 처리
     */
    public abstract void updateStock(ConfirmedProduct confirmedProduct);

    private final String format;
    private final boolean shouldPrint;

    SuggestionType(String format, boolean shouldPrint) {
        this.format = format;
        this.shouldPrint = shouldPrint;
    }

    public String getFormat() {
        return format + StoreConst.SPACE + StoreConst.YES_OR_NO;
    }

    public boolean isShouldPrint() {
        return shouldPrint;
    }


    // enum overide method에서 추출한 메서드들
    private static void decreaseInsufficientPromotionStockByUserAnswer(final StoreSuggestion storeSuggestion) {
        int promotionStock = storeSuggestion.getPromotionAvailableStockQuantity();
        int userRequestSize = storeSuggestion.getUserRequestSize();

        if (promotionStock >= userRequestSize) { //프로모션 재고가 유저 요청 개수보다 많으면
            storeSuggestion.decreasePromotionStock(userRequestSize);
            return;
        }
        int remainder = userRequestSize - promotionStock;
        storeSuggestion.decreasePromotionStock(promotionStock);
        storeSuggestion.decreaseNormalStock(remainder);
    }

    private static int getPossiblePromotionQuantity(final StoreSuggestion storeSuggestion) {
        int defaultQuantity = storeSuggestion.getPromotionDefaultQuantity();
        int promotionAvailableStockQuantity = storeSuggestion.getPromotionAvailableStockQuantity();
        int avail = promotionAvailableStockQuantity / defaultQuantity * defaultQuantity;
        return avail;
    }


}
