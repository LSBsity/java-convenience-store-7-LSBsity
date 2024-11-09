package store.domain.view;

import camp.nextstep.edu.missionutils.Console;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.dto.*;
import store.common.parser.input.InputParser;
import store.domain.model.product.CurrentProducts;
import store.domain.model.promotion.UserAnswer;

import java.util.List;
import java.util.stream.Collectors;

public class InputView {

    private final InputParser inputParser;

    public InputView(InputParser inputParser) {
        this.inputParser = inputParser;
    }

    private <T> T readInputAndHandleErrors(final InputSupplier<T> supplier) {
        while (true) {
            try {
                return supplier.get();
            } catch (BusinessException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public List<UserWish.Request> getUserWishList(final CurrentProducts currentProducts) {
        System.out.println(StoreConst.NAME_QUANTITY_REQ_MSG);

        return readInputAndHandleErrors(() -> {
            String userInput = Console.readLine();
            return inputParser.validateNameAndQuantity(userInput, currentProducts);
        });
    }

    public UserAnswer askMembershipSale() {
        System.out.println(StoreConst.ASK_MEMBERSHIP_SALE);

        return readInputAndHandleErrors(this::getValidatedUserAnswer);
    }

    public UserAnswer tryAgain() {
        System.out.println(StoreConst.TRY_AGAIN);

        return readInputAndHandleErrors(this::getValidatedUserAnswer);
    }

    private UserAnswer getValidatedUserAnswer() {
        String userInput = Console.readLine();
        return validateUserAnswerInput(userInput);
    }

    private static UserAnswer validateUserAnswerInput(final String input) {
        if (input.equals(StoreConst.YES)) {
            return UserAnswer.YES;
        }
        if (input.equals(StoreConst.NO)) {
            return UserAnswer.NO;
        }

        throw new BusinessException(ErrorCode.USER_CONFIRM_INPUT_ERROR);
    }

    public List<ConfirmedProduct> showSuggestions(final List<StoreSuggestion> storeSuggestions) {
        return storeSuggestions.stream()
                .map(this::suggestToUserAndTakeConfirm)
                .collect(Collectors.toList());
    }

    private ConfirmedProduct suggestToUserAndTakeConfirm(final StoreSuggestion storeSuggestion) {
        UserAnswer userAnswer = printSuggestionAndGetConfirmation(storeSuggestion);

        return ConfirmedProduct.of(storeSuggestion, userAnswer);
    }

    private UserAnswer printSuggestionAndGetConfirmation(final StoreSuggestion storeSuggestion) {
        SuggestionType suggestion = storeSuggestion.getSuggestionType();
        if (!suggestion.isShouldPrint()) return UserAnswer.NO;

        System.out.printf(suggestion.getFormat(), storeSuggestion.getProductName(), storeSuggestion.getOfferSize());

        return readInputAndHandleErrors(this::getValidatedUserAnswer);
    }
}
