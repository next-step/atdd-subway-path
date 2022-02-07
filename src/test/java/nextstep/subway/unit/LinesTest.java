package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LinesTest {
    private final List<Line> lines = new ArrayList<>();

    private static final Line 이호선 = new Line("2호선", "bg-green-600");
    private static final Line 삼호선 = new Line("3호선", "bg-orange-600");
    private static final Line 신분당선 = new Line("신분당선", "bg-pink-600");

    private static final Station 교대역 = new Station(1L, "교대역");
    private static final Station 강남역 = new Station(2L, "강남역");
    private static final Station 양재역 = new Station(3L, "양재역");
    private static final Station 남부터미널역 = new Station(4L, "남부터미널역");

    @BeforeEach
    void setUp() {
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        lines.add(이호선);
        lines.add(삼호선);
        lines.add(신분당선);
    }

    @DisplayName("모든 라인의 지하철역을 가져온다.")
    @Test
    void getAllStations() {
        Lines lines = new Lines(this.lines);

        List<Station> stations = lines.getAllStations();

        assertThat(stations).hasSize(4);
        assertThat(stations).contains(교대역, 강남역, 양재역, 남부터미널역);
    }

    @DisplayName("모든 라인의 구간을 가져온다.")
    @Test
    void getAllSections() {
        Lines lines = new Lines(this.lines);

        List<Section> sections = lines.getAllSections();

        assertThat(sections).hasSize(4);
    }

    @DisplayName("지하철역 아이디로 지하철역 목록 가져오기")
    @Test
    void getStationsByIds() {
        Lines lines = new Lines(this.lines);
        List<Long> ids = new ArrayList<>();
        ids.add(강남역.getId());
        ids.add(교대역.getId());

        List<Station> stations = lines.getStationsByIds(ids);

        assertThat(stations).hasSize(2);
        assertThat(stations).contains(강남역, 교대역);
    }
}
