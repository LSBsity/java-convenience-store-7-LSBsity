package store.domain.model.store.invoice;

import store.common.constant.InvoicePrintConst;
import store.common.constant.StoreConst;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Invoice {

    private final List<ProductInfo> purchasedProducts;
    private final List<ProductInfo> giftProducts;

    private int nonDiscountedPrice = 0;
    private int promotionDiscountedAmount = 0;
    private int membershipDiscountAmount = 0;
    private int totalPrice = 0;

    public Invoice(List<ProductInfo> purchasedProducts, List<ProductInfo> giftProducts) {
        this.purchasedProducts = purchasedProducts;
        this.giftProducts = giftProducts;
    }

    public static Invoice issue(final List<ConfirmedProduct> confirmedProducts) {
        List<ProductInfo> purchasedProducts = new ArrayList<>();
        List<ProductInfo> giftProducts = new ArrayList<>();

        for (ConfirmedProduct confirmedWishList : confirmedProducts) {
            addGiftProduct(confirmedWishList, giftProducts);
            addNormalProduct(confirmedWishList, purchasedProducts);
        }

        return new Invoice(purchasedProducts, giftProducts);
    }

    public void takeSummary(final UserAnswer isMemberShip) {
        int nondiscountedPrice = calculateNonDiscountedPrice(); //차감 전 금액
        int totalGiftPrice = calculateTotalGiftPrice();         //차감할 금액
        setNonDiscountedPrice(nondiscountedPrice);
        setMembershipDiscountPrice(isMemberShip);
        setPromotionDiscountedPrice(totalGiftPrice);
        setTotalPrice(nondiscountedPrice - promotionDiscountedAmount - membershipDiscountAmount);
    }
    /**
     * 영수증에 구매한 물품 표시를 위한 리스트 저장
     */
    private static void addNormalProduct(final ConfirmedProduct confirmedWishList, final List<ProductInfo> purchasedProducts) {
        int userRequestSize = confirmedWishList.getUserRequestSize();
        if (userRequestSize == 0) return;

        Product normalProduct = confirmedWishList.getProduct();
        ProductInfo product = ProductInfo.of(normalProduct, userRequestSize);

        purchasedProducts.add(product);
    }

    /**
     * 영수증에 증정된 물품 표시를 위한 리스트 저장
     */
    private static void addGiftProduct(final ConfirmedProduct confirmedWishList, final List<ProductInfo> giftProducts) {
        if (!confirmedWishList.isAvailablePromotion()) return;

        int userGiftQuantity = getGiftProductQuantity(confirmedWishList);
        if (userGiftQuantity == 0) return;

        Product promotionProduct = confirmedWishList.getPromotionProduct();
        ProductInfo product = ProductInfo.of(promotionProduct, userGiftQuantity);

        giftProducts.add(product);
    }

    private static int getGiftProductQuantity(final ConfirmedProduct confirmedWishList) {
        int promotionStock = confirmedWishList.getPromotionStock();
        int userRequestSize = confirmedWishList.getUserRequestSize();
        int promotionDefaultQuantity = confirmedWishList.getPromotionDefaultQuantity();

        int userEligibleGiftProductQuantity = userRequestSize / promotionDefaultQuantity;
        int giftLimit = promotionStock / promotionDefaultQuantity;
        int userGiftQuantity = Math.min(userEligibleGiftProductQuantity, giftLimit);
        return userGiftQuantity;
    }

    public void setNonDiscountedPrice(final int nonDiscountedPrice) {
        this.nonDiscountedPrice = nonDiscountedPrice;
    }

    public void setPromotionDiscountedPrice(final int promotionDiscountedAmount) {
        this.promotionDiscountedAmount += promotionDiscountedAmount;
    }

    public void setMembershipDiscountAmount(final int membershipDiscountAmount) {
        this.membershipDiscountAmount += membershipDiscountAmount;
    }

    public int getNonDiscountedPrice() {
        return nonDiscountedPrice;
    }

    public void setTotalPrice(final int finalAmount) {
        this.totalPrice += finalAmount;
    }

    public int getPromotionDiscountedAmount() {
        return promotionDiscountedAmount;
    }

    public int getMembershipDiscountAmount() {
        return membershipDiscountAmount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    private void setMembershipDiscountPrice(final UserAnswer isMemberShip) {
        if (isMemberShip == UserAnswer.NO) return;

        int membershipDiscountPrice = calculateMembershipDiscount();
        setMembershipDiscountAmount(membershipDiscountPrice);
    }

    private int calculateNonDiscountedPrice() {
        return purchasedProducts.stream()
                .mapToInt(ProductInfo::calculateTotalPrice)
                .sum();
    }

    private int calculateTotalGiftPrice() {
        return giftProducts.stream()
                .mapToInt(productPair -> productPair.getPrice() * productPair.getSize())
                .sum();
    }

    /**
     * 프로모션이 아니거나 기간이 지난 상품에 대해서만 멤버십 할인 적용
     */
    private int calculateMembershipDiscount() {
        return (int) (purchasedProducts.stream()
                .filter(Invoice::isMembershipDiscountable)
                .mapToInt(ProductInfo::calculateTotalPrice)
                .sum() * StoreConst.MEMBERSHIP_DISCOUNT_RATE);
    }

    private static boolean isMembershipDiscountable(final ProductInfo productPair) {
        return productPair.inNotPromoted() || productPair.isExpired();
    }

    public int getTotalQuantity() {
        return this.purchasedProducts.stream()
                .mapToInt(ProductInfo::getSize)
                .sum();
    }

    public String printSummary() {
        return String.format(InvoicePrintConst.NO_DISCOUNTED_PRICE, InvoicePrintConst.NO_DISCOUNTED_PRICE_NAME, getTotalQuantity(), getNonDiscountedPrice()) +
                String.format(InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE, InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE_NAME, getPromotionDiscountedAmount()) +
                String.format(InvoicePrintConst.MEMBERSHIP_DISCOUNTED_PRICE, InvoicePrintConst.MEMBERSHIP_DISCOUNTED_PRICE_NAME, getMembershipDiscountAmount()) +
                String.format(InvoicePrintConst.TOTAL_PRICE, InvoicePrintConst.TOTAL_PRICE_NAME, getTotalPrice());
    }

    public String printPurchasedProduct() {
        return this.purchasedProducts.stream()
                .map(ProductInfo::printPurchased)
                .collect(Collectors.joining());
    }

    public String printGifts() {
        return this.giftProducts.stream()
                .map(ProductInfo::printGift)
                .collect(Collectors.joining());
    }
}