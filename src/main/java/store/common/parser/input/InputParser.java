package store.common.parser.input;

import store.domain.model.product.CurrentProducts;
import store.common.parser.dto.UserWish;

import java.util.List;

public interface InputParser {
    List<UserWish.Request> validateNameAndQuantity(String input, CurrentProducts currentProducts);
}