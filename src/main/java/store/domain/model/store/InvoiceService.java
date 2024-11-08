package store.domain.model.store;

import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;

import java.util.List;

public class InvoiceService {

    public Invoice createInvoice(List<ConfirmedProduct> confirmedProducts, UserAnswer isMemberShip) {
        Invoice invoice = new Invoice();

        for (ConfirmedProduct confirmedWishList : confirmedProducts) {
            ProductPair purchasedProduct = new ProductPair(confirmedWishList.getProduct(), confirmedWishList.getUserRequestSize());
            invoice.addPurchasedProduct(purchasedProduct);

            if (confirmedWishList.isAvailablePromotion()) {
                Product product = confirmedWishList.getProduct();
                int userGiftQuantity = getGiftProductQuantity(confirmedWishList);
                ProductPair giftProduct = new ProductPair(product, userGiftQuantity);
                invoice.addGiftProduct(giftProduct);
            }
        }

        invoice.takeSummary(isMemberShip);
        return invoice;
    }

    private int getGiftProductQuantity(ConfirmedProduct confirmedWishList) {
        int promotionStock = confirmedWishList.getPromotionStock();
        int userRequestSize = confirmedWishList.getUserRequestSize();
        int promotionDefaultQuantity = confirmedWishList.getPromotionDefaultQuantity();

        int userEligibleGiftProductQuantity = userRequestSize / promotionDefaultQuantity;
        int giftLimit = promotionStock / promotionDefaultQuantity;
        return Math.min(userEligibleGiftProductQuantity, giftLimit);
    }
}