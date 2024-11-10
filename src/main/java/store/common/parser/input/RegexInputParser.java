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
    public List<UserWish> validateNameAndQuantity(final String input, final CurrentProducts currentProducts) {
        if (!isValidFormat(input)) throw new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR);

        Matcher matcher = WISH_LIST_PATTERN.matcher(input);
        return parseWishListFromInput(matcher, currentProducts);
    }

    private boolean isValidFormat(final String input) {
        return input.matches(StoreConst.WISH_LIST_INPUT_REGEX);
    }

    private List<UserWish> parseWishListFromInput(final Matcher matcher, final CurrentProducts currentProducts) {
        List<UserWish> wishList = new ArrayList<>();
        Set<String> productNames = new HashSet<>();

        while (matcher.find()) {
            String userRequestProductName = matcher.group(1);
            int userRequestProductQuantity = Integer.parseInt(matcher.group(2));

            validate(currentProducts, productNames, userRequestProductName, userRequestProductQuantity);
            wishList.add(UserWish.of(userRequestProductName, userRequestProductQuantity));
        }
        return wishList;
    }

    private static void validate(final CurrentProducts currentProducts, final Set<String> productNames, final String userRequestProductName, final int userRequestProductQuantity) {
        validateDuplicate(productNames, userRequestProductName);
        validateProductAvailability(currentProducts, userRequestProductName, userRequestProductQuantity);
    }

    private static void validateDuplicate(final Set<String> productNames, final String userRequestProductName) {
        if (!productNames.add(userRequestProductName)) {
            throw new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_ERROR);
        }
    }

    private static void validateProductAvailability(final CurrentProducts currentProducts, final String productName, final int quantity) {
        ensureProductExists(currentProducts, productName);
        ensureSufficientStock(currentProducts, productName, quantity);
    }

    private static void ensureProductExists(final CurrentProducts products, final String productName) {
        products.findProductByName(productName); //throw WISH_PRODUCT_NOT_EXIST_ERROR if not exist
    }

    private static void ensureSufficientStock(final CurrentProducts currentProducts, final String productName, final int requestedQuantity) {
        int availableStock = currentProducts.getCurrentTotalStockQuantity(productName);
        if (availableStock < requestedQuantity) {
            throw new BusinessException(ErrorCode.WISH_PRODUCT_OUT_OF_STOCK_ERROR);
        }
    }
}
