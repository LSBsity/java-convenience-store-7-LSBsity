package store.common.parser.promotion;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;

import java.time.LocalDate;
import java.util.List;

@DisplayName("⭐️Promotions.md 파싱 테스트")
class PromotionMarkDownFileParserTest {

    @Nested
    @DisplayName("parsePromotions 메서드는")
    class ParsePromotionMethodTest {

        PromotionParser parser = new PromotionMarkDownFileParser();

        String TEST_PROMOTIONS_MD_PATH = "src/test/resources/Testpromotions.md";
        String TEST_PROMOTIONS__MD_PATH_EX = "src/test/resources/Testpromotions2.md";
        String TEST_PROMOTIONS_FORMAT_EX = "src/test/resources/Testpromotions_ex.md";

        @Nested
        @DisplayName("올바른 파일 경로와 형식이 주어지면")
        class IfCorrectPath {
            @Test
            @DisplayName("파일을 읽어서 CurrentPromotions로 반환한다.")
            void parsePromotion() {
                //given
                String filePath = TEST_PROMOTIONS_MD_PATH;

                //when
                CurrentPromotions currentPromotions = parser.parsePromotions(filePath);
                List<Promotion> promotionList = currentPromotions.getCurrentPromotions();

                //then
                Assertions.assertThat(currentPromotions.getCountOfCurrentPromotions())
                        .isEqualTo(3);

                Promotion first = promotionList.get(0);
                Assertions.assertThat(first.getPromotionType()).isEqualTo(PromotionType.ONE_PLUS_ONE);
                Assertions.assertThat(first.getPromotionName()).isEqualTo("테스트1+1");
                Assertions.assertThat(first.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
                Assertions.assertThat(first.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 2));

                Promotion second = promotionList.get(1);
                Assertions.assertThat(second.getPromotionType()).isEqualTo(PromotionType.TWO_PLUS_ONE);
                Assertions.assertThat(second.getPromotionName()).isEqualTo("테스트추천상품");
                Assertions.assertThat(second.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 3));
                Assertions.assertThat(second.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 4));

                Promotion third = promotionList.get(2);
                Assertions.assertThat(third.getPromotionType()).isEqualTo(PromotionType.ONE_PLUS_ONE);
                Assertions.assertThat(third.getPromotionName()).isEqualTo("테스트할인");
                Assertions.assertThat(third.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 5));
                Assertions.assertThat(third.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 6));
            }
        }

        @Nested
        @DisplayName("올바른 파일 경로와 형식이 주어지지 않으면")
        class IfNotCorrectPath {
            @Test
            @DisplayName("FILE_PARSE_OR_PATH_ERROR를 반환한다.")
            void parsePromotion_ex() {
                //given
                //when
                //then
                Assertions.assertThatThrownBy(() -> parser.parsePromotions(TEST_PROMOTIONS__MD_PATH_EX))
                        .isInstanceOf(BusinessException.class);
                Assertions.assertThatThrownBy(() -> parser.parsePromotions(TEST_PROMOTIONS__MD_PATH_EX))
                        .hasMessageContaining(StoreConst.FILE_PARSE_OR_PATH_ERROR_MSG);
                Assertions.assertThatThrownBy(() -> parser.parsePromotions(TEST_PROMOTIONS_FORMAT_EX))
                        .isInstanceOf(BusinessException.class);
                Assertions.assertThatThrownBy(() -> parser.parsePromotions(TEST_PROMOTIONS_FORMAT_EX))
                        .hasMessageContaining(StoreConst.FILE_PARSE_OR_PATH_ERROR_MSG);
            }
        }
    }

}