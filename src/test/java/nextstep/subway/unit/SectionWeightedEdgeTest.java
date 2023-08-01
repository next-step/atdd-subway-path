package nextstep.subway.unit;

import nextstep.subway.path.domain.SectionWeightedEdge;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DisplayName("SectionWeightedEdge 테스트")
class SectionWeightedEdgeTest {

    private Station 강남역, 역삼역, 잠실역;
    private final int distance = 2;
    @BeforeEach
    void setUp() {
        강남역 = createStation(1L, "강남역");
        역삼역 = createStation(2L, "역삼역");
        잠실역 = createStation(3L, "잠실역");
    }

    @DisplayName("SectionWeightedEdge의 source는 Section의 upStation과 같다.")
    @Test
    void getSource() {
        // given
        Section section = section(강남역, 역삼역, distance);

        // when
        SectionWeightedEdge edge = new SectionWeightedEdge(section);

        // then
        assertThat(edge.getSource()).isEqualTo(강남역);
    }

    @DisplayName("SectionWeightedEdge의 target은 Section의 downStation과 같다.")
    @Test
    void getTarget() {
        // given
        Section section = section(강남역, 역삼역, distance);

        // when
        SectionWeightedEdge edge = new SectionWeightedEdge(section);

        // then
        assertThat(edge.getTarget()).isEqualTo(역삼역);
    }

    @DisplayName("SectionWeightedEdge의 weight는 Section의 distance와 같다.")
    @Test
    void getWeight() {
        // given
        Section section = section(강남역, 역삼역, distance);

        // when
        SectionWeightedEdge edge = new SectionWeightedEdge(section);

        // then
        assertThat(edge.getWeight()).isEqualTo(distance);
    }

    private Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);

        return station;
    }

    private Section section(Station upStation, Station downStation, int distance) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}