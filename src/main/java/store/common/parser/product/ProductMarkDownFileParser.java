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
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // 파일의 헤더는 무시

            readFile(reader, products, currentPromotions);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }

        return CurrentProducts.create(products);
    }

    private static void readFile(final BufferedReader reader, final List<Product> products,
                                 final CurrentPromotions currentPromotions) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            Product product = parseLine(line, currentPromotions);
            products.add(product);
        }
    }

    private static Product parseLine(final String line, final CurrentPromotions currentPromotions) {
        String[] fields = line.split(StoreConst.FILE_PARSE_DELIMETER);

        if (fields.length != StoreConst.PRODUCT_COLUMN_SIZE) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }

        try {
            Promotion promotion = findAvailablePromotion(fields, currentPromotions);
            return convert(fields, promotion);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static Promotion findAvailablePromotion(final String[] fields, final CurrentPromotions currentPromotions) {
        String promotionName = fields[3];
        Promotion promotion = currentPromotions.findAvailablePromotionByName(promotionName);
        return promotion;
    }

    private static Product convert(final String[] fields, final Promotion promotion) {
        String name = fields[0];
        int price = Integer.parseInt(fields[1]);
        int quantity = Integer.parseInt(fields[2]);

        return Product.of(name, price, quantity, promotion, promotion.isValidPromotion(), true);
    }

}