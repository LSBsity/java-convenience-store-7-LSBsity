package store.domain.model.store;

import store.common.constant.StoreConst;
import store.domain.model.promotion.UserAnswer;

import java.util.ArrayList;
import java.util.List;

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
}
