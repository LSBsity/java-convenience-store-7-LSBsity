package store.domain.model.store;

import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.Suggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;

import java.util.List;

public class ConvenienceStore {

    private final CurrentProducts currentProducts;

    public ConvenienceStore(CurrentProducts currentProducts) {
        this.currentProducts = currentProducts;
    }

    public CurrentProducts getCurrentProducts() {
        return this.currentProducts;
    }

    public List<StoreSuggestion> suggest(List<UserWish.Request> userWishList) {
        return userWishList.stream()
                .map(this::createDefaultSuggestion)
                .peek(this::applyPromotions)
                .toList();
    }

    private StoreSuggestion createDefaultSuggestion(UserWish.Request request) {
        List<Product> availableProducts = currentProducts.findProductByName(request.getProductName());
        return new StoreSuggestion(availableProducts, request.getQuantity());
    }

    private void applyPromotions(StoreSuggestion suggestion) {
        if (isEligibleForAdditionalPurchase(suggestion)) {
            applyAdditionalPurchaseOffer(suggestion);
        }
        if (isInsufficientPromotionStock(suggestion)) {
            applyInsufficientPromotionStockOffer(suggestion);
        }
    }

    private static boolean isEligibleForAdditionalPurchase(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) {
            return false;
        }
        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity();
        int requestSize = suggestion.getUserRequestSize();
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();
        int remainder = requestSize % promotionDefaultSize;

        if (remainder == 0 && promotionAvailableStockQuantity >= requestSize) {
            suggestion.changeSuggestion(Suggestion.ALREADY_ELIGIBLE);
            return false;
        }
        return isEligibleButNotCorrectQuantity(remainder, promotionDefaultSize, promotionAvailableStockQuantity, requestSize);
    }

    private static boolean isEligibleButNotCorrectQuantity(int remainder, int promotionDefaultSize, int stock, int requestSize) {
        return remainder == promotionDefaultSize - 1 && stock >= requestSize + 1;
    }

    private static void applyAdditionalPurchaseOffer(StoreSuggestion suggestion) {
        suggestion.changeSuggestion(Suggestion.ADDITIONAL_FREE_PRODUCT);
        suggestion.changeOfferSize(1);
    }

    private static boolean isInsufficientPromotionStock(StoreSuggestion suggestion) {
        if (isNotApplicable(suggestion)) {
            return false;
        }

        int promotionAvailableStockQuantity = suggestion.getPromotionAvailableStockQuantity(); // 행사 재고
        int requestSize = suggestion.getUserRequestSize();                                     // 유저 요청 재고
        int promotionDefaultSize = suggestion.getPromotionDefaultQuantity();                   // 행사 디폴트 사이즈

        int remainder = requestSize % promotionDefaultSize;
        if (remainder == 0 && promotionAvailableStockQuantity >= requestSize) {
            suggestion.changeSuggestion(Suggestion.ALREADY_ELIGIBLE);
            return false;
        }

        if (promotionAvailableStockQuantity <= requestSize) { // 수량이 맞아떨어지지 않는데 재고도 없으면
            int forAskUserToBuyQuantity = calculateQuantityForAskUserNotAppliedPromotion(
                    promotionAvailableStockQuantity, promotionDefaultSize, requestSize);
            suggestion.changeOfferSize(forAskUserToBuyQuantity);
            return true;
        }

        return false;
    }

    private static int calculateQuantityForAskUserNotAppliedPromotion(int promotionAvailableStockQuantity, int promotionDefaultSize, int requestSize) {
        int noAvailablePromotionRemainder = promotionAvailableStockQuantity % promotionDefaultSize;
        int availablePromotionQuantity = promotionAvailableStockQuantity - noAvailablePromotionRemainder;
        int forAskUserToBuyQuantity = requestSize - availablePromotionQuantity;
        return forAskUserToBuyQuantity;
    }

    private static void applyInsufficientPromotionStockOffer(StoreSuggestion suggestion) {
        suggestion.changeSuggestion(Suggestion.INSUFFICIENT_PROMOTION_STOCK);
    }

    private static boolean isNotApplicable(StoreSuggestion suggestion) {
        return suggestion.isAlreadySuggested() || !suggestion.isPromoted();
    }

    public Invoice check(List<ConfirmedProduct> confirmedProducts, UserAnswer isMemberShip) {
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

    private static int getGiftProductQuantity(ConfirmedProduct confirmedWishList) {
        int promotionStock = confirmedWishList.getPromotionStock();
        int userRequestSize = confirmedWishList.getUserRequestSize();
        int promotionDefaultQuantity = confirmedWishList.getPromotionDefaultQuantity();

        int userEligibleGiftProductQuantity = userRequestSize / promotionDefaultQuantity;
        int giftLimit = promotionStock / promotionDefaultQuantity;
        int userGiftQuantity = Math.min(userEligibleGiftProductQuantity, giftLimit);
        return userGiftQuantity;
    }
}