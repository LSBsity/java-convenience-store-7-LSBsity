package store.domain.controller;

import store.domain.model.dto.ConfirmedWishList;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.ConvenienceStore;
import store.domain.model.store.Invoice;
import store.domain.view.InputView;
import store.domain.view.OutputView;

import java.util.List;

public class StoreController {

    private final ConvenienceStore convenienceStore;
    private final OutputView outputView;
    private final InputView inputView;

    public StoreController(ConvenienceStore convenienceStore, InputView inputView, OutputView outputView) {
        this.convenienceStore = convenienceStore;
        this.outputView = outputView;
        this.inputView = inputView;
    }

    public void run() {
        CurrentProducts currentProducts = convenienceStore.getCurrentProducts();
        outputView.showStock(currentProducts);
        List<UserWish.Request> userWishList = inputView.getUserWishList(currentProducts);

        List<StoreSuggestion> storeSuggestions = convenienceStore.suggest(userWishList);
        List<ConfirmedWishList> confirmedWishLists = inputView.showSuggestions(storeSuggestions);

        UserAnswer isMemberShip = inputView.askMembershipSale();

        Invoice invoice = convenienceStore.check(confirmedWishLists, isMemberShip);
        outputView.showInvoice(invoice);

    }
}
