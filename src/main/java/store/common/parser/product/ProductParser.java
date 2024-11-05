package store.common.parser.product;

import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.CurrentPromotions;

public interface ProductParser {

    CurrentProducts parseProducts(String filePath, CurrentPromotions currentPromotions);
}
