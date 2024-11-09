package store.common.parser.input;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;
import store.domain.model.dto.UserWish;

import java.time.LocalDate;
import java.util.List;

@DisplayName("⭐고객 구매 요청 파싱 테스트")
class RegexInputParserTest {

    InputParser inputParser = new RegexInputParser();
    CurrentProducts currentProducts;

    @BeforeEach
    void setUp() {
        Promotion onePlusOne = Promotion.of("테스트1+1",
                PromotionType.ONE_PLUS_ONE,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2)
        );
        Promotion twoPlusone = Promotion.of("테스트2+1",
                PromotionType.TWO_PLUS_ONE,
                LocalDate.of(2024, 1, 3),
                LocalDate.of(2024, 1, 4)
        );

        Product product1 = Product.of("콜라", 1000, 10, onePlusOne, true, false);
        Product product2 = Product.of("콜라", 1000, 10, Promotion.createNone(), false, false);
        Product product3 = Product.of("사이다", 1000, 8, twoPlusone, true, false);
        Product product4 = Product.of("사이다", 1000, 7, Promotion.createNone(), true, false);
        Product product5 = Product.of("오렌지주스", 1800, 9, twoPlusone, true, false);
        Product product6 = Product.of("오렌지주스", 1800, 0, Promotion.createNone(), false, false);

        currentProducts = CurrentProducts.create(List.of(product1, product2, product3, product4, product5, product6));
    }

    @Nested
    @DisplayName("validateNameAndQuantity 메서드는")
    class ValidateNameAndQuantityTest {

        @Nested
        @DisplayName("올바른 입력이 들어오면")
        class IfCorrectInput {

            @Test
            @DisplayName("사용자의 구매 물품과 수량을 담은 List<UserWish.Request>를 반환한다.")
            void returnUserWishList() {
                //given
                String userRequest = "[콜라-3],[사이다-3],[오렌지주스-1]";

                //when
                List<UserWish.Request> userWishLists = inputParser.validateNameAndQuantity(userRequest, currentProducts);

                //then
                Assertions.assertThat(userWishLists.size()).isEqualTo(3);

                UserWish.Request first = userWishLists.get(0);
                Assertions.assertThat(first.getProductName()).isEqualTo("콜라");
                Assertions.assertThat(first.getQuantity()).isEqualTo(3);

                UserWish.Request second = userWishLists.get(1);
                Assertions.assertThat(second.getProductName()).isEqualTo("사이다");
                Assertions.assertThat(second.getQuantity()).isEqualTo(3);

                UserWish.Request third = userWishLists.get(2);
                Assertions.assertThat(third.getProductName()).isEqualTo("오렌지주스");
                Assertions.assertThat(third.getQuantity()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("올바르지 않은 입력이 들어오면")
        class IfInCorrectInput {

            @ParameterizedTest
            @ValueSource(strings = {
                    "[콜라-3],[사이다-3],[오렌지주스-1a]",
                    "[콜라-3],[사이다-],[오렌지주스-]",
                    "[콜라-],[사이다-3],[오렌지주스-12a]",
                    "[콜라--2],[사이다-3],[오렌지주스--12a]",
            })
            @DisplayName("수량 입력 오류일 시 WISH_PRODUCT_INPUT_FORMAT_ERROR를 반환한다.")
            void throwBusinessException_quantity(String userInput) {
                //given
                //when
                //then
                Assertions.assertThatThrownBy(() -> inputParser.validateNameAndQuantity(userInput, currentProducts))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR.getMessage());

                Assertions.assertThatThrownBy(() -> inputParser.validateNameAndQuantity(userInput, currentProducts))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR.getMessage());

            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "콜라-3],[사이다-3],[오렌지주스-1a]",
                    "[콜라-3,[사이다-],[오렌지주스-]",
                    "[콜라-]사이다-3],[오렌지주스-12a",
                    "[콜라-]사이다-3][-오렌지주스-12a]",
                    "[콜라-]사이다-3[-오렌지주스-12a]",
            })
            @DisplayName("입력 포맷을 지키지 않을 시 WISH_PRODUCT_INPUT_FORMAT_ERROR를 반환한다.")
            void throwBusinessException_format(String userInput) {
                //given
                //when
                //then
                Assertions.assertThatThrownBy(() -> inputParser.validateNameAndQuantity(userInput, currentProducts))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.WISH_PRODUCT_INPUT_FORMAT_ERROR.getMessage());

            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "[컵라면-3],[사이다-3],[오렌지주스-1]",
                    "[콜라-3],[초코바-3],[오렌지주스-1]",
                    "[콜라-3],[사이다-3],[감자칩-1]",
            })
            @DisplayName("존재하지 않는 물건일 시 WISH_PRODUCT_NOT_EXIST_ERROR를 반환한다.")
            void throwBusinessException_notExistProduct(String userInput) {
                //given
                //when
                //then
                Assertions.assertThatThrownBy(() -> inputParser.validateNameAndQuantity(userInput, currentProducts))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.WISH_PRODUCT_NOT_EXIST_ERROR.getMessage());

            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "[콜라-21],[사이다-8],[오렌지주스-9]",
                    "[콜라-2],[사이다-9],[오렌지주스-10]",
                    "[콜라-5],[사이다-3],[오렌지주스-10]",
                    "[콜라-11],[사이다-10],[오렌지주스-10]",
            })
            @DisplayName("구매하려는 물건의 양이 부족할 시 WISH_PRODUCT_OUT_OF_STOCK_ERROR를 반환한다.")
            void throwBusinessException_outOfStock(String userInput) {
                //given
                //when
                //then
                Assertions.assertThatThrownBy(() -> inputParser.validateNameAndQuantity(userInput, currentProducts))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.WISH_PRODUCT_OUT_OF_STOCK_ERROR.getMessage());
            }
        }

    }
}