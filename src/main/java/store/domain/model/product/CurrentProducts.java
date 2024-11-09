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

    public static CurrentProducts create(final List<Product> productList) {
        Map<String, List<Product>> productMap = productList.stream()
                .collect(Collectors.groupingBy(
                        Product::getName,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(),productComparator())
                ));

        addDefaultProductIfOnlyPromotedExists(productMap);
        return new CurrentProducts(productMap);
    }

    /**
     * Products.md에 프로모션 물품만 있고 프로모션 미적용 물품이 없다면 이름과 가격을 카피해서 ProductMap에 추가함
     */
    private static void addDefaultProductIfOnlyPromotedExists(final Map<String, List<Product>> productMap) {
        for (String productName : productMap.keySet()) {
            List<Product> products = productMap.get(productName);
            Product first = products.getFirst();
            if (products.size() == 1 && first.isPromotedProduct()) {
                Product emptyProduct = Product.createCopyOfProduct(first);
                products.add(emptyProduct);
            }
        }
    }

    public List<Product> findProductByName(final String name) {
        if (isNotExistProduct(name)) {
            throw new BusinessException(ErrorCode.WISH_PRODUCT_NOT_EXIST_ERROR);
        }
        return this.currentProducts.get(name);
    }

    public Map<String, List<Product>> getCurrentProducts() {
        return Collections.unmodifiableMap(this.currentProducts);
    }

    public boolean isNotExistProduct(final String name) {
        return this.currentProducts
                .containsKey(name) == false;
    }

    public int getCurrentTotalStockQuantity(final String name) {
        return this.currentProducts.get(name)
                .stream()
                .mapToInt(Product::getCurrentQuantity)
                .sum();
    }

    public boolean hasAvailableStock() {
        return this.currentProducts.keySet().stream()
                .anyMatch(key -> hasAtLeastOneStock(key, getCurrentProducts()));
    }

    private boolean hasAtLeastOneStock(final String key, final Map<String, List<Product>> currentProductsMap) {
        return currentProductsMap.get(key)
                .stream()
                .anyMatch(product -> product.getCurrentQuantity() != 0);
    }

    private static Function<List<Product>, List<Product>> productComparator() {
        return list -> list.stream()
                .sorted((p1, p2) -> Boolean.compare(p2.isPromotedProduct(), p1.isPromotedProduct()))
                .collect(Collectors.toList());
    }
}
