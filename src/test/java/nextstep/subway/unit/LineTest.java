package nextstep.subway.unit;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

import nextstep.subway.applicaion.dto.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        Long 신분당선 = 지하철_노선_생성_요청("신분당선", "green").jsonPath().getLong("id");

        SectionRequest params = new SectionRequest(강남역, 판교역, 10);

        지하철_노선에_지하철_구간_생성_요청(신분당선, params);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
    }
}
