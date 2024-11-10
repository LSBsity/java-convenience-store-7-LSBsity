package store.domain.model.store.invoice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.common.constant.StoreConst;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;
import store.domain.model.promotion.UserAnswer;

import java.time.LocalDate;
import java.util.List;


@DisplayName("⭐영수증 발행 테스트")
class InvoiceTest {


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
    Product chocoEx;

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
        chocoEx = Product.of("초코바", 1200, 5, expired, true, false);

        currentProducts = CurrentProducts.create(List.of(cokeOPO, cokeNON, ciderTPO, ciderNON, juiceTPO, juiceNON, sparklingWaterTPO, vitaminWaterTPO, lunchBoxNON));
    }

    @Nested
    @DisplayName("영수증을 발행하는 issue 메서드는")
    class IssueMethodTest {

        @Nested
        @DisplayName("구매한 물품에 프로모션 물품이 있다면")
        class IfExistPromotionProduct {
            @ParameterizedTest
            @ValueSource(ints = {2, 4, 6})
            @DisplayName("증정란에 증정받은 물품과 개수를 표시한다.")
            void saveDefaultAndGiftProductBoth(int userRequestSize) {
                //given
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                //when
                Invoice invoice = Invoice.issue(List.of(confirmedProduct));

                //then
                Assertions.assertThat(invoice.printPurchasedProduct())
                        .contains(cokeOPO.getName())
                        .contains(String.format("%,d", cokeOPO.getPrice() * userRequestSize));
                Assertions.assertThat(invoice.printGifts())
                        .contains(cokeNON.getName())
                        .contains(String.valueOf(userRequestSize / cokeOPO.getPromotionDefaultQuantity()));
            }
        }

        @Nested
        @DisplayName("구매한 물품에 프로모션 물품이 없다면")
        class IfNotExistPromotionProduct {
            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5})
            @DisplayName("증정란은 비어있어야 한다.")
            void saveDefaultAndGiftProductBoth(int userRequestSize) {
                //given
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
                ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                //when
                Invoice invoice = Invoice.issue(List.of(confirmedProduct));

                //then
                Assertions.assertThat(invoice.printPurchasedProduct())
                        .contains(lunchBoxNON.getName())
                        .contains(String.valueOf(userRequestSize))
                        .contains(String.format("%,d", lunchBoxNON.getPrice() * userRequestSize));
                Assertions.assertThat(invoice.printGifts()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("발행한 영수증을 요약하는 takeSummary 메서드는")
    class TakeSummaryMethodTest {

        @ParameterizedTest
        @ValueSource(ints = {2, 4, 6})
        @DisplayName("구매한 전체 수량와 할인 적용 전 가격인 총구매액이 계산되어야 한다.")
        void shouldCalculateQuantityAndPriceThatBeforeApplyDiscount(int userRequestSize) {
            StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
            ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

            StoreSuggestion lunchBoxSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
            ConfirmedProduct lunchBoxConfirmedProduct = ConfirmedProduct.of(lunchBoxSuggestion, null);

            Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, lunchBoxConfirmedProduct));

            //when
            invoice.takeSummary(null);

            //then
            Assertions.assertThat(invoice.getTotalQuantity())
                    .isEqualTo(userRequestSize * 2);

            int cokePrice = cokeOPO.getPrice() * userRequestSize;
            int lunchBoxPrice = lunchBoxNON.getPrice() * userRequestSize;
            Assertions.assertThat(invoice.getNonDiscountedPrice())
                    .isEqualTo(cokePrice + lunchBoxPrice);
        }

        @Nested
        @DisplayName("구매한 물품에 프로모션 물품이 있다면")
        class IfExistPromotionProduct {
            @ParameterizedTest
            @ValueSource(ints = {2, 4, 6})
            @DisplayName("행사할인란은 물품 가격 * 증정 개수 만큼 계산되어야 한다.")
            void saveDefaultAndGiftProductBoth(int userRequestSize) {
                //given
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                ConfirmedProduct confirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);
                Invoice invoice = Invoice.issue(List.of(confirmedProduct));

                int giftQuantity = userRequestSize / cokeOPO.getPromotionDefaultQuantity();

                //when
                invoice.takeSummary(null);

                //then
                Assertions.assertThat(invoice.getPromotionDiscountedAmount())
                        .isEqualTo(cokeOPO.getPrice() * giftQuantity);
            }
        }

        @Nested
        @DisplayName("구매한 물품에 프로모션 물품과 일반 물품이 같이 있다면")
        class IfExistPromotionAndDefaultProducts {
            @ParameterizedTest
            @ValueSource(ints = {2, 4, 6})
            @DisplayName("행사할인란은 프로모션 상품에 대해서만 계산되어야 한다.")
            void saveDefaultAndGiftProductBoth(int userRequestSize) {
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                StoreSuggestion lunchBoxSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
                ConfirmedProduct lunchBoxConfirmedProduct = ConfirmedProduct.of(lunchBoxSuggestion, null);

                Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, lunchBoxConfirmedProduct));

                int giftQuantity = userRequestSize / cokeOPO.getPromotionDefaultQuantity();

                //when
                invoice.takeSummary(null);

                //then
                Assertions.assertThat(invoice.getPromotionDiscountedAmount())
                        .isEqualTo(cokeOPO.getPrice() * giftQuantity);
            }
        }

        @Nested
        @DisplayName("멤버십 할인 적용 여부를 물어보고")
        class IfMembershipDiscountApply {

            @Nested
            @DisplayName("만약 고객이 멤버십 할인을 받는다고 하면 ")
            class CustomerRequestedApplyMembershipDiscount {

                UserAnswer userAnswer = UserAnswer.YES;

                @ParameterizedTest
                @ValueSource(ints = {2, 4, 6})
                @DisplayName("프로모션 금액을 제외한 일반 물품의 가격에서 30프로를 할인해야 한다.")
                void applyMembershipDiscountAtDefaultProduct(int userRequestSize) {
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                    ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                    StoreSuggestion lunchBoxSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
                    ConfirmedProduct lunchBoxConfirmedProduct = ConfirmedProduct.of(lunchBoxSuggestion, null);

                    Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, lunchBoxConfirmedProduct));


                    //when
                    invoice.takeSummary(userAnswer);

                    //then
                    int discountedPrice = (int) (lunchBoxNON.getPrice() * userRequestSize * StoreConst.MEMBERSHIP_DISCOUNT_RATE);
                    Assertions.assertThat(invoice.getMembershipDiscountAmount())
                            .isEqualTo(discountedPrice);
                }

                @ParameterizedTest
                @ValueSource(ints = {2, 4, 6})
                @DisplayName("프로모션이 지난 상품의 가격에서도 30프로를 할인해야 한다.")
                void applyMembershipDiscountAtPromotionProductThatExpired(int userRequestSize) {
                    StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                    ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                    StoreSuggestion chocoSuggestion = StoreSuggestion.of(List.of(chocoEx), userRequestSize);
                    ConfirmedProduct chocoConfirmedProduct = ConfirmedProduct.of(chocoSuggestion, null);

                    Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, chocoConfirmedProduct));

                    //when
                    invoice.takeSummary(userAnswer);

                    //then
                    int discountedPrice = (int) (chocoEx.getPrice() * userRequestSize * StoreConst.MEMBERSHIP_DISCOUNT_RATE);
                    Assertions.assertThat(invoice.getMembershipDiscountAmount())
                            .isEqualTo(discountedPrice);
                }
            }
        }

        @Nested
        @DisplayName("만약 고객이 멤버십 할인을 받지 않는다고 하면 ")
        class CustomerNotRequestedApplyMembershipDiscount {

            UserAnswer userAnswer = UserAnswer.NO;

            @ParameterizedTest
            @ValueSource(ints = {2, 4, 6})
            @DisplayName("멤버십 할인 가격은 0원이여야 한다.")
            void saveDefaultAndGiftProductBoth(int userRequestSize) {
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                StoreSuggestion lunchBoxSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
                ConfirmedProduct lunchBoxConfirmedProduct = ConfirmedProduct.of(lunchBoxSuggestion, null);

                Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, lunchBoxConfirmedProduct));

                //when
                invoice.takeSummary(userAnswer);

                //then
                Assertions.assertThat(invoice.getMembershipDiscountAmount())
                        .isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("총 게산된 금액은")
        class TotalPrice {

            @ParameterizedTest
            @ValueSource(ints = {2, 4, 6})
            @DisplayName("총구매액에서 행사 할인과 멤버십 할인 금액을 뺀 가격이여야 한다.")
            void TotalPriceShouldBeMinusPromotionAndMembershipDiscount(int userRequestSize) {
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                StoreSuggestion lunchBoxSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
                ConfirmedProduct lunchBoxConfirmedProduct = ConfirmedProduct.of(lunchBoxSuggestion, null);

                Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, lunchBoxConfirmedProduct));

                //when
                invoice.takeSummary(UserAnswer.YES);

                //then
                int nonDiscountedPrice = invoice.getNonDiscountedPrice();
                int promotionDiscountedAmount = invoice.getPromotionDiscountedAmount();
                int membershipDiscountAmount = invoice.getMembershipDiscountAmount();

                Assertions.assertThat(invoice.getTotalPrice())
                        .isEqualTo(nonDiscountedPrice - promotionDiscountedAmount - membershipDiscountAmount);
            }

            @ParameterizedTest
            @ValueSource(ints = {2, 4, 6})
            @DisplayName("멤버십 할인이 없다면 총구매액에서 행사 할인을 뺀 가격이여야 한다.")
            void TotalPriceShouldBeMinusPromotionDiscount(int userRequestSize) {
                StoreSuggestion cokeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), userRequestSize);
                ConfirmedProduct cokeConfirmedProduct = ConfirmedProduct.of(cokeSuggestion, null);

                StoreSuggestion lunchBoxSuggestion = StoreSuggestion.of(List.of(lunchBoxNON), userRequestSize);
                ConfirmedProduct lunchBoxConfirmedProduct = ConfirmedProduct.of(lunchBoxSuggestion, null);

                Invoice invoice = Invoice.issue(List.of(cokeConfirmedProduct, lunchBoxConfirmedProduct));

                //when
                invoice.takeSummary(UserAnswer.NO);

                //then
                int nonDiscountedPrice = invoice.getNonDiscountedPrice();
                int promotionDiscountedAmount = invoice.getPromotionDiscountedAmount();
                int membershipDiscountAmount = invoice.getMembershipDiscountAmount();

                Assertions.assertThat(membershipDiscountAmount)
                        .isEqualTo(0);
                Assertions.assertThat(invoice.getTotalPrice())
                        .isEqualTo(nonDiscountedPrice - promotionDiscountedAmount);
            }
        }
    }
}