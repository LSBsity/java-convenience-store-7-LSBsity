package store.domain.model.store;


import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.SuggestionType;

public class StockService {

    public void updateStock(ConfirmedProduct confirmedProduct) {
        SuggestionType suggestionType = confirmedProduct.getSuggestionType();
        suggestionType.updateStock(confirmedProduct);
    }
}
