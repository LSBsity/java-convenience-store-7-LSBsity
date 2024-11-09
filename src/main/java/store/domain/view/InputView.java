package store.domain.view;

import camp.nextstep.edu.missionutils.Console;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.dto.*;
import store.common.parser.input.InputParser;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.UserAnswer;
import store.domain.model.store.SuggestionService;

import java.util.List;
import java.util.stream.Collectors;

public class InputView {

    private final InputParser inputparser;
    private final CurrentProducts currentProducts;

    public InputView(InputParser inputparser, CurrentProducts currentProducts) {
        this.inputparser = inputparser;
        this.currentProducts = currentProducts;
    }

    public List<UserWish.Request> getUserWishList() {
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

    public List<ConfirmedProduct> showSuggestions(List<StoreSuggestion> storeSuggestions) {
        return storeSuggestions.stream()
                .map(this::suggestToUserAndTakeConfirm)
                .collect(Collectors.toList());
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

    private ConfirmedProduct suggestToUserAndTakeConfirm(StoreSuggestion storeSuggestion) {
        UserAnswer userAnswer = printSuggest(storeSuggestion);
        return ConfirmedProduct.of(storeSuggestion, userAnswer);
    }

    private UserAnswer printSuggest(StoreSuggestion storeSuggestion) {
        UserAnswer userConfirm = UserAnswer.NO;
        Suggestion suggestion = storeSuggestion.getSuggestion();
        if (suggestion.isShouldPrint()) {
            System.out.printf(suggestion.getFormat(), storeSuggestion.getProductName(), storeSuggestion.getOfferSize());
            userConfirm = getUserConfirm();
        }
        return userConfirm;
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

    public UserAnswer tryAgain() {
        System.out.println(StoreConst.TRY_AGAIN);
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
