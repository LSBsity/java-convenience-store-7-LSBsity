package store.domain.model.promotion;

import camp.nextstep.edu.missionutils.DateTimes;
import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Promotion {

    private final String promotionName;         // name
    private final PromotionType promotionType;  // buy, get
    private final LocalDate startDate;          // start
    private final LocalDate endDate;            // end

    private Promotion(String promotionName, PromotionType promotionType, LocalDateTime startDate,
                      LocalDateTime endDate) {
        validate(promotionName, promotionType, startDate, endDate);
        this.promotionName = promotionName;
        this.promotionType = promotionType;
        this.startDate = LocalDate.from(startDate);
        this.endDate = LocalDate.from(endDate);
    }

    public static Promotion of(String promotionName, PromotionType promotionType, LocalDate startDate,
                               LocalDate endDate) {
        return new Promotion(promotionName, promotionType, startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    private static void validate(final String promotionName, final PromotionType promotionType,
                                 final LocalDateTime startDate, final LocalDateTime endDate) {
        if (promotionName == null || promotionType == null || startDate == null || endDate == null) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    public static Promotion createNone() {
        return new Promotion(StoreConst.NULL, PromotionType.NONE, DateTimes.now(), DateTimes.now());
    }

    public boolean isValidPromotion() {
        return !this.promotionType.equals(PromotionType.NONE);
    }

    public boolean isAvailable(final LocalDateTime currentDate) {
        return !currentDate.isBefore(startDate.atStartOfDay()) &&
                !currentDate.isAfter(endDate.atStartOfDay()) && promotionType != PromotionType.NONE;
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
