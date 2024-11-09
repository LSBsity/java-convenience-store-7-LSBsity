package store.domain.model.dto;

import store.common.constant.StoreConst;
import store.domain.model.promotion.UserAnswer;

public enum SuggestionType {

    ADDITIONAL_FREE_PRODUCT(StoreConst.ADDITIONAL_FREE_PRODUCT_MSG, true) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            if (confirmedProduct.getUserAnswer() == UserAnswer.YES) {
                confirmedProduct.increaseUserRequestSize(); // 1+1, 2+1 적용
            }
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();

            storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
        }
    },
    INSUFFICIENT_PROMOTION_STOCK(StoreConst.INSUFFICIENT_PROMOTION_STOCK_MSG, true) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            if (confirmedProduct.getUserAnswer() == UserAnswer.NO) {
                int nonDiscountableQuantity = confirmedProduct.getOfferSize();
                int discountableQuantity = confirmedProduct.getUserRequestSize() - nonDiscountableQuantity;

                confirmedProduct.changeUserRequestSize(discountableQuantity);
            }
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
            UserAnswer userAnswer = confirmedProduct.getUserAnswer();

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
    },
    EXCESSIVE_ADDITIONAL_PURCHASE(StoreConst.EXCESSIVE_ADDITIONAL_PURCHASE_MSG, true) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            if (confirmedProduct.getUserAnswer() == UserAnswer.NO) {
                int discountableQuantity = confirmedProduct.getUserRequestSize() - 1;
                confirmedProduct.changeUserRequestSize(discountableQuantity);
            }
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
    ALREADY_ELIGIBLE(StoreConst.EMPTY_MSG, false) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            return;
        }

        @Override
        public void updateStock(final ConfirmedProduct confirmedProduct) {
            StoreSuggestion storeSuggestion = confirmedProduct.getStoreSuggestion();
            storeSuggestion.decreasePromotionStock(storeSuggestion.getUserRequestSize());
        }
    },
    NONE(StoreConst.EMPTY_MSG, false) {
        @Override
        public void adjustRequestSizeByAnswer(final ConfirmedProduct confirmedProduct) {
            return;
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

    private final String format;
    private final boolean shouldPrint;

    SuggestionType(String format, boolean shouldPrint) {
        this.format = format;
        this.shouldPrint = shouldPrint;
    }

    public String getFormat() {
        return format + " " + StoreConst.YES_OR_NO;
    }

    public boolean isShouldPrint() {
        return shouldPrint;
    }

    public abstract void adjustRequestSizeByAnswer(ConfirmedProduct confirmedProduct);

    public abstract void updateStock(ConfirmedProduct confirmedProduct);
}
