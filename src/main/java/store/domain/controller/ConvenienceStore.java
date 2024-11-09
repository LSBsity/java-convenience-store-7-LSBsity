package store.domain.controller;

import store.common.constant.StoreConst;
import store.common.writer.ProductWriter;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.ConvenienceStoreService;
import store.domain.model.store.invoice.Invoice;
import store.domain.view.InputView;
import store.domain.view.OutputView;

import java.util.List;

public class ConvenienceStore {

    private final ConvenienceStoreService convenienceStoreService;
    private final OutputView outputView;
    private final InputView inputView;

    public ConvenienceStore(ConvenienceStoreService convenienceStore, InputView inputView, OutputView outputView) {
        this.convenienceStoreService = convenienceStore;
        this.outputView = outputView;
        this.inputView = inputView;
    }

    public void run() {
        do {
            List<UserWish.Request> userWishList = getUserWishList();

            List<ConfirmedProduct> confirmedWishLists = suggestAndHandle(userWishList);

            UserAnswer isMemberShip = inputView.askMembershipSale();
            Invoice invoice = convenienceStoreService.issueInvoice(confirmedWishLists, isMemberShip);

            convenienceStoreService.updateStock(confirmedWishLists);

            outputView.showInvoice(invoice);
        } while (convenienceStoreService.isNotEmpty() && inputView.tryAgain() == UserAnswer.YES);

        writeProductFile(convenienceStoreService.getCurrentProducts());
    }

    private List<UserWish.Request> getUserWishList() {
        outputView.showStock();
        List<UserWish.Request> userWishList = inputView.getUserWishList();
        return userWishList;
    }

    private List<ConfirmedProduct> suggestAndHandle(List<UserWish.Request> userWishList) {
        List<StoreSuggestion> storeSuggestions = convenienceStoreService.suggest(userWishList);
        List<ConfirmedProduct> confirmedWishLists = inputView.showSuggestions(storeSuggestions);
        convenienceStoreService.suggestHandle(confirmedWishLists);
        return confirmedWishLists;
    }

    private static void writeProductFile(CurrentProducts currentProducts) {
        ProductWriter.writeProductsToFile(currentProducts, StoreConst.PRODUCTS_FILE_PATH);
    }

}
