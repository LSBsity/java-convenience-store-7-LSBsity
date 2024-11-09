package store;

import store.common.config.Factory;
import store.domain.controller.ConvenienceStore;

public class Application {
    public static void main(String[] args) {
        Factory factory = new Factory();

        ConvenienceStore storeController = factory.storeController();
        storeController.run();
    }
}
