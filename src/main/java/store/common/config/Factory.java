package store.common.config;

import store.common.parser.input.InputParser;
import store.common.parser.input.RegexInputParser;
import store.common.parser.product.ProductMarkDownFileParser;
import store.common.parser.product.ProductParser;
import store.common.parser.promotion.PromotionMarkDownFileParser;
import store.common.parser.promotion.PromotionParser;
import store.domain.controller.StoreController;
import store.domain.view.InputView;
import store.domain.view.OutputView;

public class Factory {

    public StoreController storeController() {
        return new StoreController(outputView(), inputView(), promotionParser(), productParser());
    }

    private InputView inputView() {
        return new InputView(inputParser());
    }

    private OutputView outputView() {
        return new OutputView();
    }

    private ProductParser productParser() {
        return new ProductMarkDownFileParser();
    }

    private PromotionParser promotionParser() {
        return new PromotionMarkDownFileParser();
    }

    private InputParser inputParser() {
        return getRegexInputParser();
    }

    private RegexInputParser getRegexInputParser() {
        return new RegexInputParser();
    }
}
