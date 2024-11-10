package store.common.constant;

public class InvoicePrintConst {
    public static final String PRINT_STORE_NAME_TITLE = "===========W 편의점=============";
    public static final String PRINT_NAME_QUANTITY_PRICE = "상품명\t\t\t수량\t\t금액";

    public static final String PRINT_GIFT_TITLE = "===========증\t정=============";

    public static final String PRINT_SEPARATOR = "==============================";

    public static final String NO_DISCOUNTED_PRICE = "%-4s\t\t\t%-2d\t\t%,6d\n";
    public static final String NO_DISCOUNTED_PRICE_NAME = "총구매액";

    public static final String PROMOTION_DISCOUNTED_PRICE = "%-4s\t\t\t\t\t-%,-6d\n";
    public static final String PROMOTION_DISCOUNTED_PRICE_NAME = "행사할인";

    public static final String MEMBERSHIP_DISCOUNTED_PRICE = "%-5s\t\t\t\t\t-%,-6d\n";
    public static final String MEMBERSHIP_DISCOUNTED_PRICE_NAME = "멤버십할인";

    public static final String TOTAL_PRICE = "%-3s\t\t\t\t\t%,6d\n";
    public static final String TOTAL_PRICE_NAME = "내실돈";

    public static final String PURCHASED_PRODUCT = "%-5s\t\t\t%2d\t\t%-,5d\n";
    public static final String GIFT_PRODUCT = "%-5s\t\t\t%-5d\n";
}
