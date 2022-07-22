package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionsTest {
    private Sections sections;
    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("신논현역");
        downStation = new Station("언주역");
        line = new Line("신분당선", "bg-red-600");

        sections = new Sections();
        sections.addSection(new Section(line, upStation, downStation, 10));
    }

    @DisplayName("노선에 구간이 하행 종점역에 정상적으로 등록되었습니다.")
    @Test
    void addSections() {
        // given
        Station newStation = new Station("신규역");

        // when
        sections.addSection(new Section(line, downStation, newStation, 3));

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("노선에 등록할 구간이 이미 존재하는 경우 예외처리를 한다.")
    @Test
    void checkDuplicationExistingSection() {
        // given
        Station duplicateUpStation = new Station("신논현역");
        Station duplicateDownStation = new Station("언주역");

        // when & then
        assertThatThrownBy(() -> sections.addSection(new Section(line, duplicateUpStation, duplicateDownStation, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간의 중간에 신규 역을 정상 등록한다.")
    @Test
    void addSectionInMiddleOfLine() {
        // given
        Station newStation = new Station("신규역");

        // when
        sections.addSection(new Section(line, upStation, newStation, 3));

        // then
        List<Integer> findSectionsDistance = sections.getSections().stream().map(Section::getDistance)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(sections.getStations()).containsExactly(upStation, newStation, downStation),
                () -> assertThat(findSectionsDistance).containsExactly(3, 7)
        );
    }

    @ParameterizedTest(name = "구간의 중간에 등록할 신규역의 길이가 {0}로 기존 구간의 길이보다 같거나 크면 예외처리 된다.")
    @ValueSource(ints = {10, 15})
    void exceptionDistanceOfNewSectionOverExistingSectionDistance(int distance) {
        // given
        Station newStation = new Station("신규역");

        // when & then
        assertThatThrownBy(() -> sections.addSection(new Section(line, upStation, newStation, distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 구간을 등록할 때, 기존 구간중 상/하행역 둘 중 하나라도 일치하지 않는 경우, 예외처리 된다.")
    @Test
    void exceptionNotMatchedExistingSection() {
        // given
        Station gangnamStation = new Station("강남역");
        Station seolleungStation = new Station("선릉역");

        // when & then
        assertThatThrownBy(() -> sections.addSection(new Section(line, gangnamStation, seolleungStation, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}