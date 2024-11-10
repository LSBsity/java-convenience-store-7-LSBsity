package store.domain.model.store;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.SuggestionType;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.servce.stock.StockService;

import java.time.LocalDate;
import java.util.List;

@DisplayName("⭐재고 처리 서비스 테스트")
class StockServiceTest {

    StockService stockService = new StockService();

    CurrentProducts currentProducts;

    Promotion onePlusOne = Promotion.of("테스트1+1",
            PromotionType.ONE_PLUS_ONE,
            LocalDate.of(2024, 11, 1),
            LocalDate.of(2024, 12, 1)
    );
    Promotion twoPlusone = Promotion.of("테스트2+1",
            PromotionType.TWO_PLUS_ONE,
            LocalDate.of(2024, 11, 1),
            LocalDate.of(2024, 12, 4)
    );
    Promotion expired = Promotion.of("만료2+1",
            PromotionType.TWO_PLUS_ONE,
            LocalDate.of(2024, 10, 1),
            LocalDate.of(2024, 11, 1)
    );

    Product cokeOPO;
    Product cokeNON;
    Product ciderTPO;
    Product ciderNON;
    Product juiceTPO;
    Product juiceNON;
    Product sparklingWaterTPO;
    Product vitaminWaterTPO;
    Product lunchBoxNON;

    @BeforeEach
    void setUp() {
        cokeOPO = Product.of("콜라", 1000, 10, onePlusOne, true, false);
        cokeNON = Product.of("콜라", 1000, 10, Promotion.createNone(), false, false);
        ciderTPO = Product.of("사이다", 1000, 8, twoPlusone, true, false);
        ciderNON = Product.of("사이다", 1000, 7, Promotion.createNone(), false, false);
        juiceTPO = Product.of("오렌지주스", 1800, 9, twoPlusone, true, false);
        juiceNON = Product.of("오렌지주스", 1800, 0, Promotion.createNone(), false, false);
        sparklingWaterTPO = Product.of("탄산수", 1200, 5, twoPlusone, true, false);
        vitaminWaterTPO = Product.of("비타민워터", 1500, 7, twoPlusone, true, false);
        lunchBoxNON = Product.of("정식도시락", 6400, 8, Promotion.createNone(), false, false);

        currentProducts = CurrentProducts.create(List.of(cokeOPO, cokeNON, ciderTPO, ciderNON, juiceTPO, juiceNON, sparklingWaterTPO, vitaminWaterTPO, lunchBoxNON));
    }

    @Nested
    @DisplayName("최종으로 확정된 물건의 수량을 차감하는 메서드인")
    class StockTest {

        @Nested
        @DisplayName("updateStock 메서드는")
        class UpdateStockMethodTest {

            @Nested
            @DisplayName("프로모션 혜택을 권하는 ADDITIONAL_FREE_PRODUCT일 시")
            class IF_ADDITIONAL_FREE_PRODUCT {

                final static int beforeQuantity = 10;

                @ParameterizedTest
                @ValueSource(ints = {2, 4, 6})
                @DisplayName("프로모션 제품이므로 프로모션 재고를 내린다.")
                void decreasePromotionStock(int userRequestSize) {
                    //given

                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                    cokeSuggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                    //when
                    stockService.updateStock(confirmedProduct);

                    //then
                    Assertions.assertThat(cokeOPO.getCurrentQuantity()).isEqualTo(beforeQuantity - userRequestSize);
                }
            }

            @Nested
            @DisplayName("프로모션 재고가 부족하다는 INSUFFICIENT_PROMOTION_STOCK이고")
            class IF_INSUFFICIENT_PROMOTION_STOCK {

                @Nested
                @DisplayName("고객이 나머지는 그냥 할인 없이 구매하겠다고 한다면")
                class IF_USER_YES {

                    @Nested
                    @DisplayName("프로모션 재고가 충분하지 않다면")
                    class DecreaseAllStock {

                        final static int CIDER_PROMOTION_QUANTITY = 8;

                        @ParameterizedTest
                        @ValueSource(ints = {10, 11, 12, 13})
                        @DisplayName("프로모션 재고에서 전부 차감하고 나머지는 일반 재고에서 차감한다.")
                        void decreaseRemainderInDefaultStock(int userRequestSize) {
                            //given
                            StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                            ciderSuggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);

                            ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, UserAnswer.YES);
                            int before = ciderNON.getCurrentQuantity();

                            //when
                            stockService.updateStock(confirmedProduct);

                            //then
                            int defaultStockDecrease = userRequestSize - CIDER_PROMOTION_QUANTITY;
                            Assertions.assertThat(ciderTPO.getCurrentQuantity()).isEqualTo(0);
                            Assertions.assertThat(ciderNON.getCurrentQuantity()).isEqualTo(before - defaultStockDecrease);
                        }

                        @ParameterizedTest
                        @ValueSource(ints = {7})
                        @DisplayName("프로모션 재고가 충분하다면 프로모션 재고에서 전부 내린다.")
                        void decreaseRemainderInPromotionStock(int userRequestSize) {
                            //given
                            StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                            ciderSuggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);

                            ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, UserAnswer.YES);
                            int before = ciderNON.getCurrentQuantity();
                            //when
                            stockService.updateStock(confirmedProduct);

                            //then
                            Assertions.assertThat(ciderTPO.getCurrentQuantity()).isEqualTo(CIDER_PROMOTION_QUANTITY - userRequestSize);
                            Assertions.assertThat(ciderNON.getCurrentQuantity()).isEqualTo(before);
                        }
                    }
                }

                @Nested
                @DisplayName("고객이 프로모션 적용 가능한 만큼만 구매하겠다고 하면")
                class IF_USER_NO {

                    final static int PROMOTION_DEFAULT_SIZE = 3;

                    @ParameterizedTest
                    @ValueSource(ints = {7, 8, 9, 10, 11, 12, 13, 14})
                    @DisplayName("프로모션 재고에서 가능한 만큼만 차감한다.")
                    void decreaseRemainderInDefaultStock(int userRequestSize) {
                        //given
                        StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                        ciderSuggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);

                        ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, UserAnswer.NO);

                        int beforePromotionQuantity = ciderTPO.getCurrentQuantity();
                        int beforeDefaultQuantity = ciderNON.getCurrentQuantity();
                        int maximumEligibleQuantity = PROMOTION_DEFAULT_SIZE * (beforePromotionQuantity / PROMOTION_DEFAULT_SIZE);

                        //when
                        stockService.updateStock(confirmedProduct);

                        //then
                        Assertions.assertThat(ciderTPO.getCurrentQuantity()).isEqualTo(beforePromotionQuantity - maximumEligibleQuantity);
                        Assertions.assertThat(ciderNON.getCurrentQuantity()).isEqualTo(beforeDefaultQuantity);
                    }
                }
            }

            @Nested
            @DisplayName("프로모션 혜택을 온전히 다 받을 수 없는 개수라는 EXCESSIVE_ADDITIONAL_PURCHASE일 시")
            class IF_EXCESSIVE_ADDITIONAL_PURCHASE {

                @ParameterizedTest
                @ValueSource(ints = {4, 7})
                @DisplayName("고객이 상관없이 그냥 구매하겠다고 하면 나머지 하나는 일반 재고에서 차감한다.")
                void decreasePromotionStock(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);
                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, UserAnswer.YES);

                    int before = ciderNON.getCurrentQuantity();

                    //when
                    stockService.updateStock(confirmedProduct);

                    //then
                    int after = ciderNON.getCurrentQuantity();
                    Assertions.assertThat(after).isEqualTo(before - 1);
                }

                @ParameterizedTest
                @ValueSource(ints = {3, 6})
                @DisplayName("고객이 프로모션 가능한 만큼만 구매하겠다고 하면 일반 재고에서 차감하지 않는다.")
                void decreasePromotionStockSubtractOne(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);
                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, UserAnswer.NO);

                    int before = ciderNON.getCurrentQuantity();

                    //when
                    stockService.updateStock(confirmedProduct);

                    //then
                    int after = ciderNON.getCurrentQuantity();
                    Assertions.assertThat(after).isEqualTo(before);
                }
            }

            @Nested
            @DisplayName("이미 혜택이 적용되는 상태라는 ALREADY_ELIGIBLE일 시")
            class IF_ALREADY_ELIGIBLE {

                final static int beforeQuantity = 10;

                @ParameterizedTest
                @ValueSource(ints = {2, 4, 6})
                @DisplayName("단순히 재고를 요청 만큼 내린다.")
                void decreasePromotionStock(int userRequestSize) {
                    //given

                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                    cokeSuggestion.changeSuggestion(SuggestionType.ALREADY_ELIGIBLE);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                    //when
                    stockService.updateStock(confirmedProduct);

                    //then
                    int currentQuantity = cokeOPO.getCurrentQuantity();
                    Assertions.assertThat(currentQuantity).isEqualTo(beforeQuantity - userRequestSize);
                }
            }

            @Nested
            @DisplayName("아무것도 해당되지 않는 NONE일 시")
            class IF_NONE {

                @Nested
                @DisplayName("프로모션 기간이 지난 상품이고 일반 재고 요청보다 요청이 많다면")
                class IF_EXCEED_DEFAULT_STOCK {

                    @ParameterizedTest
                    @ValueSource(ints = {6, 7, 8})
                    @DisplayName("일반 재고에서 차감하고 남은 개수는 프로모션 재고에서 차감한다.")
                    void decreasePromotionStock(int userRequestSize) {
                        //given
                        Product chocoTPO = Product.of("초코바", 1200, 5, expired, true, false);
                        Product chocoNON = Product.of("초코바", 1200, 5, Promotion.createNone(), false, false);
                        StoreSuggestion chocoSuggestion = StoreSuggestion.of(List.of(chocoTPO, chocoNON), userRequestSize);
                        chocoSuggestion.changeSuggestion(SuggestionType.NONE);

                        ConfirmedProduct confirmedProduct = ConfirmedProduct.of(chocoSuggestion, null);
                        int beforePromotionQuantity = chocoTPO.getCurrentQuantity();
                        int beforeDefaultQuantity = chocoNON.getCurrentQuantity();

                        //when
                        stockService.updateStock(confirmedProduct);

                        //then
                        Assertions.assertThat(chocoTPO.getCurrentQuantity())
                                .isEqualTo(beforePromotionQuantity - (userRequestSize - beforeDefaultQuantity));
                        Assertions.assertThat(chocoNON.getCurrentQuantity())
                                .isEqualTo(0);
                    }
                }

                @Nested
                @DisplayName("일반 재고보다 요청이 같거나 적다면")
                class IF_NOT_EXCEED_DEFAULT_STOCK {

                    @ParameterizedTest
                    @ValueSource(ints = {1, 2, 3, 4, 5})
                    @DisplayName("일반 재고에서 그대로 차감한다.")
                    void decreasePromotionStock(int userRequestSize) {
                        //given
                        Product chocoTPO = Product.of("초코바", 1200, 5, expired, true, false);
                        Product chocoNON = Product.of("초코바", 1200, 5, Promotion.createNone(), false, false);
                        StoreSuggestion chocoSuggestion = StoreSuggestion.of(List.of(chocoTPO, chocoNON), userRequestSize);
                        chocoSuggestion.changeSuggestion(SuggestionType.NONE);

                        ConfirmedProduct confirmedProduct = ConfirmedProduct.of(chocoSuggestion, null);
                        int before = chocoNON.getCurrentQuantity();
                        //when
                        stockService.updateStock(confirmedProduct);

                        //then
                        Assertions.assertThat(chocoNON.getCurrentQuantity())
                                .isEqualTo(before - userRequestSize);
                    }
                }
            }
        }
    }
}