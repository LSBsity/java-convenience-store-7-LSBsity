package store.domain.model.dto;

public enum Suggestion {

    ADDITIONAL_FREE_PRODUCT("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까?", true),
    INSUFFICIENT_PROMOTION_STOCK("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까?",  true),
    ALREADY_ELIGIBLE("", false),
    NONE("", false),
    ;

    private final String format;
    private final boolean shouldPrint;

    Suggestion(String format, boolean shouldPrint) {
        this.format = format;
        this.shouldPrint = shouldPrint;
    }

    public String getFormat() {
        return format + "(Y/N)\n";
    }

    public boolean isShouldPrint() {
        return shouldPrint;
    }
}
