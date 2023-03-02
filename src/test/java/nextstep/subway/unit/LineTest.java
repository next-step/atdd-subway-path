package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철 노선 테스트")
class LineTest {
    @Test
    @DisplayName("지하철 2호선에 강남역 - 역삼역 구간을 추가할 수 있다")
    void addSection() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        // When
        line.addSection(강남역, 역삼역, 10);

        // Then
        assertThat(line.getStations())
            .isNotEmpty()
            .containsExactly(강남역, 역삼역);

        assertThat(line.getSectionList())
            .extracting(Section::getDistance)
            .containsExactly(10);

        line_section_station_toString_테스트(line);
    }

    @Test
    @DisplayName("지하철 2호선에 강남역 - 역삼역 - 선릉역 구간을 추가할 수 있다")
    void addSection2() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        // When
        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 20);

        // Then
        assertThat(line.getStations())
            .isNotEmpty()
            .containsExactly(강남역, 역삼역, 선릉역);

        assertThat(line.getSectionList())
            .extracting(Section::getDistance)
            .containsExactly(10, 20);
    }

    @Test
    @DisplayName("지하철 2호선에 이미 등록된 노선을 추가할 수 없다")
    void failToAddSectionInCaseOfDuplicatedSection() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 20);

        // Expected
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            line.addSection(역삼역, 선릉역, 20)
        );
    }

    @ParameterizedTest(name = "distance = {0}")
    @ValueSource(ints = {30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40})
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면, 새로운 노선을 등록할 수 없다")
    void failToAddSectionInCaseOfTooMuchDistance(int tooMuchDistance) {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        line.addSection(강남역, 선릉역, 30);

        // Expected
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            line.addSection(역삼역, 선릉역, tooMuchDistance)
        );
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면, 새로운 노선을 등록할 수 없다")
    void failToAddSectionInCaseOfNotExistsStation() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 20);

        Station 삼성역 = new Station("삼성역");
        Station 잠실역 = new Station("잠실역");

        // Expected
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            line.addSection(삼성역, 잠실역, 5)
        );
    }

    @Test
    @DisplayName("지하철 2호선에 상행역 기준으로 구간 목록 중간에 추가할 수 있다")
    void addSectionInBaseOnUpStation() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        // When
        line.addSection(강남역, 선릉역, 30);
        line.addSection(강남역, 역삼역, 10);

        // Then
        assertThat(line.getStations())
            .isNotEmpty()
            .containsExactly(강남역, 역삼역, 선릉역);

        assertThat(line.getSectionList())
            .extracting(Section::getDistance)
            .containsExactly(10, 20);
    }

    @Test
    @DisplayName("지하철 2호선에 하행역 기준으로 구간 목록 중간에 추가할 수 있다")
    void addSectionInBaseOnDownStation() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        // When
        line.addSection(강남역, 선릉역, 30);
        line.addSection(역삼역, 선릉역, 20);

        // Then
        assertThat(line.getStations())
            .isNotEmpty()
            .containsExactly(강남역, 역삼역, 선릉역);

        assertThat(line.getSectionList())
            .extracting(Section::getDistance)
            .containsExactly(10, 20);
    }

    @Test
    @DisplayName("지하철 2호선에 새로운 역을 상행 종점으로 추가할 수 있다")
    void addSectionInBaseOnFirstUpStation() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        // When
        line.addSection(역삼역, 선릉역, 20);
        line.addSection(강남역, 역삼역, 10);

        // Then
        assertThat(line.getStations())
            .isNotEmpty()
            .containsExactly(강남역, 역삼역, 선릉역);

        assertThat(line.getSectionList())
            .extracting(Section::getDistance)
            .containsExactly(10, 20);
    }

    @Test
    @DisplayName("지하철 2호선에 새로운 역을 하행 종점으로 추가할 수 있다")
    void addSectionInBaseOnLastDownStation() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        // When
        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 20);

        // Then
        assertThat(line.getStations())
            .isNotEmpty()
            .containsExactly(강남역, 역삼역, 선릉역);

        assertThat(line.getSectionList())
            .extracting(Section::getDistance)
            .containsExactly(10, 20);
    }

    @Test
    @DisplayName("지하철 2호선에 포함된 역들을 조회할 수 있다")
    void getStations() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 20);

        // When
        List<Station> stations = line.getStations();

        // Then
        assertThat(stations)
            .isNotEmpty()
            .hasSize(3)
            .containsOnly(강남역, 역삼역, 선릉역);
    }

    @Test
    @DisplayName("지하철 2호선에 마지막 구간을 제거할 수 있다")
    void removeSection() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Station 선릉역 = new Station("선릉역");

        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 20);

        // When
        line.removeSection(선릉역);

        // Then
        assertThat(line.getSectionList())
            .isNotEmpty()
            .hasSize(1)
            .first()
            .extracting(it -> it.isSameAsUpStation(강남역) && it.isSameAsDownStation(역삼역))
            .isEqualTo(true);

        assertThat(line.getStations())
            .isNotEmpty()
            .containsOnly(강남역, 역삼역);
    }

    private void line_section_station_toString_테스트(Line line) {
        assertThat(line.toString())
            .isNotBlank();

        assertThat(line.getSectionList())
            .extracting(Section::toString)
            .allMatch(StringUtils::isNotBlank);

        assertThat(line.getStations())
            .extracting(Station::toString)
            .allMatch(StringUtils::isNotBlank);
    }
}
