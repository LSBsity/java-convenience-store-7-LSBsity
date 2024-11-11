package store.common.writer;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ProductWriter {

    public static void writeProductsToFile(final String fileName, final CurrentProducts currentProducts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writeHeader(writer);
            write(writer, currentProducts);
        } catch (IOException ignored) {
        }
    }

    private static void writeHeader(final BufferedWriter writer) throws IOException {
        writer.write(StoreConst.PRODUCTS_FILE_HEADER);
        writer.newLine();
    }

    private static void write(final BufferedWriter writer, final CurrentProducts currentProducts) throws IOException {
        currentProducts.getCurrentProducts().values().stream()
                .flatMap(List::stream)  // List<Product>를 Product 스트림으로 펼침
                .filter(Product::isFromFile)  // 파일에서 온 제품만 필터링
                .forEach(product -> writeProduct(product, writer));  // 각 제품을 작성
    }

    private static void writeProduct(final Product product, final BufferedWriter writer) {
        try {
            String line = makeRow(product); // 프로모션 이름 가져오기
            writer.write(line);
            writer.newLine();
        } catch (IOException ignored) {
            throw new BusinessException(ErrorCode.FILE_WRITE_ERROR);
        }
    }

    private static String makeRow(final Product product) {
        CharSequence[] attributes = {
                product.getName(),                            //물품 이름
                String.valueOf(product.getPrice()),           //물품 가격
                String.valueOf(product.getCurrentQuantity()), //물품 수량
                getPromotionName(product)                     //프로모션 이름
        };
        return String.join(StoreConst.COMMA, attributes);
    }

    private static String getPromotionName(final Product product) {
        if (product.getPromotion() == null) return StoreConst.NULL;

        return product.getPromotion().getPromotionName();
    }
}
