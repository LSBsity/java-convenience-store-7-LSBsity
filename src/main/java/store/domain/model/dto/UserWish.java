package store.domain.model.dto;

public class UserWish {

    private final String productName;
    private final int quantity;

    private UserWish(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public static UserWish of(String productName, int quantity) {
        return new UserWish(productName, quantity);
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
