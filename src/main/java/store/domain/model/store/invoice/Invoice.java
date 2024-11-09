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

    private final List<ProductPair> purchasedProducts;
    private final List<ProductPair> giftProducts;

    private int notDiscountedPrice = 0;
    private int promotionDiscount = 0;
    private int membershipDiscount = 0;
    private int totalPrice = 0;

    public Invoice(List<ProductPair> purchasedProducts, List<ProductPair> giftProducts) {
        this.purchasedProducts = purchasedProducts;
        this.giftProducts = giftProducts;
    }

    public static Invoice issue(List<ConfirmedProduct> confirmedProducts) {
        List<ProductPair> purchasedProducts = new ArrayList<>();
        List<ProductPair> giftProducts = new ArrayList<>();

        for (ConfirmedProduct confirmedWishList : confirmedProducts) {
            addGiftProduct(confirmedWishList, giftProducts);
            addNormalProduct(confirmedWishList, purchasedProducts);
        }

        return new Invoice(purchasedProducts, giftProducts);
    }

    private static void addNormalProduct(ConfirmedProduct confirmedWishList, List<ProductPair> purchasedProducts) {
        int userRequestSize = confirmedWishList.getUserRequestSize();
        if (userRequestSize == 0) return;

        Product normalProduct = confirmedWishList.getProduct();
        ProductPair product = ProductPair.of(normalProduct, userRequestSize);

        purchasedProducts.add(product);
    }

    private static void addGiftProduct(ConfirmedProduct confirmedWishList, List<ProductPair> giftProducts) {
        if (!confirmedWishList.isAvailablePromotion()) return;

        int userGiftQuantity = getGiftProductQuantity(confirmedWishList);
        if (userGiftQuantity == 0) return;

        Product promotionProduct = confirmedWishList.getPromotionProduct();
        ProductPair product = ProductPair.of(promotionProduct, userGiftQuantity);

        giftProducts.add(product);
    }

    private static int getGiftProductQuantity(ConfirmedProduct confirmedWishList) {
        int promotionStock = confirmedWishList.getPromotionStock();
        int userRequestSize = confirmedWishList.getUserRequestSize();
        int promotionDefaultQuantity = confirmedWishList.getPromotionDefaultQuantity();

        int userEligibleGiftProductQuantity = userRequestSize / promotionDefaultQuantity;
        int giftLimit = promotionStock / promotionDefaultQuantity;
        int userGiftQuantity = Math.min(userEligibleGiftProductQuantity, giftLimit);
        return userGiftQuantity;
    }

    public void setNotDiscountedPrice(int totalAmountPaid) {
        this.notDiscountedPrice = totalAmountPaid;
    }

    public void setPromotionDiscount(int promotionDiscount) {
        this.promotionDiscount += promotionDiscount;
    }

    public void setMembershipDiscount(int membershipDiscount) {
        this.membershipDiscount += membershipDiscount;
    }

    public int getNonDiscountedPrice() {
        return notDiscountedPrice;
    }

    public void setTotalPrice(int finalAmount) {
        this.totalPrice += finalAmount;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void takeSummary(UserAnswer isMemberShip) {
        int nondiscountedPrice = calculateNonDiscountedPrice();
        int totalGiftPrice = calculateTotalGiftPrice();
        setNotDiscountedPrice(nondiscountedPrice);
        setMembershipDiscountIfAnswered(isMemberShip);
        setPromotionDiscount(totalGiftPrice);
        setTotalPrice(nondiscountedPrice - promotionDiscount - membershipDiscount);
    }

    private void setMembershipDiscountIfAnswered(UserAnswer isMemberShip) {
        if (isMemberShip == UserAnswer.YES) {
            int membershipDiscountPrice = calculateMembershipDiscount();
            setMembershipDiscount(membershipDiscountPrice);
        }
    }

    private int calculateNonDiscountedPrice() {
        return purchasedProducts.stream()
                .mapToInt(ProductPair::calculateTotalPrice)
                .sum();
    }

    private int calculateTotalGiftPrice() {
        return giftProducts.stream()
                .mapToInt(productPair -> productPair.getPrice() * productPair.getSize())
                .sum();
    }

    private int calculateMembershipDiscount() {
        return (int) (purchasedProducts.stream()
                .filter(productPair -> productPair.inNotPromoted() || productPair.expired())
                .mapToInt(ProductPair::calculateTotalPrice)
                .sum() * StoreConst.MEMBERSHIP_DISCOUNT_RATE);
    }


    public int getTotalQuantity() {
        return this.purchasedProducts.stream().mapToInt(ProductPair::getSize).sum();
    }

    public String printSummary() {
        return String.format(InvoicePrintConst.NO_DISCOUNTED_PRICE, InvoicePrintConst.NO_DISCOUNTED_PRICE_NAME, getTotalQuantity(), getNonDiscountedPrice()) +
                String.format(InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE, InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE_NAME, getPromotionDiscount()) +
                String.format(InvoicePrintConst.MEMBERSHIP_DISCOUNTED_PRICE, InvoicePrintConst.MEMBERSHIP_DISCOUNTED_PRICE_NAME, getMembershipDiscount()) +
                String.format(InvoicePrintConst.TOTAL_PRICE, InvoicePrintConst.TOTAL_PRICE_NAME, getTotalPrice());
    }

    public String printPurchasedProduct() {
        return this.purchasedProducts.stream()
                .map(ProductPair::printPurchased)
                .collect(Collectors.joining());
    }

    public String printGifts() {
        return this.giftProducts.stream()
                .map(ProductPair::printGift)
                .collect(Collectors.joining());
    }
}