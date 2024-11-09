package store.domain.view;

import store.common.constant.InvoicePrintConst;
import store.common.constant.StoreConst;
import store.domain.model.product.CurrentProducts;
import store.domain.model.store.invoice.Invoice;

public class OutputView {

    public void showStock(CurrentProducts currentProducts) {
        welcome();
        printHavingProductMessage();
        printProductStock(currentProducts);
    }

    public void showInvoice(Invoice invoice) {
        printStoreName();
        printPurchasedProduct(invoice);
        printGiftProduct(invoice);
        printLineSeparator();
        printSummary(invoice);
    }

    public static void welcome() {
        System.out.println(StoreConst.WELCOME_MSG);
    }

    private void printHavingProductMessage() {
        System.out.println(StoreConst.HAVING_PRODUCT_MSG);
        System.out.println();
    }

    private void printProductStock(CurrentProducts currentProducts) {
        currentProducts.getCurrentProducts()
                .forEach((productName, products) -> products.forEach(System.out::println));
        System.out.println();
    }

    private static void printSummary(Invoice invoice) {
        System.out.println(invoice.printSummary());
    }

    private static void printStoreName() {
        System.out.println(InvoicePrintConst.PRINT_STORE_NAME_TITLE);
    }

    private static void printLineSeparator() {
        System.out.println(InvoicePrintConst.PRINT_SEPARATOR);
    }

    private static void printGiftProduct(Invoice invoice) {
        System.out.println(InvoicePrintConst.PRINT_GIFT_TITLE);
        System.out.print(invoice.printGifts());
    }

    private static void printPurchasedProduct(Invoice invoice) {
        System.out.println(InvoicePrintConst.PRINT_NAME_QUANTITY_PRICE);
        System.out.print(invoice.printPurchasedProduct());
    }
}
