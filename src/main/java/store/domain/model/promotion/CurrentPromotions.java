package store.domain.model.promotion;

import java.util.Collections;
import java.util.List;

public class CurrentPromotions {

    private final List<Promotion> currentPromotions;

    private CurrentPromotions(List<Promotion> currentPromotions) {
        this.currentPromotions = currentPromotions;
    }

    public static CurrentPromotions create(List<Promotion> currentPromotions) {
        return new CurrentPromotions(currentPromotions);
    }

    public int getCountOfCurrentPromotions() {
        return this.currentPromotions.size();
    }

    public List<Promotion> getCurrentPromotions() {
        return Collections.unmodifiableList(this.currentPromotions);
    }

    public Promotion findPromotionByName(String name) {
        return currentPromotions.stream()
                .filter(promotion -> promotion.getPromotionName().equals(name))
                .findFirst()
                .orElse(Promotion.createNone());
    }
}