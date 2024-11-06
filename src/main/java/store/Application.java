package store;

import store.common.config.Factory;
import store.domain.controller.StoreController;

public class Application {
    public static void main(String[] args) {
        Factory factory = new Factory();

        StoreController storeController = factory.storeController();
        storeController.run();
    }
}
