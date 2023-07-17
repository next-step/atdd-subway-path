package nextstep.subway.acceptance.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_생성한다;
import static nextstep.subway.acceptance.line.SectionSteps.지하철구간을_등록한다;
import static nextstep.subway.acceptance.path.PathSteps.최단경로_조회에_실패한다;
import static nextstep.subway.acceptance.path.PathSteps.최단경로를_조회한다;
import static nextstep.subway.acceptance.station.StationSteps.지하철역을_생성한다;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.applicaion.station.response.StationResponse;

@DisplayName("지하철 경로 관리 기능")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역, 강남역, 양재역, 남부터미널역, 광교역;

    //
    //   교대역    --- 10 --- 강남역
    //     |                   |
    //     5                   5
    //     |                   |
    // 남부터미널역  --- 5 --- 양재역
    //

    @BeforeEach
    public void setUp() {
        교대역 = 지하철역을_생성한다("교대역").getId();
        강남역 = 지하철역을_생성한다("강남역").getId();
        양재역 = 지하철역을_생성한다("양재역").getId();
        남부터미널역 = 지하철역을_생성한다("남부터미널역").getId();
        광교역 = 지하철역을_생성한다("광교역").getId();

        지하철노선을_생성한다(교대역, 강남역, 10);
        지하철노선을_생성한다(강남역, 양재역, 5);
        지하철구간을_등록한다(지하철노선을_생성한다(교대역, 남부터미널역, 5).getId(), 남부터미널역, 양재역, 5);
    }

    @DisplayName("지하철 최단경로를 조회한다")
    @Nested
    class showShortestPath {

        @Test
        void success() {
            // when
            final var response = 최단경로를_조회한다(교대역, 양재역);

            // then
            final var distance = response.getDistance();
            final var stations = response.getStations().stream()
                    .map(StationResponse::getId)
                    .collect(Collectors.toUnmodifiableList());

            assertAll(
                    () -> assertThat(distance).isEqualTo(10),
                    () -> assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역)
            );
        }

        @Nested
        class Fail {

            @Test
            void 출발역과_도착역이_같아선_안된다() {
                최단경로_조회에_실패한다(강남역, 강남역);
            }

            @Test
            void 출발역과_도착역이_연결되어_있어야_한다() {
                최단경로_조회에_실패한다(강남역, 광교역);
            }

            @Test
            void 존재하지_않는_역은_출발역이_될_수_없다() {
                최단경로_조회에_실패한다(0L, 강남역);
            }

            @Test
            void 존재하지_않는_역은_도착역이_될_수_없다() {
                최단경로_조회에_실패한다(강남역, 0L);
            }
        }
    }
}
