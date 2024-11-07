package store.common.exception;

import store.common.constant.StoreConst;

public enum ErrorCode {

    // file or format error
    FILE_PARSE_OR_PATH_ERROR(StoreConst.FILE_PARSE_OR_PATH_ERROR_MSG),

    // product
    // user input
    WISH_PRODUCT_NOT_EXIST_ERROR(StoreConst.WISH_PRODUCT_NOT_EXIST_ERROR_MSG),
    WISH_PRODUCT_OUT_OF_STOCK_ERROR(StoreConst.WISH_PRODUCT_OUT_OF_STOCK_MSG),
    WISH_PRODUCT_INPUT_ERROR(StoreConst.WISH_PRODUCT_INPUT_ERROR_MSG),
    WISH_PRODUCT_INPUT_FORMAT_ERROR(StoreConst.WISH_PRODUCT_INPUT_FORAT_ERROR_MSG),

    USER_CONFIRM_INPUT_ERROR(StoreConst.USER_CONFIRM_INPUT_ERROR),
    ;
    private final String message;

    ErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return StoreConst.ERROR_PREFIX + message;
    }
}
