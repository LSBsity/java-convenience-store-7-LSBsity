package store.domain.model.product;

import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CurrentProducts {

    private final Map<String, List<Product>> currentProducts;

    private CurrentProducts(Map<String, List<Product>> currentProducts) {
        this.currentProducts = currentProducts;
    }

    public static CurrentProducts create(List<Product> productList) {
        // 이름을 기준으로 Map으로 변환하고, 각 리스트는 프로모션이 있는 제품이 맨 앞에 오도록 정렬
        Map<String, List<Product>> productMap = productList.stream()
                .collect(Collectors.groupingBy(
                        Product::getName,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), productComparator())
                ));
        return new CurrentProducts(productMap);
    }

    public List<Product> findProductByName(String name) {
        if (isNotExistProduct(name)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_EXIST);
        }
        return this.currentProducts.get(name);
    }

    public Map<String, List<Product>> getCurrentProducts() {
        return Collections.unmodifiableMap(this.currentProducts);
    }

    private boolean isNotExistProduct(String name) {
        return this.currentProducts.containsKey(name) == false;
    }

    private static Function<List<Product>, List<Product>> productComparator() {
        return list -> list.stream()
                .sorted((p1, p2) -> Boolean.compare(p2.isPromotedProduct(), p1.isPromotedProduct()))
                .collect(Collectors.toList());
    }
}
