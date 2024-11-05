package store.domain.model.promotion;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public enum PromotionType {
    ONE_PLUS_ONE(1, 1, (buy, get) -> buy == 1 && get == 1),
    TWO_PLUS_ONE(2, 1, (buy, get) -> buy == 2 && get == 1),
    NONE(0, 0, (buy, get) -> buy == 0 && get == 0);

    private final int buy;
    private final int get;
    private final BiFunction<Integer, Integer, Boolean> exp;

    PromotionType(int buy, int get, BiFunction<Integer, Integer, Boolean> exp) {
        this.buy = buy;
        this.get = get;
        this.exp = exp;
    }

    public boolean matchType(int buy, int get) {
        return exp.apply(buy, get);
    }

    public static PromotionType getMatchedPromotionType(int buy, int get) {
        return Stream.of(PromotionType.values())
                .filter(type -> type.matchType(buy, get))
                .findFirst()
                .orElse(PromotionType.NONE);
    }
}
