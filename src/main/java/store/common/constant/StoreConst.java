package store.common.constant;

public class StoreConst {

    public static final String EMPTY = "";
    public static final String ERROR_PREFIX = "[ERROR] ";

    public static final int PRODUCT_COLUMN_SIZE = 4;
    public static final int PROMOTION_COLUMN_SIZE = 5;

    public static final String NOT_INSTOCK_MSG = "재고 없음";
    public static final String QUANTITY_UNIT = "개";

    public static final String WISH_LIST_INPUT_REGEX = "\\[(.+?)-\\d+]((,\\[(.+?)-\\d+])*)";
    public static final String WISH_LIST_INPUT_COMPILE_REGEX = "\\[(.*?)-(\\d+)]";

    public static final String PROMOTIONS_FILE_PATH = "src/main/resources/promotions.md";
    public static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";

    public static final String FILE_PARSE_OR_PATH_ERROR_MSG = "파일의 경로가 올바르지 않거나 형식이 올바르지 않습니다.";
    public static final String FILE_PARSE_DELIMETER = ",";

    public static final String WELCOME_MSG = "안녕하세요. W편의점입니다.";
    public static final String HAVING_PRODUCT_MSG = "현재 보유하고 있는 상품입니다.";

    public static final String NAME_QUANTITY_REQ_MSG = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";

    public static final String WISH_PRODUCT_NOT_EXIST_ERROR_MSG = "존재하지 않는 상품입니다. 다시 입력해 주세요.";
    public static final String WISH_PRODUCT_OUT_OF_STOCK_MSG = "재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.";
    public static final String WISH_PRODUCT_INPUT_FORAT_ERROR_MSG = "올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.";
    public static final String WISH_PRODUCT_INPUT_ERROR_MSG = "잘못된 입력입니다. 다시 입력해 주세요.";
}
