package store.domain.model.store;

import store.common.constant.InvoicePrintConst;
import store.common.constant.StoreConst;
import store.domain.model.promotion.UserAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Invoice {

    private final List<ProductPair> purchasedProducts = new ArrayList<>();
    private final List<ProductPair> giftProducts = new ArrayList<>();

    private int notDiscountedPrice = 0;
    private int promotionDiscount = 0;
    private int membershipDiscount = 0;
    private int totalPrice = 0;


    public List<ProductPair> getPurchasedProducts() {
        return purchasedProducts;
    }

    public List<ProductPair> getGiftProducts() {
        return giftProducts;
    }

    public void addPurchasedProduct(ProductPair productAndSize) {
        this.purchasedProducts.add(productAndSize);
    }


    public void addGiftProduct(ProductPair productAndSize) {
        this.giftProducts.add(productAndSize);
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

    public int getNotDiscountedPrice() {
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
        int notDiscountedPrice = purchasedProducts.stream()
                .mapToInt(ProductPair::calculateTotalPrice)
                .sum();
        int totalGiftPrice = giftProducts.stream()
                .mapToInt(productPair -> productPair.getPrice() * productPair.getSize())
                .sum();
        setNotDiscountedPrice(notDiscountedPrice);
        if (isMemberShip == UserAnswer.YES) {
            int membershipDiscountPrice = (int) (purchasedProducts.stream()
                    .filter(ProductPair::inNotPromoted)
                    .mapToInt(ProductPair::calculateTotalPrice)
                    .sum() * StoreConst.MEMBERSHIP_DISCOUNT_RATE);
            setMembershipDiscount(membershipDiscountPrice);
        }
        setPromotionDiscount(totalGiftPrice);
        setTotalPrice(notDiscountedPrice - promotionDiscount - membershipDiscount);
    }

    public int getTotalQuantity() {
        return this.purchasedProducts.stream().mapToInt(ProductPair::getSize).sum();
    }

    public String printSummary() {
        return String.format(InvoicePrintConst.NO_DISCOUNTED_PRICE, InvoicePrintConst.NO_DISCOUNTED_PRICE_NAME, getTotalQuantity(), getNotDiscountedPrice()) +
                String.format(InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE, InvoicePrintConst.PROMOTION_DISCOUNTED_PRICE_NAME, getPromotionDiscount()) +
                String.format(InvoicePrintConst.MEMBERSHIP_DISCOUNTED_PRICE, InvoicePrintConst.MEMBERSHIP_DISCOUNTED_PRICE_NAME, getMembershipDiscount()) +
                String.format(InvoicePrintConst.TOTAL_PRICE, InvoicePrintConst.TOTAL_PRICE_NAME, getTotalPrice());
    }

    public String printPurchasedProduct() {
        System.out.println(InvoicePrintConst.PRINT_NAME_QUANTITY_PRICE);
        return this.purchasedProducts.stream()
                .map(ProductPair::printPurchased)
                .collect(Collectors.joining());
    }

    public String printGifts() {
        System.out.println(InvoicePrintConst.PRINT_GIFT_TITLE);
        return this.giftProducts.stream()
                .map(ProductPair::printGift)
                .collect(Collectors.joining());
    }
}
