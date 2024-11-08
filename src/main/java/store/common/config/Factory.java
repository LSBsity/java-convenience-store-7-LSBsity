package store.common.config;

import store.common.constant.StoreConst;
import store.common.parser.input.InputParser;
import store.common.parser.input.RegexInputParser;
import store.common.parser.product.ProductMarkDownFileParser;
import store.common.parser.product.ProductParser;
import store.common.parser.promotion.PromotionMarkDownFileParser;
import store.common.parser.promotion.PromotionParser;
import store.domain.controller.StoreController;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.model.store.ConvenienceStore;
import store.domain.model.store.InvoiceService;
import store.domain.model.store.PromotionService;
import store.domain.view.InputView;
import store.domain.view.OutputView;

public class Factory {

    public StoreController storeController() {
        return new StoreController(convenienceStore(), inputView(), outputView());
    }

    public InputView inputView() {
        return new InputView(inputParser());
    }

    public OutputView outputView() {
        return new OutputView();
    }

    public ConvenienceStore convenienceStore() {
        return new ConvenienceStore(currentProducts());
    }

    public PromotionService promotionService() {
        return new PromotionService();
    }

    public InvoiceService invoiceService() {
        return new InvoiceService();
    }

    public CurrentProducts currentProducts() {
        return productParser().parseProducts(StoreConst.PRODUCTS_FILE_PATH, currentPromotions());
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
