package store.common.parser.product;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.model.promotion.Promotion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductMarkDownFileParser implements ProductParser {

    @Override
    public CurrentProducts parseProducts(final String filePath, final CurrentPromotions currentPromotions) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<Product> products = convertFileToProducts(currentPromotions, reader);

            return CurrentProducts.create(products);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static List<Product> convertFileToProducts(final CurrentPromotions currentPromotions, final BufferedReader reader) throws IOException {
        List<Product> products = new ArrayList<>();
        reader.readLine(); // 파일의 헤더는 무시
        String line;
        while ((line = reader.readLine()) != null) {
            Product product = parseLine(line, currentPromotions);
            products.add(product);
        }
        return products;
    }

    private static Product parseLine(final String line, final CurrentPromotions currentPromotions) {
        String[] fields = line.split(StoreConst.FILE_PARSE_DELIMETER);
        validateHeaders(fields.length);
        try {
            Promotion promotion = findCurrentPromotion(fields, currentPromotions);
            return toProduct(fields, promotion);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static Promotion findCurrentPromotion(final String[] fields, final CurrentPromotions currentPromotions) {
        Promotion promotion = currentPromotions.findAvailablePromotionByName(fields[3]);
        return promotion;
    }

    private static Product toProduct(final String[] fields, final Promotion promotion) {
        String name = fields[0];
        int price = Integer.parseInt(fields[1]);
        int quantity = Integer.parseInt(fields[2]);
        validateQuantity(price, quantity);
        return Product.of(name, price, quantity, promotion, promotion.isValidPromotion(), true);
    }

    private static void validateHeaders(final int headerLength) {
        if (headerLength != StoreConst.PRODUCT_COLUMN_SIZE) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static void validateQuantity(final int price, final int quantity) {
        if (price < 0 || quantity < 0) {
            throw new BusinessException(ErrorCode.FILE_CONTAINS_NEGATIVE_NUMBERS);
        }
    }
}