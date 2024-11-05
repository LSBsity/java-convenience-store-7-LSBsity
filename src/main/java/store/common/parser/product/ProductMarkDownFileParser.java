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
    public CurrentProducts parseProducts(String filePath, CurrentPromotions currentPromotions) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // ignore first line create file

            while ((line = reader.readLine()) != null) {
                Product product = parseLine(line, currentPromotions);
                products.add(product);
            }

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }

        return CurrentProducts.create(products);
    }

    private static Product parseLine(String line, CurrentPromotions currentPromotions) {
        String[] fields = line.split(StoreConst.FILE_PARSE_DELIMETER);

        if (fields.length != StoreConst.PRODUCT_COLUMN_SIZE) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }

        try {
            String name = fields[0];
            int price = Integer.parseInt(fields[1]);
            int quantity = Integer.parseInt(fields[2]);
            String promotionName = fields[3];

            Promotion promotion = currentPromotions.findPromotionByName(promotionName);
            return Product.of(name, price, quantity, promotion, promotion.isValidPromotion());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }
}