package store.common.parser.promotion;

import store.domain.model.promotion.CurrentPromotions;

public interface PromotionParser {

    CurrentPromotions parsePromotions(String filePath);
}
