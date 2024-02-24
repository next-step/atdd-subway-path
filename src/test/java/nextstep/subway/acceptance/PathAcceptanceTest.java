package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import nextstep.subway.fixture.*;
import nextstep.subway.fixture.AcceptanceTest;
import nextstep.subway.line.domain.Color;
import nextstep.subway.line.presentation.*;
import nextstep.subway.path.presentation.PathsResponse;
import nextstep.subway.path.domain.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Outside In TDD
 * 컨트롤러 구현 이후 서비스 구현 시 바로 기능 구현에 앞서 단위 테스트 먼저 작성
 * 서비스 레이어의 단위 테스트 목적은 비즈니스 플로우를 검증하는 것이며 이 때 협력 객체는 stubbing을 활용하여 대체함
 * 단위 테스트 작성 후 해당 단위 테스트를 만족하는 기능을 구현한 다음 리팩터링 진행
 * 그 다음 기능 구현은 방금 전 사이클에서 stubbing 한 객체를 대상으로 진행하면 수월하게 TDD 사이클을 진행할 수 있음
 * 외부 라이브러리를 활용한 로직을 검증할 때는 가급적 실제 객체를 활용
 * Happy 케이스에 대한 부분만 구현( Side 케이스에 대한 구현은 다음 단계에서 진행)
 */
@AcceptanceTest
@Transactional
public class PathAcceptanceTest {

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        교대역 = StationSteps.createStation("교대역").getId();
        강남역 = StationSteps.createStation("강남역").getId();
        양재역 = StationSteps.createStation("양재역").getId();
        남부터미널역 = StationSteps.createStation("남부터미널역").getId();

        이호선 = LineSteps.노선_생성(new LineRequest("이호선", Color.GREEN, 교대역, 강남역, 10)).getId();
        신분당선 = LineSteps.노선_생성(new LineRequest("신분당선", Color.RED, 강남역, 양재역, 10)).getId();
        삼호선 = LineSteps.노선_생성(new LineRequest("삼호선", Color.ORANGE, 교대역, 남부터미널역, 10)).getId();
        SectionSteps.라인에_구간을_추가한다(삼호선, new SectionRequest(남부터미널역, 양재역, 1));
    }

    @DisplayName("출발역으로부터 도착역까지의 경로에 있는 역 목록이 조회된다")
    @Test
    void searchPath() {

        // given & when
        PathsResponse pathsResponse = PathSteps.getPath(교대역, 양재역);

        // then
        List<Long> stationIds = pathsResponse.getStationDtoList().stream().map(StationDto::getId).collect(Collectors.toList());
        assertThat(stationIds).containsExactly(교대역, 남부터미널역, 양재역);
    }
}
