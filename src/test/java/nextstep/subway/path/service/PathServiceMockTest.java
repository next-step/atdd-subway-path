package nextstep.subway.path.service;

import nextstep.subway.common.fixture.LineFactory;
import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.service.LineProvider;
import nextstep.subway.path.service.dto.PathResponse;
import nextstep.subway.path.service.dto.PathSearchRequest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
    private final Long 강남역_Id = 1L;
    private final Long 교대역_Id = 2L;
    private final Long 양재역_Id = 3L;
    private final Long 남부터미널역_Id = 4L;
    private final int 교대역_강남역_distance = 5;
    private final int 강남역_양재역_distance = 10;
    private final int 교대역_남부터미널_distance = 2;
    private final int 남부터미널_양재역_distance = 3;
    private static final long 이호선_Id = 2L;
    private static final long 신분당선_Id = 4L;
    private static final long 삼호선_Id = 3L;

    @Mock
    private LineProvider lineProvider;

    @Test
    @DisplayName("findPath 를 통해 최단경로를 반환받을 수 있다.")
    void findPathTest() {
        final PathService pathService = new PathService(lineProvider);
        final PathSearchRequest searchRequest = new PathSearchRequest(강남역_Id, 남부터미널역_Id);
        given(lineProvider.getAllLines()).willReturn(createLines());

        final PathResponse response = pathService.findPath(searchRequest);

        assertThat(response.getDistance()).isEqualTo(교대역_강남역_distance + 교대역_남부터미널_distance);
        assertThat(response.getStations()).extracting("id")
                .containsExactly(강남역_Id, 교대역_Id, 남부터미널역_Id);
    }

    private List<Line> createLines() {
        final Station 교대역 = StationFactory.createStation(교대역_Id, "교대역");
        final Station 강남역 = StationFactory.createStation(강남역_Id, "강남역");
        final Station 양재역 = StationFactory.createStation(양재역_Id, "양재역");
        final Station 남부터미널역 = StationFactory.createStation(남부터미널역_Id, "남부터미널역");
        final Section 교대역_강남역_구간 = SectionFactory.createSection(1L, 교대역, 강남역, 교대역_강남역_distance);
        final Section 강남역_양재역_구간 = SectionFactory.createSection(2L, 강남역, 양재역, 강남역_양재역_distance);
        final Section 교대역_남부터미널_구간 = SectionFactory.createSection(3L, 교대역, 남부터미널역, 교대역_남부터미널_distance);
        final Section 남부터미널_양재역_구간 = SectionFactory.createSection(4L, 남부터미널역, 양재역, 남부터미널_양재역_distance);
        final Line 이호선 = LineFactory.createLine(이호선_Id, "1호선", "green", 교대역_강남역_구간);
        final Line 신분당선 = LineFactory.createLine(신분당선_Id, "1호선", "red", 강남역_양재역_구간);
        final Line 삼호선 = LineFactory.createLine(삼호선_Id, "2호선", "orange", 교대역_남부터미널_구간);
        삼호선.addSection(남부터미널_양재역_구간);
        return List.of(이호선, 신분당선, 삼호선);
    }
}
