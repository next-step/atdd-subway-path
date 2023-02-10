package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    public void setUp() {
        이호선 = new Line("2호선", "bg-green-600");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
    }

    @Test
    void update() {
        // when
        이호선.update("신분당선", "bg-red-600");

        // then
        assertThat(이호선.getName()).isEqualTo("신분당선");
        assertThat(이호선.getColor()).isEqualTo("bg-red-600");
    }

    @Test
    void addSectionBetweenStations() {
        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(강남역, 선릉역, 9);

        // then
        assertThat(이호선.getStations()).containsExactly(강남역, 선릉역, 역삼역);
        assertThat(이호선.getSections().stream()
                .filter(section -> section.getUpStation().equals(강남역))
                .filter(section -> section.getDownStation().equals(선릉역))
                .findFirst().get()
                .getDistance()).isEqualTo(9);
        assertThat(이호선.getSections().stream()
                .filter(section -> section.getUpStation().equals(선릉역))
                .filter(section -> section.getDownStation().equals(역삼역))
                .findFirst().get()
                .getDistance()).isEqualTo(1);
    }

    @Test
    void addSectionWithLastUpStation() {
        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(선릉역, 강남역, 10);

        // then
        assertThat(이호선.getStations()).containsExactly(선릉역, 강남역, 역삼역);
    }

    @Test
    void addSectionWithLastDownStation() {
        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // then
        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @Test
    void cannotAddSectionBetweenStationsWithInvalidDistance() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(강남역, 선릉역, 10);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            이호선.addSection(강남역, 선릉역, 11);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cannotAddSectionUpStationAndDownStationIsAlreadyExists() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(강남역, 선릉역, 3);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cannotAddSectionUpStationAndDownStationDoesNotContainInLine() {
        // given
        final Station 당산역 = new Station("당산역");
        final Station 선유도역 = new Station("선유도역");
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(당산역, 선유도역, 7);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getStations() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when
        final List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsAnyOf(강남역, 역삼역);
    }

    @Test
    void removeSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when
        이호선.removeLastSection(선릉역);

        // then
        final List<Station> stations = 이호선.getStations();
        assertThat(stations).containsOnly(강남역, 역삼역);
    }
}
