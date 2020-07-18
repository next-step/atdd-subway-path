package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.PathFinderResult;
import nextstep.subway.path.ui.FindType;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 찾는 PathFinder 도메인에 대한 유닛 테스트")
class PathFinderTest {

    private List<LineResponse> lineResponses;

    @BeforeEach
    void setUp() {
        final StationResponse 강남역 = createStation(1L, "강남역");
        final StationResponse 역삼역 = createStation(2L, "역삼역");
        final StationResponse 선릉역 = createStation(3L, "선릉역");
        final StationResponse 양재역 = createStation(4L, "양재역");
        final StationResponse 양시숲 = createStation(5L, "양재시민의숲");

        // 2호선
        final LineStationResponse lsr1 = new LineStationResponse(강남역, null, 0, 0);
        final LineStationResponse lsr2 = new LineStationResponse(역삼역, 강남역.getId(), 10, 10);
        final LineStationResponse lsr3 = new LineStationResponse(선릉역, 역삼역.getId(), 10, 10);

        // 신분당
        final LineStationResponse lsr4 = new LineStationResponse(강남역, null, 0, 0);
        final LineStationResponse lsr5 = new LineStationResponse(양재역, 강남역.getId(), 20, 10);
        final LineStationResponse lsr6 = new LineStationResponse(양시숲, 양재역.getId(), 20, 10);

        final LineResponse lineResponse1 = createLineResponse(1L, "2호선", "GREEN", List.of(lsr1, lsr2, lsr3));
        final LineResponse lineResponse2 = createLineResponse(2L, "신분당", "RED", List.of(lsr4, lsr5, lsr6));
        lineResponses = new ArrayList<>();
        lineResponses.add(lineResponse1);
        lineResponses.add(lineResponse2);
    }

    @DisplayName("findPath 메서드 테스트 - duration")
    @Test
    void findPathWithDuration() {

        // given
        final PathFinder pathFinder = new PathFinder();
        final long srcStationId = 3;
        final long dstStationId = 5;
        final FindType type = FindType.DURATION;

        // when
        final PathFinderResult pfr = pathFinder.findPath(lineResponses, srcStationId, dstStationId, type);

        // then
        assertThat(pfr.getStationIds()).containsExactlyElementsOf(List.of(3L, 2L, 1L, 4L, 5L));
        assertThat(pfr.getWeight()).isEqualTo(40);
    }

    private StationResponse createStation(long id, String name) {
        return new StationResponse(id, name, LocalDateTime.now(), LocalDateTime.now());
    }

    private LineResponse createLineResponse(long id, String name, String color, List<LineStationResponse> lineStationResponses) {
        return new LineResponse(id, name, color, LocalTime.now(), LocalTime.now(), 5, lineStationResponses, LocalDateTime.now(), LocalDateTime.now());
    }
}