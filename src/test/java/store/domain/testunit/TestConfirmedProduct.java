package store.domain.testunit;

import store.domain.model.dto.ConfirmedProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestConfirmedProduct {

    List<ConfirmedProduct> confirmedProducts = new ArrayList<>();

    private TestConfirmedProduct(ConfirmedProduct... confirmedProducts) {
        Collections.addAll(this.confirmedProducts, confirmedProducts);
    }

    public static TestConfirmedProduct createTestConfirmedProduct(ConfirmedProduct... confirmedProducts) {
        return new TestConfirmedProduct(confirmedProducts);
    }

    public List<ConfirmedProduct> getTestConfirmedProducts() {
        return confirmedProducts;
    }
}
