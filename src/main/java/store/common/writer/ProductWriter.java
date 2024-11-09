package store.common.writer;

import store.common.constant.StoreConst;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ProductWriter {

    public static void writeProductsToFile(final CurrentProducts currentProducts, final String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writeHeader(writer);
            write(currentProducts, writer);
        } catch (IOException ignored) {
        }
    }

    private static void writeHeader(final BufferedWriter writer) throws IOException {
        writer.write(StoreConst.PRODUCTS_FILE_HEADER);
        writer.newLine();
    }

    private static void write(final CurrentProducts currentProducts, final BufferedWriter writer) throws IOException {
        currentProducts.getCurrentProducts().values().stream()
                .flatMap(List::stream)  // List<Product>를 Product 스트림으로 펼침
                .filter(Product::isFromFile)  // 파일에서 온 제품만 필터링
                .forEach(product -> writeProduct(product, writer));  // 각 제품을 작성
    }

    private static void writeProduct(final Product product, final BufferedWriter writer) {
        try {
            String promotionText = getPromotionText(product);  // 프로모션 텍스트 가져오기
            String line = String.join(StoreConst.COMMA, product.getName(), String.valueOf(product.getPrice()),
                    String.valueOf(product.getCurrentQuantity()), promotionText);
            writer.write(line);
            writer.newLine();
        } catch (IOException ignored) {
        }
    }

    private static String getPromotionText(final Product product) {
        if (product.getPromotion() != null) {
            return product.getPromotion().getPromotionName();
        }
        return "null";
    }
}
