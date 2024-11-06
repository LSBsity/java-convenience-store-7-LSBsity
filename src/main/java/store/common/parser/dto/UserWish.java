package store.common.parser.dto;

public class UserWish {

    public static class Request {
        private final String productName;
        private final int quantity;

        private Request(String productName, int quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        public static Request of(String productName, int quantity) {
            return new Request(productName, quantity);
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
