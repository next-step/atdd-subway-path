package nextstep.subway.acceptance.test.utils;

import lombok.Builder;
import nextstep.subway.utils.BaseParams;

public class Lines {
    public static 지하철_노선_생성_파람 GTXA노선_연신내_서울역;
    public static 지하철_노선_생성_파람 GTXA노선_상행_정보없음;
    public static 지하철_노선_생성_파람 GTXA노선_하행_정보없음;
    public static 지하철_노선_생성_파람 GTXA노선_거리_음수;
    public static 지하철_노선_생성_파람 신분당선;

    public static 지하철_노선_수정_파람 노선색상;

    public static 지하철_구간_생성_파람 GTXA노선_구간_서울역_삼성역;
    public static 지하철_구간_생성_파람 GTXA노선_구간_연신내역_삼성역;
    public static 지하철_구간_생성_파람 GTXA노선_구간_서울역_연신내역;

    public static 지하철_구간_삭제_파람 GTXA노선_구간_삭제_삼성역;
    public static 지하철_구간_삭제_파람 GTXA노선_구간_삭제_연신내;
    public static 지하철_구간_삭제_파람 GTXA노선_구간_삭제_서울역;

    private static final String DEFAULT_NAME = "GTX-A";
    private static final String DEFAULT_COLOR = "bg-red-900";
    private static final Long DEFAULT_UP_STATION_ID = 1L;
    private static final Long DEFAULT_DOWN_STATION_ID = 2L;
    private static final Integer DEFAULT_DISTANCE = 10;

    public static void 파람_초기화() {
        GTXA노선_연신내_서울역 = 지하철_노선_생성_파람.builder()
                .name(DEFAULT_NAME)
                .color(DEFAULT_COLOR)
                .upStationId(DEFAULT_UP_STATION_ID)
                .downStationId(DEFAULT_DOWN_STATION_ID)
                .distance(DEFAULT_DISTANCE)
                .build();

        GTXA노선_상행_정보없음 = 지하철_노선_생성_파람.builder()
                .name(DEFAULT_NAME)
                .color(DEFAULT_COLOR)
                .downStationId(DEFAULT_DOWN_STATION_ID)
                .distance(DEFAULT_DISTANCE)
                .build();

        GTXA노선_하행_정보없음 = 지하철_노선_생성_파람.builder()
                .name(DEFAULT_NAME)
                .color(DEFAULT_COLOR)
                .upStationId(DEFAULT_UP_STATION_ID)
                .distance(DEFAULT_DISTANCE)
                .build();

        GTXA노선_거리_음수 = 지하철_노선_생성_파람.builder()
                .name(DEFAULT_NAME)
                .color(DEFAULT_COLOR)
                .upStationId(DEFAULT_UP_STATION_ID)
                .downStationId(DEFAULT_DOWN_STATION_ID)
                .distance(-1)
                .build();

        신분당선 = 지하철_노선_생성_파람.builder()
                .name("신분당선")
                .color("bg-red-500")
                .upStationId(2L)
                .downStationId(3L)
                .distance(10)
                .build();

        노선색상 = 지하철_노선_수정_파람.builder()
                .color("bg-red-800")
                .build();

        GTXA노선_구간_서울역_삼성역 = 지하철_구간_생성_파람.builder()
                .upStationId(2L)
                .downStationId(3L)
                .distance(DEFAULT_DISTANCE)
                .build();

        GTXA노선_구간_연신내역_삼성역 = 지하철_구간_생성_파람.builder()
                .upStationId(1L)
                .downStationId(3L)
                .distance(DEFAULT_DISTANCE)
                .build();

        GTXA노선_구간_서울역_연신내역 = 지하철_구간_생성_파람.builder()
                .upStationId(2L)
                .downStationId(1L)
                .distance(DEFAULT_DISTANCE)
                .build();

        GTXA노선_구간_삭제_삼성역 = 지하철_구간_삭제_파람.builder()
                .stationId(3L)
                .build();

        GTXA노선_구간_삭제_연신내 = 지하철_구간_삭제_파람.builder()
                .stationId(1L)
                .build();

        GTXA노선_구간_삭제_서울역 = 지하철_구간_삭제_파람.builder()
                .stationId(2L)
                .build();
    }

    public static class 지하철_노선_생성_파람 extends BaseParams {
        private final String name;
        private final String color;
        private final Long upStationId;
        private final Long downStationId;
        private final Integer distance;

        @Builder
        지하철_노선_생성_파람(String name, String color, Long upStationId, Long downStationId, Integer distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }

    public static class 지하철_노선_수정_파람 extends BaseParams {
        private final String name;
        private final String color;
        private final Long upStationId;
        private final Long downStationId;
        private final Integer distance;

        @Builder
        지하철_노선_수정_파람(String name, String color, Long upStationId, Long downStationId, Integer distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }

    public static class 지하철_구간_생성_파람 extends BaseParams {
        private final Long upStationId;
        private final Long downStationId;
        private final Integer distance;

        @Builder
        지하철_구간_생성_파람(Long upStationId, Long downStationId, Integer distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }

    public static class 지하철_구간_삭제_파람 extends BaseParams {
        private final Long stationId;

        @Builder
        지하철_구간_삭제_파람(Long stationId) {
            this.stationId = stationId;
        }
    }
}
