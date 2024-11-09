package store.common.parser.input;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.CurrentProducts;
import store.domain.model.dto.UserWish;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexInputParser implements InputParser {

    private static final Pattern WISH_LIST_PATTERN = Pattern.compile(StoreConst.WISH_LIST_INPUT_COMPILE_REGEX);

    @Override
    public List<UserWish.Request> validateNameAndQuantity(String input, CurrentProducts currentProducts) {
        if (!isValidFormat(input)) throw new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR);

        Matcher matcher = WISH_LIST_PATTERN.matcher(input);
        return parseWishListFromInput(matcher, currentProducts);
    }

    private boolean isValidFormat(String input) {
        return input.matches(StoreConst.WISH_LIST_INPUT_REGEX);
    }

    private List<UserWish.Request> parseWishListFromInput(Matcher matcher, CurrentProducts currentProducts) {
        List<UserWish.Request> wishList = new ArrayList<>();

        while (matcher.find()) {
            String userRequestProductName = matcher.group(1);
            int userRequestProductQuantity = Integer.parseInt(matcher.group(2));

            validateProductAvailability(currentProducts, userRequestProductName, userRequestProductQuantity);
            wishList.add(UserWish.Request.of(userRequestProductName, userRequestProductQuantity));
        }
        return wishList;
    }

    private void validateProductAvailability(CurrentProducts currentProducts, String productName, int quantity) {
        ensureProductExists(currentProducts, productName);
        ensureSufficientStock(currentProducts, productName, quantity);
    }

    private void ensureProductExists(CurrentProducts products, String productName) {
        products.findProductByName(productName);
    }

    private void ensureSufficientStock(CurrentProducts currentProducts, String productName, int requestedQuantity) {
        int availableStock = currentProducts.getCurrentTotalStockQuantity(productName);
        if (availableStock < requestedQuantity) {
            throw new BusinessException(ErrorCode.WISH_PRODUCT_OUT_OF_STOCK_ERROR);
        }
    }
}
