package store.common.parser.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@DisplayName("⭐️Products.md 파싱 테스트")
class ProductMarkDownFileParserTest {

    @Nested
    @DisplayName("parseProducts 메서드는")
    class ParseProductMethodTest {

        ProductParser parser = new ProductMarkDownFileParser();

        String TEST_PRODUCTS_MD_PATH = "src/test/resources/TestProducts.md";
        String TEST_PRODUCTS__MD_PATH_EX = "src/test/resources/TestProducts2.md";
        String TEST_PRODUCTS_FORMAT_EX = "src/test/resources/TestProducts_ex.md";

        @Nested
        @DisplayName("올바른 파일 경로와 형식이 주어지면")
        class IfCorrectPath {
            @Test
            @DisplayName("파일을 읽어서 CurrentProducts로 반환한다.")
            void parsePromotion() {
                //given
                String filePath = TEST_PRODUCTS_MD_PATH;

                Promotion promotion1 = Promotion.of("테스트1+1",
                        PromotionType.ONE_PLUS_ONE,
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2)
                );
                Promotion promotion2 = Promotion.of("테스트추천상품",
                        PromotionType.TWO_PLUS_ONE,
                        LocalDate.of(2024, 1, 3),
                        LocalDate.of(2024, 1, 4)
                );
                Promotion promotion3 = Promotion.of("테스트할인",
                        PromotionType.ONE_PLUS_ONE,
                        LocalDate.of(2024, 1, 5),
                        LocalDate.of(2024, 1, 6)
                );

                List<Promotion> promotions = List.of(promotion1, promotion2, promotion3);
                CurrentPromotions currentPromotions = CurrentPromotions.create(promotions);

                //when
                CurrentProducts currentProducts = parser.parseProducts(filePath, currentPromotions);
                Map<String, List<Product>> productMap = currentProducts.getCurrentProducts();

                //then
                for (Map.Entry<String, List<Product>> entry : productMap.entrySet()) {
                    String key = entry.getKey();
                    List<Product> expectedProducts = entry.getValue();
                    List<Product> actualProducts = productMap.get(key);

                    Assertions.assertThat(actualProducts)
                            .hasSize(expectedProducts.size())
                            .containsExactlyInAnyOrderElementsOf(expectedProducts);
                }
            }
        }

        @Nested
        @DisplayName("올바른 파일 경로와 형식이 주어지지 않으면")
        class IfNotCorrectPath {
            @Test
            @DisplayName("FILE_PARSE_OR_PATH_ERROR를 반환한다.")
            void parsePromotion_ex() {
                //given
                Promotion promotion1 = Promotion.of("테스트1+1",
                        PromotionType.ONE_PLUS_ONE,
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2)
                );
                List<Promotion> promotions = List.of(promotion1);

                CurrentPromotions currentPromotions = CurrentPromotions.create(promotions);

                //when
                //then
                Assertions.assertThatThrownBy(() -> parser.parseProducts(TEST_PRODUCTS__MD_PATH_EX, currentPromotions))
                        .isInstanceOf(BusinessException.class);
                Assertions.assertThatThrownBy(() -> parser.parseProducts(TEST_PRODUCTS__MD_PATH_EX, currentPromotions))
                        .hasMessageContaining(StoreConst.FILE_PARSE_OR_PATH_ERROR_MSG);
                Assertions.assertThatThrownBy(() -> parser.parseProducts(TEST_PRODUCTS_FORMAT_EX, currentPromotions))
                        .isInstanceOf(BusinessException.class);
                Assertions.assertThatThrownBy(() -> parser.parseProducts(TEST_PRODUCTS_FORMAT_EX, currentPromotions))
                        .hasMessageContaining(StoreConst.FILE_PARSE_OR_PATH_ERROR_MSG);
            }
        }
    }
}