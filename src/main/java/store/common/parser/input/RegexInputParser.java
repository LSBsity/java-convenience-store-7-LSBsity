package store.common.parser.input;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.common.parser.dto.UserWish;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexInputParser implements InputParser {

    @Override
    public List<UserWish.Request> validateNameAndQuantity(String input, CurrentProducts products) {
        if (!input.matches(StoreConst.WISH_LIST_INPUT_REGEX)) {                              //형식을 맞추었는가
            throw new BusinessException(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR);
        }

        Pattern pattern = Pattern.compile(StoreConst.WISH_LIST_INPUT_COMPILE_REGEX);
        Matcher matcher = pattern.matcher(input);

        return convertUserRequestToWishList(products, matcher);
    }

    private static List<UserWish.Request> convertUserRequestToWishList(CurrentProducts products, Matcher matcher) {
        List<UserWish.Request> wishList = new ArrayList<>();

        while (matcher.find()) {
            String requestProductName = matcher.group(1);
            int requestProductQuantity = Integer.parseInt(matcher.group(2));

            parseValidateLogics(products, requestProductName, requestProductQuantity);

            UserWish.Request userWishProduct = UserWish.Request.of(requestProductName, requestProductQuantity);
            wishList.add(userWishProduct);
        }
        return wishList;
    }

    private static void parseValidateLogics(CurrentProducts products, String requestProductName, int requestProductQuantity) {
        List<Product> findProducts = validateProductExist(products, requestProductName); // 존재하는가
        validateProductInstock(findProducts, requestProductQuantity);                    // 수량이 있는가
    }
    private static List<Product> validateProductExist(CurrentProducts products, String productName) {
        return products.findProductByName(productName); // throw WISH_PRODUCT_NOT_EXIST_ERROR
    }

    private static void validateProductInstock(List<Product> findProducts, int requestProductQuantity) {
        findProducts.stream()
                .filter(product -> product.getQuantity() >= requestProductQuantity)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_PRODUCT_OUT_OF_STOCK_ERROR));
    }

}
