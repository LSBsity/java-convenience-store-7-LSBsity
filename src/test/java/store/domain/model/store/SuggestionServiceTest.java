package store.domain.model.store;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.SuggestionType;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.servce.suggestion.SuggestionService;

import java.time.LocalDate;
import java.util.List;

@DisplayName("⭐고객 제안 서비스 테스트")
class SuggestionServiceTest {

    SuggestionService suggestionService = new SuggestionService();

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

    Product cokeOPO = Product.of("콜라", 1000, 10, onePlusOne, true, false);
    Product cokeNON = Product.of("콜라", 1000, 10, Promotion.createNone(), false, false);
    Product ciderTPO = Product.of("사이다", 1000, 8, twoPlusone, true, false);
    Product ciderNON = Product.of("사이다", 1000, 7, Promotion.createNone(), true, false);
    Product juiceTPO = Product.of("오렌지주스", 1800, 9, twoPlusone, true, false);
    Product juiceNON = Product.of("오렌지주스", 1800, 0, Promotion.createNone(), false, false);
    Product sparklingWaterTPO = Product.of("탄산수", 1200, 5, twoPlusone, true, false);
    Product vitaminWaterTPO = Product.of("비타민워터", 1500, 7, twoPlusone, true, false);
    Product lunchBoxNON = Product.of("정식도시락", 6400, 8, Promotion.createNone(), false, false);


    @BeforeEach
    void setUp() {
        currentProducts = CurrentProducts.create(List.of(cokeOPO, cokeNON, ciderTPO, ciderNON, juiceTPO, juiceNON, sparklingWaterTPO, vitaminWaterTPO, lunchBoxNON));
    }


    @Nested
    @DisplayName("고객에게 프로모션 안내와 수량 조정을 권하는 메서드인")
    class SuggestTest {
        @Nested
        @DisplayName("suggest 메서드는")
        class SuggestMethodTest {
            @Nested
            @DisplayName("프로모션에 해당되는 물품들이 정상적으로 요청되면")
            class IfExactProducts {

                @ParameterizedTest
                @CsvSource(value = {"2, 3, 3", "2, 6, 9", "6, 6, 9", "4, 6, 6", "6, 6, 9"})
                @DisplayName("모든 Suggestion이 ALREADY_ELIGIBLE이어야 한다.")
                void allSuggestionHaveToAlreadyEligible(int cokeQuantity, int ciderQuantity, int juiceQuantity) {
                    //given
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), cokeQuantity);
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), ciderQuantity);
                    StoreSuggestion juiceSuggestion = StoreSuggestion.of(List.of(juiceTPO, juiceNON), juiceQuantity);
                    List<StoreSuggestion> storeSuggestions = List.of(cokeSuggestion, ciderSuggestion, juiceSuggestion);

                    //when
                    suggestionService.suggest(storeSuggestions);

                    //then
                    storeSuggestions.forEach(suggest ->
                            Assertions.assertThat(suggest.getSuggestionType())
                                    .isEqualTo(SuggestionType.ALREADY_ELIGIBLE));
                }

                @ParameterizedTest
                @CsvSource(value = {"2, 3, 3", "2, 6, 9", "6, 6, 9", "4, 6, 6", "6, 6, 9"})
                @DisplayName("이벤트로 더 받을 수 있거나 할인 적용이 되지 않는 물품 개수가 0이여야 한다.")
                void all(int cokeQuantity, int ciderQuantity, int juiceQuantity) {
                    //given
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), cokeQuantity);
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), ciderQuantity);
                    StoreSuggestion juiceSuggestion = StoreSuggestion.of(List.of(juiceTPO, juiceNON), juiceQuantity);
                    List<StoreSuggestion> storeSuggestions = List.of(cokeSuggestion, ciderSuggestion, juiceSuggestion);

                    //when
                    suggestionService.suggest(storeSuggestions);

                    //then
                    storeSuggestions.forEach(suggest ->
                            Assertions.assertThat(suggest.getOfferSize())
                                    .isEqualTo(0)
                    );
                }
            }

            @Nested
            @DisplayName("프로모션에 해당되는 물품이지만")
            class IfNotExactProducts {

                @Nested
                @DisplayName("추가로 받을 수 있는 물품을 빼먹고 요청하면")
                class IfEligibleAdditionalOffer {

                    @ParameterizedTest
                    @CsvSource(value = {"1, 2, 2", "3, 5, 8", "1, 5, 8", "7, 2, 5", "9, 2, 8"})
                    @DisplayName("Suggestion이 ADDITIONAL_FREE_PRODUCT이어야 한다")
                    void allSuggestionHaveToAdditionalOffer(int cokeQuantity, int ciderQuantity, int juiceQuantity) {
                        //given
                        StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), cokeQuantity);
                        StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), ciderQuantity);
                        StoreSuggestion juiceSuggestion = StoreSuggestion.of(List.of(juiceTPO, juiceNON), juiceQuantity);
                        List<StoreSuggestion> storeSuggestions = List.of(cokeSuggestion, ciderSuggestion, juiceSuggestion);

                        //when
                        suggestionService.suggest(storeSuggestions);

                        //then
                        storeSuggestions.forEach(suggest ->
                                Assertions.assertThat(
                                        suggest.getSuggestionType()).isEqualTo(SuggestionType.ADDITIONAL_FREE_PRODUCT)
                        );
                    }
                }
            }

            @Nested
            @DisplayName("추가로 받을 수 있는 물품이 있어도 재고가 없으면")
            class IfEligibleButStockIsLimited {
                @ParameterizedTest
                @CsvSource(value = {"10, 11", "11, 12", "12, 13", "13,14"})
                @DisplayName("Suggestion이 INSUFFICIENT_PROMOTION_STOCK이여야 한다")
                void allSuggestionHaveToInsufficient(int ciderQuantity, int cokeQuantity) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), ciderQuantity);
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), cokeQuantity);
                    List<StoreSuggestion> storeSuggestions = List.of(ciderSuggestion, cokeSuggestion);

                    //when
                    suggestionService.suggest(storeSuggestions);

                    //then
                    storeSuggestions.forEach(storeSuggestion ->
                            Assertions.assertThat(storeSuggestion.getSuggestionType())
                                    .isEqualTo(SuggestionType.INSUFFICIENT_PROMOTION_STOCK)
                    );
                }
            }

            @Nested
            @DisplayName("2+1 프로모션에서 4개, 7개, 10개와 같이 추가 혜택을 받지 못하는 경우")
            class IfEligibleAdditionalOffer {

                @ParameterizedTest
                @CsvSource(value = {"1, 4, 4", "4, 1, 4", "7, 4, 4", "4, 7, 4", "4, 1, 1"})
                @DisplayName("Suggestion이 EXCESSIVE_ADDITIONAL_PURCHASE이여야 한다")
                void allSuggestionHaveToAdditionalOffer(int ciderQuantity, int juiceQuantity, int sparklingWaterQuantity) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), ciderQuantity);
                    StoreSuggestion juiceSuggestion = StoreSuggestion.of(List.of(juiceTPO, juiceNON), juiceQuantity);
                    StoreSuggestion sparklingWaterSuggestion = StoreSuggestion.of(List.of(sparklingWaterTPO), sparklingWaterQuantity);
                    List<StoreSuggestion> storeSuggestions = List.of(sparklingWaterSuggestion, ciderSuggestion, juiceSuggestion);

                    //when
                    suggestionService.suggest(storeSuggestions);

                    //then
                    storeSuggestions.forEach(suggest ->
                            Assertions.assertThat(
                                    suggest.getSuggestionType()).isEqualTo(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE)
                    );
                }
            }
        }
    }

    @Nested
    @DisplayName("고객의 대답에 따라 요청 수량을 바꾸는")
    class AdjustTest {

        @Nested
        @DisplayName("adjustUserRequestQuantity 메서드는")
        class AdjustUserRequestQuantity {

            @Nested
            @DisplayName("ADDITIONAL_FREE_PRODUCT이고 대답이 YES라면")
            class IF_ADDITIONAL_FREE_PRODUCT_AND_USER_YES {

                final static UserAnswer userAnswer = UserAnswer.YES;

                @ParameterizedTest
                @ValueSource(ints = {1, 3, 5, 7})
                @DisplayName("요청 수량을 하나 올린다.(1+1)")
                void addOne1(int userRequestSize) {
                    //given
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                    cokeSuggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, userAnswer);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize + 1);
                }

                @ParameterizedTest
                @ValueSource(ints = {2, 5})
                @DisplayName("2+1을 받도록 요청 수량을 하나 올린다.(2+1)")
                void addOne(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, UserAnswer.YES);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize + 1);
                }
            }

            @Nested
            @DisplayName("ADDITIONAL_FREE_PRODUCT이고 대답이 NO라면")
            class IF_ADDITIONAL_FREE_PRODUCT_AND_USER_NO {

                final static UserAnswer userAnswer = UserAnswer.NO;

                @ParameterizedTest
                @ValueSource(ints = {1, 3, 5, 7})
                @DisplayName("수량을 바꾸지 않는다.(1+1)")
                void addOne1(int userRequestSize) {
                    //given
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                    cokeSuggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, userAnswer);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize);
                }

                @ParameterizedTest
                @ValueSource(ints = {2, 5})
                @DisplayName("수량을 바꾸지 않는다.(2+1)")
                void addOne2(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.ADDITIONAL_FREE_PRODUCT);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, userAnswer);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize);
                }
            }

            @Nested
            @DisplayName("INSUFFICIENT_PROMOTION_STOCK이고 대답이 NO라면")
            class IF_INSUFFICIENT_PROMOTION_STOCK_AND_USER_NO {

                final static UserAnswer userAnswer = UserAnswer.NO;

                @ParameterizedTest
                @ValueSource(ints = {7, 8, 9})
                @DisplayName("프로모션 혜택이 가능한 만큼 요청 수량을 뺀다.")
                void changeToMaximumEligibleQuantity(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, userAnswer);

                    int promotionDefaultQuantity = confirmedProduct.getPromotionDefaultQuantity();
                    int promotionStock = confirmedProduct.getPromotionStock();
                    int maximum = promotionDefaultQuantity * (promotionStock / promotionDefaultQuantity);
                    int offerSize = userRequestSize - maximum;
                    ciderSuggestion.changeOfferSize(offerSize);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize - offerSize);
                }
            }

            @Nested
            @DisplayName("INSUFFICIENT_PROMOTION_STOCK이고 대답이 YES라면")
            class IF_INSUFFICIENT_PROMOTION_STOCK_AND_USER_YES {

                final static UserAnswer userAnswer = UserAnswer.YES;

                @ParameterizedTest
                @ValueSource(ints = {7, 8, 9})
                @DisplayName("요청 수량이 변하지 않아야 한다.")
                void changeToMaximumEligibleQuantity(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.INSUFFICIENT_PROMOTION_STOCK);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, userAnswer);

                    int promotionDefaultQuantity = confirmedProduct.getPromotionDefaultQuantity();
                    int promotionStock = confirmedProduct.getPromotionStock();
                    int maximum = promotionDefaultQuantity * (promotionStock / promotionDefaultQuantity);
                    int offerSize = userRequestSize - maximum;
                    ciderSuggestion.changeOfferSize(offerSize);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize);
                }
            }

            @Nested
            @DisplayName("EXCESSIVE_ADDITIONAL_PURCHASE이고 대답이 NO라면")
            class IF_EXCESSIVE_ADDITIONAL_PURCHASE_AND_USER_NO {

                final static UserAnswer userAnswer = UserAnswer.NO;

                @ParameterizedTest
                @ValueSource(ints = {7, 8, 9})
                @DisplayName("2+1 혜택만 받을 수 있게 요청 재고를 1 감소시킨다.")
                void decreaseOneToEligibleTwoPlusOne(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, userAnswer);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize - 1);
                }
            }

            @Nested
            @DisplayName("EXCESSIVE_ADDITIONAL_PURCHASE이고 대답이 YES라면")
            class IF_EXCESSIVE_ADDITIONAL_PURCHASE_AND_USER_YES {

                final static UserAnswer userAnswer = UserAnswer.YES;

                @ParameterizedTest
                @ValueSource(ints = {7, 8, 9})
                @DisplayName("나머지 하나는 혜택을 받지 못해도 요청 재고를 줄이지 않는다.")
                void decreaseOneToEligibleTwoPlusOne(int userRequestSize) {
                    //given
                    StoreSuggestion ciderSuggestion = StoreSuggestion.of(List.of(ciderTPO, ciderNON), userRequestSize);
                    ciderSuggestion.changeSuggestion(SuggestionType.EXCESSIVE_ADDITIONAL_PURCHASE);

                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(ciderSuggestion, userAnswer);

                    //when
                    suggestionService.adjustUserRequestQuantity(confirmedProduct);

                    //then
                    Assertions.assertThat(confirmedProduct.getUserRequestSize()).isEqualTo(userRequestSize);
                }
            }
        }

    }
}