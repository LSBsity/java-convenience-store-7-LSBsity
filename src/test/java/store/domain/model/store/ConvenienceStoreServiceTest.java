package store.domain.model.store;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import store.common.constant.StoreConst;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.invoice.Invoice;
import store.common.testunit.TestConfirmedProduct;

import java.time.LocalDate;
import java.util.List;

@DisplayName("⭐편의점 서비스 테스트")
class ConvenienceStoreServiceTest {

    StoreManager convenienceStore;

    static int USER_REQUEST_SIZE = 14;
    static int USER_REQUEST_SIZE2 = 2;
    static int USER_REQUEST_SIZE3 = 15;

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
        convenienceStore = new StoreManager(currentProducts, new SuggestionService(), new StockService());
    }

    @Nested
    @DisplayName("계산서 발행 테스트")
    class CheckTest {

        @Nested
        @DisplayName("issueInvoice 메서드는")
        class checkMethodTest {

            @Nested
            @DisplayName("프로모션 상품만 있을 시")
            class IfAllPromotionProduct {
                @Test
                @DisplayName("멤버십 할인은 적용되지 않는다.")
                void notApplyMembershipSale() {
                    //given
                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(StoreSuggestion.of(List.of(cokeOPO, cokeNON), USER_REQUEST_SIZE), null);
                    TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct);
                    List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();
                    //when
                    Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, UserAnswer.YES);

                    //then
                    Assertions.assertThat(invoice.getMembershipDiscountAmount()).isEqualTo(0);
                }

                @Test
                @DisplayName("프로모션 할인이 적용되어야 한다.(1)")
                void applyPromotionSale() {
                    //given
                    StoreSuggestion storeSuggestion = StoreSuggestion.of(List.of(cokeOPO, cokeNON), USER_REQUEST_SIZE);
                    ConfirmedProduct confirmedProduct = ConfirmedProduct.of(storeSuggestion, null);
                    TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct);
                    List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();
                    //when
                    Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, UserAnswer.YES);

                    //then
                    Assertions.assertThat(invoice.getPromotionDiscountAmount()).isEqualTo(5000);
                    Assertions.assertThat(invoice.getTotalPrice()).isEqualTo(9000);
                }

                @Test
                @DisplayName("프로모션 할인이 적용되어야 한다.(2)")
                void applyPromotionSale2() {
                    //given
                    ConfirmedProduct confirmedProduct1 = ConfirmedProduct.of(StoreSuggestion.of(List.of(ciderTPO, ciderNON), USER_REQUEST_SIZE3), null);
                    ConfirmedProduct confirmedProduct2 = ConfirmedProduct.of(StoreSuggestion.of(List.of(lunchBoxNON), USER_REQUEST_SIZE2), null);
                    TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct1, confirmedProduct2);
                    List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();
                    //when
                    Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, UserAnswer.YES);

                    //then
                    Assertions.assertThat(invoice.getPromotionDiscountAmount()).isEqualTo(2000);
                    Assertions.assertThat(invoice.getMembershipDiscountAmount()).isEqualTo(3840);
                    Assertions.assertThat(invoice.getTotalPrice()).isEqualTo(21960);
                }
            }

            @ParameterizedTest
            @EnumSource(value = UserAnswer.class, names = {"YES", "NO"})
            @DisplayName("유저가 멤버십 할인을 선택해야 멤버십 할인을 적용한다.")
            void membershipChoose(UserAnswer userAnswer) {
                //given
                ConfirmedProduct confirmedProduct = ConfirmedProduct.of(StoreSuggestion.of(List.of(lunchBoxNON), USER_REQUEST_SIZE2), null);
                TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct);
                List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();
                //when
                Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, userAnswer);

                //then
                if (userAnswer.equals(UserAnswer.YES)) {
                    Assertions.assertThat(invoice.getMembershipDiscountAmount()).isEqualTo(3840);
                }
                if (userAnswer.equals(UserAnswer.NO)) {
                    Assertions.assertThat(invoice.getMembershipDiscountAmount()).isEqualTo(0);
                }
            }
        }

        @Nested
        @DisplayName("계산서에 프로모션 상품이 없을 시")
        class NotExistPromotionProduct {

            @Test
            @DisplayName("증정 목록이 비어있어야 한다.")
            void shouldNotExistGiftProducts() {
                //given
                ConfirmedProduct confirmedProduct = ConfirmedProduct.of(StoreSuggestion.of(List.of(lunchBoxNON), USER_REQUEST_SIZE2), null);
                TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct);
                List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();
                //when
                Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, UserAnswer.YES);

                //then
                Assertions.assertThat(invoice.printGifts()).isEmpty();
            }

            @Test
            @DisplayName("내실돈은 총구매액에 멤버십 할인을 적용한 금액이여야 한다.")
            void payAmountIsShouldBeTotalDivideMembershipDiscount() {
                //given
                ConfirmedProduct confirmedProduct = ConfirmedProduct.of(StoreSuggestion.of(List.of(lunchBoxNON), USER_REQUEST_SIZE2), null);
                TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct);
                List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();
                //when
                Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, UserAnswer.YES);

                //then
                int nonDiscountedPrice = invoice.getOriginalPrice();
                int membershipDiscountPrice = (int) (nonDiscountedPrice * StoreConst.MEMBERSHIP_DISCOUNT_RATE);

                int totalPrice = invoice.getTotalPrice();

                Assertions.assertThat(totalPrice).isEqualTo(nonDiscountedPrice - membershipDiscountPrice);
            }
        }

        @Nested
        @DisplayName("계산서에 프로모션 상품이 있을 시")
        class ExistPromotionProduct {
            @Test
            @DisplayName("증정 목록에 프로모션 상품만 존재해야 한다.")
            void giftProductsSholdExist() {
                //given
                int cokeRequestSize = 3;
                int lunchBoxRequestSize = 2;
                ConfirmedProduct confirmedProduct1 = ConfirmedProduct.of(StoreSuggestion.of(List.of(cokeOPO, cokeNON), cokeRequestSize), null);
                ConfirmedProduct confirmedProduct2 = ConfirmedProduct.of(StoreSuggestion.of(List.of(lunchBoxNON), lunchBoxRequestSize), null);
                TestConfirmedProduct testConfirmedProduct = TestConfirmedProduct.createTestConfirmedProduct(confirmedProduct1, confirmedProduct2);
                List<ConfirmedProduct> confirmedProducts = testConfirmedProduct.getTestConfirmedProducts();

                //when
                Invoice invoice = convenienceStore.issueInvoice(confirmedProducts, UserAnswer.YES);

                //then
                int promotionDefaultQuantity = cokeOPO.getPromotionDefaultQuantity();
                Assertions.assertThat(invoice.printGifts())
                        .contains(cokeOPO.getName())
                        .contains(String.valueOf(cokeRequestSize / promotionDefaultQuantity));
                Assertions.assertThat(invoice.printGifts())
                        .doesNotContain(lunchBoxNON.getName());
                Assertions.assertThat(invoice.printPurchasedProduct())
                        .contains(cokeOPO.getName());
                Assertions.assertThat(invoice.printPurchasedProduct())
                        .contains(lunchBoxNON.getName());
            }
        }
    }
}


