package store.common.config;

import store.common.constant.StoreConst;
import store.common.parser.input.InputParser;
import store.common.parser.input.RegexInputParser;
import store.common.parser.product.ProductMarkDownFileParser;
import store.common.parser.product.ProductParser;
import store.common.parser.promotion.PromotionMarkDownFileParser;
import store.common.parser.promotion.PromotionParser;
import store.domain.controller.ConvenienceStore;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.model.store.service.stock.StockService;
import store.domain.model.store.service.store.StoreManager;
import store.domain.model.store.service.suggestion.SuggestionService;
import store.domain.view.InputView;
import store.domain.view.OutputView;

public class Factory {

    private static CurrentProducts currentProducts;

    public ConvenienceStore storeController() {
        return new ConvenienceStore(storeManager(), inputView(), outputView());
    }

    public InputView inputView() {
        return new InputView(inputParser());
    }

    public OutputView outputView() {
        return new OutputView();
    }

    public StoreManager storeManager() {
        return new StoreManager(currentProduct(), suggestionService(), stockService());
    }

    private CurrentProducts currentProduct() {
        if (currentProducts == null) {
            currentProducts = productParser().parseProducts(StoreConst.PRODUCTS_FILE_PATH, currentPromotions());
        }
        return currentProducts;
    }

    public SuggestionService suggestionService() {
        return new SuggestionService();
    }

    public StockService stockService() {
        return new StockService();
    }


    public CurrentPromotions currentPromotions() {
        return promotionParser().parsePromotions(StoreConst.PROMOTIONS_FILE_PATH);
    }

    public ProductParser productParser() {
        return new ProductMarkDownFileParser();
    }

    public PromotionParser promotionParser() {
        return new PromotionMarkDownFileParser();
    }

    public InputParser inputParser() {
        return getRegexInputParser();
    }

    public RegexInputParser getRegexInputParser() {
        return new RegexInputParser();
    }
}
