package store.domain.view;

import store.common.constant.StoreConst;
import store.domain.model.product.CurrentProducts;
import store.domain.model.product.Product;
import store.domain.model.store.Invoice;
import store.domain.model.store.ProductPair;

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

    public void showInvoice(Invoice invoice) {
        List<ProductPair> purchasedProducts = invoice.getPurchasedProducts();
        List<ProductPair> giftProducts1 = invoice.getGiftProducts();

        System.out.println("===========W 편의점=============");
        System.out.println("상품명\t\t\t수량\t\t금액");
        for (ProductPair purchasedProduct : purchasedProducts) {
            Product product = purchasedProduct.getProduct();
            System.out.printf("%-5s\t\t\t%d\t\t%,d\n", product.getName(), purchasedProduct.getSize(),
                    product.getPrice() * purchasedProduct.getSize());
        }

        System.out.println("===========증\t정=============");
        for (ProductPair purchasedProduct : giftProducts1) {
            Product product = purchasedProduct.getProduct();
            System.out.printf("%s\t\t\t%d\n", product.getName(), purchasedProduct.getSize());
        }

        System.out.println("==============================");
        System.out.printf("총구매액\t\t\t%d\t\t%,d\n", invoice.getTotalQuantity(), invoice.getNotDiscountedPrice());
        System.out.printf("행사할인\t\t\t\t\t-%-,7d\n", invoice.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t\t\t\t-%-,7d\n", invoice.getMembershipDiscount());
        System.out.printf("내실돈\t\t\t\t\t%-,7d\n", invoice.getTotalPrice());

    }
}
