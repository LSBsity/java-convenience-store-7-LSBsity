package store.domain.model.promotion;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("⭐️PromotionType Enum 테스트")
class PromotionTypeTest {

    @Nested
    @DisplayName("getMatchedPromotionType 메서드는")
    class GetMatchedPromotionTypeTest {
        @Test
        @DisplayName("Buy와 Get이 (2, 1)이면 TWO_PLUS_ONE이 반환되어야 한다.")
        void twoPlusOneTest() {
            PromotionType twoPlusOne = PromotionType.TWO_PLUS_ONE;

            PromotionType matched = PromotionType.getMatchedPromotionType(2, 1);

            Assertions.assertThat(matched).isEqualTo(twoPlusOne);
        }

        @Test
        @DisplayName("Buy와 Get이 (1, 1)이면 ONE_PLUS_ONE이 반환되어야 한다.")
        void onePlusOneTest() {
            PromotionType onePlusOne = PromotionType.ONE_PLUS_ONE;

            PromotionType matched = PromotionType.getMatchedPromotionType(1, 1);

            Assertions.assertThat(matched).isEqualTo(onePlusOne);
        }

        @Test
        @DisplayName("Buy와 Get이 (2, 1), (1, 1) 둘 중 하나도 아니라면 NONE이 반환되어야 한다.")
        void noneTest() {
            PromotionType none = PromotionType.NONE;

            PromotionType first = PromotionType.getMatchedPromotionType(0, 0);
            PromotionType second = PromotionType.getMatchedPromotionType(0, 1);
            PromotionType third = PromotionType.getMatchedPromotionType(1, 0);

            Assertions.assertThat(first).isEqualTo(none);
            Assertions.assertThat(second).isEqualTo(none);
            Assertions.assertThat(third).isEqualTo(none);
        }

    }


}