package store.domain.model.dto;

import store.common.constant.StoreConst;

public enum Suggestion {

    ADDITIONAL_FREE_PRODUCT(StoreConst.ADDITIONAL_FREE_PRODUCT_MSG, true),
    INSUFFICIENT_PROMOTION_STOCK(StoreConst.INSUFFICIENT_PROMOTION_STOCK_MSG, true),
    EXCESSIVE_ADDITIONAL_PURCHASE(StoreConst.EXCESSIVE_ADDITIONAL_PURCHASE_MSG, true),
    ALREADY_ELIGIBLE(StoreConst.EMPTY_MSG, false),
    NONE(StoreConst.EMPTY_MSG, false),
    ;

    private final String format;
    private final boolean shouldPrint;

    Suggestion(String format, boolean shouldPrint) {
        this.format = format;
        this.shouldPrint = shouldPrint;
    }

    public String getFormat() {
        return format + " " + StoreConst.YES_OR_NO;
    }

    public boolean isShouldPrint() {
        return shouldPrint;
    }
}
