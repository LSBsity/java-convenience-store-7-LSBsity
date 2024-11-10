package store.domain.controller;

import store.common.constant.StoreConst;
import store.common.writer.ProductWriter;
import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.service.store.StoreManager;
import store.domain.model.store.invoice.Invoice;
import store.domain.view.InputView;
import store.domain.view.OutputView;

import java.util.List;

public class ConvenienceStore {

    private final StoreManager storeManager;
    private final OutputView outputView;
    private final InputView inputView;

    public ConvenienceStore(StoreManager convenienceStore, InputView inputView, OutputView outputView) {
        this.storeManager = convenienceStore;
        this.outputView = outputView;
        this.inputView = inputView;
    }

    public void run() {
        do {
            List<UserWish> userWishList = getUserWishList(storeManager.getCurrentProducts());

            List<ConfirmedProduct> userConfirmedWishList = suggestAndHandle(userWishList);

            UserAnswer isMemberShip = inputView.askMembershipSale();

            Invoice invoice = storeManager.issueInvoice(userConfirmedWishList, isMemberShip);

            storeManager.updateStock(userConfirmedWishList);

            outputView.showInvoice(invoice);
        } while (storeManager.hasAvailableStock() && inputView.tryAgain() == UserAnswer.YES);

        writeProductFile(storeManager.getCurrentProducts());
    }

    private List<UserWish> getUserWishList(final CurrentProducts currentProducts) {
        outputView.showStock(currentProducts);
        List<UserWish> userWishList = inputView.getUserWishList(currentProducts);
        return userWishList;
    }

    private List<ConfirmedProduct> suggestAndHandle(final List<UserWish> userWishList) {
        List<StoreSuggestion> storeSuggestions = storeManager.suggest(userWishList);

        List<ConfirmedProduct> confirmedWishLists = inputView.showSuggestions(storeSuggestions);
        storeManager.suggestHandle(confirmedWishLists);
        return confirmedWishLists;
    }

    private static void writeProductFile(final CurrentProducts currentProducts) {
        ProductWriter.writeProductsToFile(StoreConst.PRODUCTS_FILE_PATH, currentProducts);
    }
}
