package store.common.parser.input;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.CurrentProducts;
import store.domain.model.dto.UserWish;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexInputParser implements InputParser {

    private static final Pattern WISH_LIST_PATTERN = Pattern.compile(StoreConst.WISH_LIST_INPUT_COMPILE_REGEX);

    @Override
    public List<UserWish.Request> validateNameAndQuantity(final String input, final CurrentProducts currentProducts) {
        if (!isValidFormat(input)) throw new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR);

        Matcher matcher = WISH_LIST_PATTERN.matcher(input);
        return parseWishListFromInput(matcher, currentProducts);
    }

    private boolean isValidFormat(final String input) {
        return input.matches(StoreConst.WISH_LIST_INPUT_REGEX);
    }

    private List<UserWish.Request> parseWishListFromInput(final Matcher matcher, final CurrentProducts currentProducts) {
        List<UserWish.Request> wishList = new ArrayList<>();
        Set<String> productNames = new HashSet<>();

        while (matcher.find()) {
            String userRequestProductName = matcher.group(1);
            int userRequestProductQuantity = Integer.parseInt(matcher.group(2));

            validateDuplicate(productNames, userRequestProductName);
            validateProductAvailability(currentProducts, userRequestProductName, userRequestProductQuantity);
            wishList.add(UserWish.Request.of(userRequestProductName, userRequestProductQuantity));
        }
        return wishList;
    }

    private void validateDuplicate(final Set<String> productNames, final String userRequestProductName) {
        if (!productNames.add(userRequestProductName)) {
            throw new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_ERROR);
        }
    }

    private void validateProductAvailability(final CurrentProducts currentProducts, final String productName, final int quantity) {
        ensureProductExists(currentProducts, productName);
        ensureSufficientStock(currentProducts, productName, quantity);
    }

    private void ensureProductExists(final CurrentProducts products, final String productName) {
        products.findProductByName(productName);
    }

    private void ensureSufficientStock(final CurrentProducts currentProducts, final String productName, final int requestedQuantity) {
        int availableStock = currentProducts.getCurrentTotalStockQuantity(productName);
        if (availableStock < requestedQuantity) {
            throw new BusinessException(ErrorCode.WISH_PRODUCT_OUT_OF_STOCK_ERROR);
        }
    }
}
