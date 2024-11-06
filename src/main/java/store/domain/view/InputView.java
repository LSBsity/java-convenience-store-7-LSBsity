package store.domain.view;

import camp.nextstep.edu.missionutils.Console;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.parser.input.InputParser;
import store.domain.model.product.CurrentProducts;
import store.common.parser.dto.UserWish;

import java.util.List;

public class InputView {

    private final InputParser inputparser;

    public InputView(InputParser inputparser) {
        this.inputparser = inputparser;
    }

    public List<UserWish.Request> getUserWishList(CurrentProducts currentProducts) {
        requestNameAndQuantity();
        while (true) {
            try {
                String input = Console.readLine();
                return inputparser.validateNameAndQuantity(input, currentProducts);
            } catch (BusinessException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void requestNameAndQuantity() {
        System.out.println(StoreConst.NAME_QUANTITY_REQ_MSG);
    }
}
