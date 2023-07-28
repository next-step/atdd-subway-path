package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestPathException;
import nextstep.subway.unit.fixture.PathFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.unit.fixture.LineFixture.지하철_노선_생성;
import static nextstep.subway.unit.fixture.PathFixture.*;
import static nextstep.subway.unit.fixture.SectionFixture.지하철_구간_생성;
import static nextstep.subway.unit.fixture.StationFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("경로 단위 테스트")
public class PathTest {

    private static final int DEFAULT_DISTANCE = 10;
    private static final int SHORT_DISTANCE = 5;

    private Line 신분당선, 이호선, 삼호선;
    private Station 교대역, 강남역, 양재역, 남부터미널역;

    private List<Section> sections;

    @BeforeEach
    void set() {
        교대역 = 지하철역_생성("교대역");
        강남역 = 지하철역_생성("강남역");
        양재역 = 지하철역_생성("양재역");
        남부터미널역 = 지하철역_생성("남부터미널역");

        신분당선 = 지하철_노선_생성("신분당선", "bg-red-600", 강남역, 양재역, DEFAULT_DISTANCE);
        이호선 = 지하철_노선_생성("2호선", "bg-red-600", 강남역, 교대역, DEFAULT_DISTANCE);
        삼호선 = 지하철_노선_생성("3호선", "bg-red-600", 교대역, 남부터미널역, DEFAULT_DISTANCE);

        삼호선.addSections(지하철_구간_생성(삼호선, 남부터미널역, 양재역, SHORT_DISTANCE));

        sections = new ArrayList<>();
        sections.addAll(신분당선.getSections());
        sections.addAll(이호선.getSections());
        sections.addAll(삼호선.getSections());
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findShortestPath() {
        Path shortestPath = 지하철_최단_경로_조회(sections,교대역, 양재역);

        int stationCount = shortestPath.getStations().size();
        assertThat(stationCount).isEqualTo(3);
        assertThat(shortestPath.getStations().stream().mapToLong(it->it.getId())).containsExactly(교대역.getId(),남부터미널역.getId(),양재역.getId());
        assertThat(shortestPath.getDistance()).isEqualTo(DEFAULT_DISTANCE + SHORT_DISTANCE);
    }

    @DisplayName("연결되지 않은 경로 조회")
    @Test
    void findUnconnetedPath() {
        //given
        Station 대방역 = 지하철역_생성("대방역");
        Station 신림역 = 지하철역_생성("신림역");
        Line 신림선 = 지하철_노선_생성("신림선", "bg-red-600", 대방역, 신림역, DEFAULT_DISTANCE);
        sections.addAll(신림선.getSections());

        assertThrows(BadRequestPathException.class,()->지하철_최단_경로_조회(sections,대방역, 양재역));
    }

}
