package store.domain.model.promotion;

import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;

import java.time.LocalDate;

public class Promotion {

    private final String promotionName;         // name
    private final PromotionType promotionType;  // buy, get
    private final LocalDate startDate;          // start
    private final LocalDate endDate;            // end

    private Promotion(String promotionName, PromotionType promotionType, LocalDate startDate, LocalDate endDate) {
        validate(promotionName, promotionType, startDate, endDate);
        this.promotionName = promotionName;
        this.promotionType = promotionType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Promotion of(String promotionName, PromotionType promotionType, LocalDate startDate, LocalDate endDate) {
        return new Promotion(promotionName, promotionType, startDate, endDate);
    }

    private static void validate(String promotionName, PromotionType promotionType, LocalDate startDate, LocalDate endDate) {
        if (promotionName == null || promotionType == null || startDate == null || endDate == null) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    public static Promotion createNone() {
        return new Promotion("null", PromotionType.NONE, LocalDate.now(), LocalDate.now());
    }

    public boolean isValidPromotion() {
        return !this.promotionType.equals(PromotionType.NONE);
    }

    public boolean isAvailable(final LocalDate currentDate) {
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate) && promotionType != PromotionType.NONE;
    }

    public int getDefaultDefaultQuantity() {
        return this.promotionType.getDefaultSize();
    }

    public String getPromotionName() {
        return promotionName;
    }

    public PromotionType getPromotionType() {
        return promotionType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
