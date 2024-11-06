package store.domain.controller;

import store.common.constant.StoreConst;
import store.common.parser.dto.UserWish;
import store.common.parser.product.ProductParser;
import store.common.parser.promotion.PromotionParser;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.view.InputView;
import store.domain.view.OutputView;

import java.util.List;

public class StoreController {

    private final OutputView outputView;
    private final InputView inputView;
    private final PromotionParser promotionParser;
    private final ProductParser productParser;

    public StoreController(OutputView outputView, InputView inputView, PromotionParser promotionParser, ProductParser productParser) {
        this.outputView = outputView;
        this.inputView = inputView;
        this.promotionParser = promotionParser;
        this.productParser = productParser;
    }

    public void run() {
        CurrentPromotions currentPromotions = promotionParser.parsePromotions(StoreConst.PROMOTIONS_FILE_PATH);
        CurrentProducts currentProducts = productParser.parseProducts(StoreConst.PRODUCTS_FILE_PATH, currentPromotions);

        outputView.showStock(currentProducts);
        List<UserWish.Request> userWishList = inputView.getUserWishList(currentProducts);
        for (UserWish.Request request : userWishList) {
            System.out.println("request = " + request.getProductName());
            System.out.println("request = " + request.getQuantity());
        }
    }
}
