package store.domain.view;

import store.common.constant.StoreConst;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OutputView {
    public static void welcome() {
        System.out.println(StoreConst.WELCOME_MSG);
    }

    public void showStock(CurrentProducts currentProducts) {
        welcome();
        System.out.println(StoreConst.HAVING_PRODUCT_MSG);

        Map<String, List<Product>> productMap = currentProducts.getCurrentProducts();
        Set<String> productNames = productMap.keySet();

        System.out.println();
        productNames.forEach(productName -> {
            List<Product> products = productMap.get(productName);
            products.forEach(System.out::println);
        });
        System.out.println();
    }
}
