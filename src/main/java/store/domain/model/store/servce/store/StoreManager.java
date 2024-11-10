package store.domain.model.store.servce.store;

import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.UserWish;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.invoice.Invoice;
import store.domain.model.store.servce.suggestion.SuggestionService;
import store.domain.model.store.servce.stock.StockService;

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

    public Invoice issueInvoice(final List<ConfirmedProduct> confirmedProducts,final  UserAnswer isMemberShip) {
        Invoice invoice = Invoice.issue(confirmedProducts);

        invoice.takeSummary(isMemberShip);
        return invoice;
    }

    public void updateStock(final List<ConfirmedProduct> confirmedProduct) {
        confirmedProduct.forEach(stockService::updateStock);
    }

    public void suggestHandle(final List<ConfirmedProduct> confirmedProducts) {
        confirmedProducts.forEach(suggestionService::adjustUserRequestQuantity);
    }

    public boolean hasAvailableStock() {
        return this.currentProducts.hasAvailableStock();
    }

    public List<StoreSuggestion> suggest(final List<UserWish> userWishList) {
        List<StoreSuggestion> suggestions = userWishList.stream()
                .map(this::createSuggestion)
                .collect(Collectors.toList());

        suggestionService.suggest(suggestions);
        return suggestions;
    }

    private StoreSuggestion createSuggestion(final UserWish request) {
        String requestProductName = request.getProductName();
        int requestQuantity = request.getQuantity();

        List<Product> findProducts = currentProducts.findProductByName(requestProductName);

        return StoreSuggestion.of(findProducts, requestQuantity);
    }
}