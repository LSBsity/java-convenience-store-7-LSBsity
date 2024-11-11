package store.common.parser.promotion;

import store.common.constant.StoreConst;
import store.common.exception.BusinessException;
import store.common.exception.ErrorCode;
import store.domain.model.promotion.CurrentPromotions;
import store.domain.model.promotion.Promotion;
import store.domain.model.promotion.PromotionType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionMarkDownFileParser implements PromotionParser {

    @Override
    public CurrentPromotions parsePromotions(final String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            List<Promotion> promotions = convertFileToPromotions(reader);
            return CurrentPromotions.create(promotions);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static List<Promotion> convertFileToPromotions(final BufferedReader reader) throws IOException {
        reader.readLine(); // 파일의 헤더는 무시
        return reader.lines()
                .map(PromotionMarkDownFileParser::parseLine)
                .collect(Collectors.toList());
    }

    private static Promotion parseLine(final String line) {
        String[] fields = line.split(StoreConst.FILE_PARSE_DELIMETER);
        validateHeaders(fields);
        try {
            return toPromotion(fields);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static void validateHeaders(final String[] fields) {
        if (fields.length != StoreConst.PROMOTION_COLUMN_SIZE) {
            throw new BusinessException(ErrorCode.FILE_PARSE_OR_PATH_ERROR);
        }
    }

    private static Promotion toPromotion(final String[] fields) {
        String promotionName = fields[0];
        int buy = Integer.parseInt(fields[1]);
        int get = Integer.parseInt(fields[2]);
        LocalDate startDate = LocalDate.parse(fields[3]);
        LocalDate endDate = LocalDate.parse(fields[4]);

        PromotionType promotionType = PromotionType.getMatchedPromotionType(buy, get);
        return Promotion.of(promotionName, promotionType, startDate, endDate);
    }
}