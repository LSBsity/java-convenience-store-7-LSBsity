package store.domain.model.store.servce.stock;


import store.domain.model.dto.ConfirmedProduct;
import store.domain.model.dto.SuggestionType;

public class StockService {

    public void updateStock(final ConfirmedProduct confirmedProduct) {
        confirmedProduct.updateStock(confirmedProduct);
    }
}
