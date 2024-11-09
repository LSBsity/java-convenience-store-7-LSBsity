package store.domain.model.store;

import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.invoice.Invoice;

import java.util.List;
import java.util.stream.Collectors;

public class StoreManager {

    private final CurrentProducts currentProducts;
    private final SuggestionService suggestionService;
    private final StockService stockService;

    public StoreManager(CurrentProducts currentProducts, SuggestionService suggestionService, StockService stockService) {
        this.currentProducts = currentProducts;
        this.suggestionService = suggestionService;
        this.stockService = stockService;
    }

    public CurrentProducts getCurrentProducts() {
        return this.currentProducts;
    }

    public Invoice issueInvoice(List<ConfirmedProduct> confirmedProducts, UserAnswer isMemberShip) {
        Invoice invoice = Invoice.issue(confirmedProducts);

        invoice.takeSummary(isMemberShip);
        return invoice;
    }

    public void updateStock(List<ConfirmedProduct> confirmedProduct) {
        confirmedProduct.forEach(stockService::updateStock);
    }

    public void suggestHandle(List<ConfirmedProduct> confirmedProducts) {
        confirmedProducts.forEach(suggestionService::adjustUserRequestQuantity);
    }

    public boolean hasAvailableStock() {
        return this.currentProducts.hasAvailableStock();
    }

    public List<StoreSuggestion> suggest(List<UserWish.Request> userWishList) {
        List<StoreSuggestion> suggestions = userWishList.stream()
                .map(this::createSuggestion)
                .collect(Collectors.toList());

        suggestionService.suggest(suggestions);
        return suggestions;
    }

    private StoreSuggestion createSuggestion(UserWish.Request request) {
        String requestProductName = request.getProductName();
        int requestQuantity = request.getQuantity();

        List<Product> findProducts = currentProducts.findProductByName(requestProductName);

        return StoreSuggestion.of(findProducts, requestQuantity);
    }
}