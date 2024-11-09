package store.domain.testunit;

import store.domain.model.product.CurrentProducts;
import store.domain.view.OutputView;

public class TestOutputView {

    public static OutputView getTestOutputView(CurrentProducts currentProducts) {
        return new OutputView(currentProducts);
    }
}
