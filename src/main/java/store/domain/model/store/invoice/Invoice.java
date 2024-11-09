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

    private int originalPrice = 0;
    private int promotionDiscountAmount = 0;
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

    private static void addNormalProduct(final ConfirmedProduct confirmedWishList, final List<ProductInfo> purchasedProducts) {
        int userRequestSize = confirmedWishList.getUserRequestSize();
        if (userRequestSize == 0) return;

        Product normalProduct = confirmedWishList.getProduct();
        ProductInfo product = ProductInfo.of(normalProduct, userRequestSize);

        purchasedProducts.add(product);
    }

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

    public void setOriginalPrice(int totalAmountPaid) {
        this.originalPrice = totalAmountPaid;
    }

    public void setPromotionDiscountAmount(int promotionDiscountAmount) {
        this.promotionDiscountAmount += promotionDiscountAmount;
    }

    public void setMembershipDiscountAmount(int membershipDiscountAmount) {
        this.membershipDiscountAmount += membershipDiscountAmount;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setTotalPrice(int finalAmount) {
        this.totalPrice += finalAmount;
    }

    public int getPromotionDiscountAmount() {
        return promotionDiscountAmount;
    }

    public int getMembershipDiscountAmount() {
        return membershipDiscountAmount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void takeSummary(final UserAnswer isMemberShip) {
        int nondiscountedPrice = calculateNonDiscountedPrice();
        int totalGiftPrice = calculateTotalGiftPrice();
        setOriginalPrice(nondiscountedPrice);
        setMembershipDiscountIfAnswered(isMemberShip);
        setPromotionDiscountAmount(totalGiftPrice);
        setTotalPrice(nondiscountedPrice - promotionDiscountAmount - membershipDiscountAmount);
    }

    private void setMembershipDiscountIfAnswered(final UserAnswer isMemberShip) {
        if (isMemberShip == UserAnswer.YES) {
            int membershipDiscountPrice = calculateMembershipDiscount();
            setMembershipDiscountAmount(membershipDiscountPrice);
        }
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

    private int calculateMembershipDiscount() {
        return (int) (purchasedProducts.stream()
                .filter(productPair -> productPair.inNotPromoted() || productPair.isExpired())
                .mapToInt(ProductInfo::calculateTotalPrice)
                .sum() * StoreConst.MEMBERSHIP_DISCOUNT_RATE);
    }


    public int getTotalQuantity() {
        return this.purchasedProducts.stream().mapToInt(ProductInfo::getSize).sum();
    }

    public String printSummary() {
        return String.format(InvoicePrintConst.NO_DISCOUNTED_PRICE, InvoicePrintConst.NO_DISCOUNTED_PRICE_NAME, getTotalQuantity(), getOriginalPrice()) +
                String.format(InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE, InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE_NAME, getPromotionDiscountAmount()) +
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