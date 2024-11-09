package store.domain.model.promotion;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public enum PromotionType {
    ONE_PLUS_ONE(1, 1, 2, (buy, get) -> buy == 1 && get == 1),
    TWO_PLUS_ONE(2, 1, 3, (buy, get) -> buy == 2 && get == 1),
    NONE(0, 0, 0, (buy, get) -> buy == 0 && get == 0);

    private final int buy;
    private final int get;
    private final int defaultSize;
    private final BiFunction<Integer, Integer, Boolean> exp;

    PromotionType(int buy, int get, int size, BiFunction<Integer, Integer, Boolean> exp) {
        this.buy = buy;
        this.get = get;
        this.defaultSize = size;
        this.exp = exp;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public boolean matchType(int buy, int get) {
        return exp.apply(buy, get);
    }

    public static PromotionType getMatchedPromotionType(final int buy, final int get) {
        return Stream.of(PromotionType.values())
                .filter(type -> type.matchType(buy, get))
                .findFirst()
                .orElse(PromotionType.NONE);
    }
}
