package store.domain.model.store;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import store.domain.model.dto.ConfirmedWishList;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.Suggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;
import store.domain.model.promotion.UserAnswer;
import store.domain.view.OutputView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DisplayName("⭐편의점 기능 테스트")
class ConvenienceStoreTest {

    ConvenienceStore convenienceStore;
    List<ConfirmedWishList> confirmedWishLists = new ArrayList<>();
    List<ConfirmedWishList> confirmedWishLists2 = new ArrayList<>();
    List<ConfirmedWishList> confirmedWishLists3 = new ArrayList<>();
    public static int USER_REQUEST_SIZE = 14;
    public static int USER_REQUEST_SIZE2 = 2;
    public static int USER_REQUEST_SIZE3 = 15;

    @BeforeEach
    void setUp() {
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

        Product product1 = Product.of("콜라", 1000, 10, onePlusOne, true);
        Product product2 = Product.of("콜라", 1000, 10, Promotion.createNone(), false);
        Product product3 = Product.of("사이다", 1000, 8, twoPlusone, true);
        Product product4 = Product.of("사이다", 1000, 7, Promotion.createNone(), true);
        Product product5 = Product.of("오렌지주스", 1800, 9, twoPlusone, true);
        Product product6 = Product.of("오렌지주스", 1800, 0, Promotion.createNone(), false);
        Product product7 = Product.of("탄산수", 1200, 5, twoPlusone, true);
        Product product8 = Product.of("비타민워터", 1500, 7, twoPlusone, true);
        Product product9 = Product.of("정식도시락", 6400, 8, Promotion.createNone(), false);

        CurrentProducts currentProducts = CurrentProducts.create(
                List.of(product1, product2, product3, product4, product5, product6, product7, product8, product9)
        );
        convenienceStore = new ConvenienceStore(currentProducts);

        confirmedWishLists.add(ConfirmedWishList.of(List.of(product1, product2), USER_REQUEST_SIZE));
        confirmedWishLists2.add(ConfirmedWishList.of(List.of(product9), USER_REQUEST_SIZE2));
        confirmedWishLists3.add(ConfirmedWishList.of(List.of(product3, product4), USER_REQUEST_SIZE3));
        confirmedWishLists3.add(ConfirmedWishList.of(List.of(product9), USER_REQUEST_SIZE2));
    }


    @Nested
    @DisplayName("프로모션 적용과 제안 테스트")
    class PromotionAdjustTest {
        @Nested
        @DisplayName("suggest 메서드는")
        class SuggestMethodTest {
            @Nested
            @DisplayName("프로모션에 해당되는 물품들이 정상적으로 요청되면")
            class IfExactProducts {

                @ParameterizedTest
                @CsvSource(value = {"2, 3, 3", "2, 6, 9", "6, 6, 9", "4, 6, 6", "6, 6, 9"})
                @DisplayName("모든 Suggestion이 ALREADY_ELIGIBLE이어야 한다.")
                void allSuggestionHaveToAlreadyEligible(int cockQuantity, int ciderQuantity, int juiceQuantity) {
                    //given
                    UserWish.Request coke = UserWish.Request.of("콜라", cockQuantity);
                    UserWish.Request cider = UserWish.Request.of("사이다", ciderQuantity);
                    UserWish.Request juice = UserWish.Request.of("오렌지주스", juiceQuantity);
                    List<UserWish.Request> wishList = List.of(coke, cider, juice);

                    //when
                    List<StoreSuggestion> suggests = convenienceStore.suggest(wishList);

                    //then
                    Assertions.assertThat(suggests.size()).isEqualTo(wishList.size());
                    suggests.forEach(suggest -> {
                                Assertions.assertThat(suggest.getSuggestion()).isEqualTo(Suggestion.ALREADY_ELIGIBLE);
                            }
                    );
                }

                @ParameterizedTest
                @CsvSource(value = {"2, 3, 3", "2, 6, 9", "6, 6, 9", "4, 6, 6", "6, 6, 9"})
                @DisplayName("이벤트로 더 받을 수 있거나 할인 적용이 되지 않는 물품 개수가 0이여야 한다.")
                void all(int cockQuantity, int ciderQuantity, int juiceQuantity) {
                    //given
                    UserWish.Request coke = UserWish.Request.of("콜라", cockQuantity);
                    UserWish.Request cider = UserWish.Request.of("사이다", ciderQuantity);
                    UserWish.Request juice = UserWish.Request.of("오렌지주스", juiceQuantity);
                    List<UserWish.Request> wishList = List.of(coke, cider, juice);

                    //when
                    List<StoreSuggestion> suggests = convenienceStore.suggest(wishList);

                    //then
                    Assertions.assertThat(suggests.size()).isEqualTo(wishList.size());
                    suggests.forEach(suggest -> {
                                Assertions.assertThat(suggest.getOfferSize()).isEqualTo(0);
                            }
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
                        UserWish.Request coke = UserWish.Request.of("콜라", cokeQuantity);
                        UserWish.Request cider = UserWish.Request.of("사이다", ciderQuantity);
                        UserWish.Request juice = UserWish.Request.of("오렌지주스", juiceQuantity);
                        List<UserWish.Request> wishList = List.of(coke, cider, juice);

                        //when
                        List<StoreSuggestion> suggests = convenienceStore.suggest(wishList);

                        //then
                        Assertions.assertThat(suggests.size()).isEqualTo(wishList.size());
                        suggests.forEach(suggest ->
                                Assertions.assertThat(
                                        suggest.getSuggestion()).isEqualTo(Suggestion.ADDITIONAL_FREE_PRODUCT)
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
                    UserWish.Request cider = UserWish.Request.of("사이다", ciderQuantity);
                    UserWish.Request coke = UserWish.Request.of("콜라", cokeQuantity);
                    List<UserWish.Request> wishList = List.of(cider, coke);
                    int cinderPromotionStockQuantity = 6;
                    int cokePromotionStockQuantity = 10;

                    //when
                    List<StoreSuggestion> suggests = convenienceStore.suggest(wishList);

                    //then
                    Assertions.assertThat(suggests.size()).isEqualTo(wishList.size());

                    StoreSuggestion ciderSuggest = suggests.get(0);
                    Assertions.assertThat(ciderSuggest.getSuggestion())
                            .isEqualTo(Suggestion.INSUFFICIENT_PROMOTION_STOCK);
                    Assertions.assertThat(ciderSuggest.getOfferSize())
                            .isEqualTo(ciderQuantity - cinderPromotionStockQuantity);

                    StoreSuggestion cokeSuggest = suggests.get(1);
                    Assertions.assertThat(cokeSuggest.getSuggestion())
                            .isEqualTo(Suggestion.INSUFFICIENT_PROMOTION_STOCK);
                    Assertions.assertThat(cokeSuggest.getOfferSize())
                            .isEqualTo(cokeQuantity - cokePromotionStockQuantity);
                }
            }
        }
    }

    @Nested
    @DisplayName("계산서 발행 테스트")
    class CheckTest {

        @Nested
        @DisplayName("check 메서드는")
        class checkMethodTest {

            @Nested
            @DisplayName("프로모션 상품만 있을 시")
            class IfAllPromotionProduct {
                @Test
                @DisplayName("멤버십 할인은 적용되지 않는다.")
                void notApplyMembershipSale() {
                    //given
                    //when
                    Invoice invoice = convenienceStore.check(confirmedWishLists, UserAnswer.YES);

                    //then
                    Assertions.assertThat(invoice.getMembershipDiscount()).isEqualTo(0);
                }

                @Test
                @DisplayName("프로모션 할인이 적용되어야 한다.")
                void applyPromotionSale() {
                    //given
                    //when
                    Invoice invoice = convenienceStore.check(confirmedWishLists, UserAnswer.YES);

                    //then
                    Assertions.assertThat(invoice.getPromotionDiscount()).isEqualTo(5000);
                    Assertions.assertThat(invoice.getTotalPrice()).isEqualTo(9000);
                }

                @Test
                @DisplayName("프로모션 할인이 적용되어야 한다.2")
                void applyPromotionSale2() {
                    //given
                    //when
                    Invoice invoice = convenienceStore.check(confirmedWishLists3, UserAnswer.YES);

                    //then
                    OutputView outputView = new OutputView();
                    outputView.showInvoice(invoice);
                    Assertions.assertThat(invoice.getPromotionDiscount()).isEqualTo(2000);
                    Assertions.assertThat(invoice.getMembershipDiscount()).isEqualTo(3840);
                    Assertions.assertThat(invoice.getTotalPrice()).isEqualTo(21960);
                }
            }

            @ParameterizedTest
            @EnumSource(value = UserAnswer.class, names = {"YES", "NO"})
            @DisplayName("유저가 멤버십 할인을 선택해야 멤버십 할인을 적용한다.")
            public void membershipChoose(UserAnswer userAnswer) {
                //given
                //when
                Invoice invoice = convenienceStore.check(confirmedWishLists2, userAnswer);

                //then
                if (userAnswer.equals(UserAnswer.YES)) {
                    Assertions.assertThat(invoice.getMembershipDiscount()).isEqualTo(3840);
                }
                if (userAnswer.equals(UserAnswer.NO)) {
                    Assertions.assertThat(invoice.getMembershipDiscount()).isEqualTo(0);
                }
            }
        }
    }
}


