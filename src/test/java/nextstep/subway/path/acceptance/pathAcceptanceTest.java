package nextstep.subway.path.acceptance;

import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.*;
import static nextstep.subway.util.LineStep.지하철_노선_생성;
import static nextstep.subway.util.PathStep.경로_조회;
import static nextstep.subway.util.PathStep.경로_조회_실패;
import static nextstep.subway.util.SectionStep.지하철_구간_등록;
import static nextstep.subway.util.StationStep.지하철_역_등록;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class pathAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 남부터미널역;
    private StationResponse 양재역;
    private StationResponse 용산역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setup() {

        databaseCleanup.execute();

        교대역 = 지하철_역_등록("교대역");
        강남역 = 지하철_역_등록("강남역");
        남부터미널역 = 지하철_역_등록("남부터미널역");
        양재역 = 지하철_역_등록("양재");
        용산역 = 지하철_역_등록("용산");

        이호선 = 지하철_노선_생성("2호선", "green", 교대역.getId(), 강남역.getId(), 10L).getId();
        신분당선 = 지하철_노선_생성("신분당선", "red", 강남역.getId(), 양재역.getId(), 10L).getId();
        삼호선 = 지하철_노선_생성("3호선", "orange", 교대역.getId(), 남부터미널역.getId(), 2L).getId();

        지하철_구간_등록(삼호선, SectionRequest.of(남부터미널역.getId(), 양재역.getId(), 3L));

    }

    /**
     * User Story : 관리자로서, 역의 경로 조회를 할 수 있다. *
     */

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When : 관리자가 출발역의 ID와 도착역의 ID를 통해 paths를 요청하면,
       Then : 출발역부터 도착역까지의 경로에 있는 역의 목록과 조회한 경로의 구간 거리를 반환한다. */
    @DisplayName("경로 조회를 한다.")
    @Test
    public void paths_find_success() {
        // when
        var 경로_조회_결과 = 경로_조회(교대역.getId(), 양재역.getId());

        // then
        assertEquals(경로_조회_결과.getStationResponseList(), List.of(교대역, 남부터미널역, 양재역));
        assertEquals(경로_조회_결과.getDistance(), 5L);
    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When : 관리자가 출발역의 ID와 도착역의 ID가 동일한 paths를 요청하면,
       Then : 관리자는 경로의 구간 조회에 실패한다. */
    @DisplayName("경로 조회를 실패한다. 출발역과 도착역은 동일할 수 없다.")
    @Test
    public void paths_find_fail() {
        // when
        var 경로_조회_결과 = 경로_조회_실패(교대역.getId(), 교대역.getId());

        // then
        Assertions.assertAll(
                () -> assertEquals(경로_조회_결과.getStatus(), PATH_DUPLICATE_STATION.getStatus()),
                () -> assertEquals(경로_조회_결과.getCode(), PATH_DUPLICATE_STATION.getCode()),
                () -> assertEquals(경로_조회_결과.getDescription(), PATH_DUPLICATE_STATION.getDescription())
        );

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When : 관리자가 연결되어 있지 않은 출발역의 ID와 도착역의 ID를 통해 paths를 요청하면,
       Then : 관리자는 경로의 구간 조회에 실패한다. */
    @DisplayName("경로 조회를 실패한다. 출발역과 도착역은 연결되어 있어야한다.")
    @Test
    public void paths_find_fail2() {
        // when
        var 경로_조회_결과 = 경로_조회_실패(교대역.getId(), 용산역.getId());

        // then
        Assertions.assertAll(
                () -> assertEquals(경로_조회_결과.getStatus(), PATH_NOT_FOUND.getStatus()),
                () -> assertEquals(경로_조회_결과.getCode(), PATH_NOT_FOUND.getCode()),
                () -> assertEquals(경로_조회_결과.getDescription(), PATH_NOT_FOUND.getDescription())
        );

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When : 관리자가 존재하지 않은 출발역의 ID와 도착역의 ID를 통해 paths를 요청하면,
       Then : 관리자는 경로의 구간 조회에 실패한다. */
    @DisplayName("경로 조회를 실패한다. 존재하는 출발역과 도착역만 경로 조회가 가능하다.")
    @Test
    public void paths_find_fail3() {
        // given
        var 존재하지_않는_역_1 = 10L;
        var 존재하지_않는_역_2 = 11L;

        // when
        var 경로_조회_결과 = 경로_조회_실패(존재하지_않는_역_1, 존재하지_않는_역_2);

        // then
        Assertions.assertAll(
                () -> assertEquals(경로_조회_결과.getStatus(), STATION_NOT_FOUND.getStatus()),
                () -> assertEquals(경로_조회_결과.getCode(), STATION_NOT_FOUND.getCode()),
                () -> assertEquals(경로_조회_결과.getDescription(), STATION_NOT_FOUND.getDescription())
        );
    }
}
