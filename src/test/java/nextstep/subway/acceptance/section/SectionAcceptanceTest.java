package nextstep.subway.acceptance.section;

import static nextstep.subway.acceptance.station.StationAcceptanceTestHelper.지하철_파라미터_생성;
import static nextstep.subway.acceptance.station.StationAcceptanceTestHelper.지하철역_생성_요청;

import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionAcceptanceTest extends AcceptanceTest {
    final String 상행역 = "강남";
    final String 하행역 = "역삼역";
    @BeforeEach
    public void setUp() {
        지하철역_생성_요청(지하철_파라미터_생성(상행역));
        지하철역_생성_요청(지하철_파라미터_생성(하행역));
    }

    @Test
    @DisplayName("구간 추가시 노선 처음에 추가할 수 있다.")
    void AddFrontSectionAcceptanceTest() {
        // given

        // when

        // then
    }
    @Test
    @DisplayName("구간 추가시 노선 중간에 추가할 수 있다.")
    void AddMidSectionAcceptanceTest() {
        // given

        // when

        // then
    }
    @Test
    @DisplayName("구간 추가시 이미 등록된 역은 추가할 수 없다.")
    void AlreadyRegisterSectionAcceptanceTest() {
        // given

        // when

        // then
    }
}
