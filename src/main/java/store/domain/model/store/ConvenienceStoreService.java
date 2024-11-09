package store.domain.model.store;

import store.domain.model.dto.*;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.invoice.Invoice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConvenienceStoreService {

    private final CurrentProducts currentProducts;
    private final SuggestionService suggestionService;
    private final StockService stockService;

    public ConvenienceStoreService(CurrentProducts currentProducts, SuggestionService suggestionService, StockService stockService) {
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


    public boolean isNotEmpty() {
        return this.currentProducts.getCurrentProducts().keySet().stream()
                .anyMatch(key -> hasAtLeastOneStock(key, this.currentProducts.getCurrentProducts()));
    }

    private boolean hasAtLeastOneStock(String key, Map<String, List<Product>> currentProductsMap) {
        return currentProductsMap.get(key)
                .stream()
                .anyMatch(product -> product.getCurrentQuantity() != 0);
    }

    public List<StoreSuggestion> suggest(List<UserWish.Request> userWishList) {
        List<StoreSuggestion> suggestions = userWishList.stream()
                .map(request -> createDefaultSuggestion(request, currentProducts))
                .collect(Collectors.toList());

        suggestionService.suggest(suggestions);
        return suggestions;
    }

    private StoreSuggestion createDefaultSuggestion(UserWish.Request request, CurrentProducts currentProducts) {
        String productName = request.getProductName();
        int quantity = request.getQuantity();

        List<Product> availableProducts = currentProducts.findProductByName(productName);

        return StoreSuggestion.of(availableProducts, quantity);
    }
}