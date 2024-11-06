package store.common.constant;

public class StoreConst {

    public static final String EMPTY = "";
    public static final String ERROR_PREFIX = "[ERROR] ";
    public static final int PRODUCT_COLUMN_SIZE = 4;
    public static final int PROMOTION_COLUMN_SIZE = 5;

    public static final String PROMOTIONS_FILE_PATH = "src/main/resources/promotions.md";
    public static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";
    public static final String FILE_PARSE_OR_PATH_ERROR_MSG = "파일의 경로가 올바르지 않거나 형식이 올바르지 않습니다.";
    public static final String FILE_PARSE_DELIMETER = ",";

    public static final String PRODUCT_NOT_EXIST_MSG = "존재하지 않는 상품명입니다.";

    public static final String WELCOME_MSG = "안녕하세요. W편의점입니다.";
    public static final String HAVING_PRODUCT_MSG = "현재 보유하고 있는 상품입니다.";

    public static final String NOT_INSTOCK_MSG = "재고 없음";
    public static final String QUANTITY_UNIT = "개";
}
