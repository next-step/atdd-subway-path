package nextstep.subway.acceptance.test.utils;

import lombok.Builder;
import nextstep.subway.utils.BaseParams;

public class Stations {
    public static 지하철역_생성_파람 연신내역;
    public static 지하철역_생성_파람 서울역;
    public static 지하철역_생성_파람 삼성역;
    public static 지하철역_생성_파람 강남역;
    public static 지하철역_생성_파람 양재역;
    public static 지하철역_생성_파람 역삼역;

    public static void 파람_초기화() {
        연신내역 = 지하철역_생성_파람.builder()
                .name("연신내")
                .build();
        서울역 = 지하철역_생성_파람.builder()
                .name("서울역")
                .build();
        삼성역 = 지하철역_생성_파람.builder()
                .name("삼성역")
                .build();
        강남역 = 지하철역_생성_파람.builder()
                .name("강남역")
                .build();
        양재역 = 지하철역_생성_파람.builder()
                .name("양재역")
                .build();
        역삼역 = 지하철역_생성_파람.builder()
                .name("역삼역")
                .build();
    }

    public static class 지하철역_생성_파람 extends BaseParams {
        private final String name;

        @Builder
        지하철역_생성_파람(String name) {
            this.name = name;
        }
    }
}
