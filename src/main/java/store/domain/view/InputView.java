package store.domain.view;

import camp.nextstep.edu.missionutils.Console;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.dto.ConfirmedWishList;
import store.domain.model.dto.StoreSuggestion;
import store.domain.model.dto.Suggestion;
import store.common.parser.input.InputParser;
import store.domain.model.product.CurrentProducts;
import store.domain.model.dto.UserWish;
import store.domain.model.promotion.UserAnswer;

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

    public List<ConfirmedWishList> showSuggestions(List<StoreSuggestion> storeSuggestions) {
        return storeSuggestions.stream()
                .map(this::suggestToUserAndTakeConfirm)
                .toList();
    }

    private UserAnswer getUserConfirm() {
        while (true) {
            try {
                String input = Console.readLine();
                return validateUserAnswerInput(input);
            } catch (BusinessException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static UserAnswer validateUserAnswerInput(String input) {
        if (input.equals(StoreConst.YES)) {
            return UserAnswer.YES;
        }
        if (input.equals(StoreConst.NO)) {
            return UserAnswer.NO;
        }
        throw new BusinessException(ErrorCode.USER_CONFIRM_INPUT_ERROR);
    }

    private ConfirmedWishList suggestToUserAndTakeConfirm(StoreSuggestion storeSuggestion) {
        Suggestion suggestion = storeSuggestion.getSuggestion();

        String name = storeSuggestion.getProductName();
        int offerSize = storeSuggestion.getOfferSize();
        int userRequestSize = storeSuggestion.getUserRequestSize();

        UserAnswer userAnswer = printSuggest(suggestion, name, offerSize);

        changeUserRequestQuantity(storeSuggestion, userAnswer, userRequestSize, offerSize);
        return ConfirmedWishList.of(storeSuggestion.getProducts(), storeSuggestion.getUserRequestSize());
    }

    private static void changeUserRequestQuantity(StoreSuggestion storeSuggestion, UserAnswer userAnswer, int userRequestSize, int offerSize) {
        if (isAdditionalFree(storeSuggestion) && userAnswer == UserAnswer.YES) {
            storeSuggestion.addUserRequestSize();
        }
        if (storeSuggestion.getSuggestion() == Suggestion.INSUFFICIENT_PROMOTION_STOCK && userAnswer == UserAnswer.NO) {
            storeSuggestion.changeUserRequestSize(userRequestSize - offerSize);
        }
    }

    private static boolean isAdditionalFree(StoreSuggestion storeSuggestion) {
        return storeSuggestion.getSuggestion() == Suggestion.ADDITIONAL_FREE_PRODUCT;
    }

    private UserAnswer printSuggest(Suggestion suggestion, String name, int size) {
        UserAnswer userConfirm = UserAnswer.NO;
        if (suggestion.isShouldPrint()) {
            System.out.printf(suggestion.getFormat(), name, size);
            userConfirm = getUserConfirm();
        }
        return userConfirm;
    }

    public UserAnswer askMembershipSale() {
        System.out.println(StoreConst.ASK_MEMBERSHIP_SALE);
        while (true) {
            try {
                String input = Console.readLine();
                return validateUserAnswerInput(input);
            } catch (BusinessException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
