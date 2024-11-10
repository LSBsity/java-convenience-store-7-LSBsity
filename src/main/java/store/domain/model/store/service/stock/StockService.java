package store.domain.model.store.service.stock;


import store.domain.model.dto.ConfirmedProduct;

public class StockService {

    public void updateStock(final ConfirmedProduct confirmedProduct) {
        confirmedProduct.updateStock(confirmedProduct);
    }
}
