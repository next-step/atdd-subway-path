package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @Test
    void update() {
        // given
        final Line line = new Line("2호선", "bg-green-600");

        // when
        line.update("신분당선", "bg-red-600");

        // then
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("bg-red-600");
    }

    @Test
    void addSectionBetweenStations() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");

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
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");

        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(선릉역, 강남역, 10);

        // then
        assertThat(이호선.getStations()).containsExactly(선릉역, 강남역, 역삼역);
    }

    @Test
    void addSectionWithLastDownStation() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");

        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // then
        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @Test
    void cannotAddSectionBetweenStationsWithInvalidDistance() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
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
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
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
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);
        final Station 당산역 = new Station("당산역");
        final Station 선유도역 = new Station("선유도역");

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(당산역, 선유도역, 7);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getStations() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
        이호선.addSection(강남역, 역삼역, 10);

        // when
        final List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsAnyOf(강남역, 역삼역);
    }

    @Test
    void removeSection() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when
        이호선.removeLastSection(선릉역);

        // then
        final List<Station> stations = 이호선.getStations();
        assertThat(stations).containsOnly(강남역, 역삼역);
    }
}
