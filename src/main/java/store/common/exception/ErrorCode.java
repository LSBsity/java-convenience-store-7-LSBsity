package store.common.exception;

import store.common.constant.StoreConst;

public enum ErrorCode {

    // file or format error
    FILE_PARSE_OR_PATH_ERROR(StoreConst.FILE_PARSE_OR_PATH_ERROR_MSG),

    // product
    PRODUCT_NOT_EXIST(StoreConst.PRODUCT_NOT_EXIST_MSG);

    private final String message;

    ErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return StoreConst.ERROR_PREFIX + message;
    }
}
