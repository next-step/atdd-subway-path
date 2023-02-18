package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.line.InvalidRemoveStationException;
import nextstep.subway.domain.exception.line.LineHasNotEnoughSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.AssertionUtils.목록은_다음을_순서대로_포함한다;
import static nextstep.subway.utils.AssertionUtils.목록은_다음을_포함하지_않는다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Line line;
    private Section sectionA;
    private Section sectionB;

    @BeforeEach
    void setUp() {
        stationA = new Station("stationA");
        stationB = new Station("stationB");
        stationC = new Station("stationC");

        line = new Line("lineA", "lineA-color");

        sectionA = new Section(line, stationA, stationB, 10);
        sectionB = new Section(line, stationB, stationC, 10);
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        line.addSection(sectionA);

        assertThat(line.hasEmptySection()).isFalse();
    }

    @DisplayName("지하철 노선에 포함된 역 목록을 조회한다.")
    @Test
    void getStations() {
        line.addSection(sectionA);

        final List<Station> stations = line.getStations();

        목록은_다음을_순서대로_포함한다(stations, stationA, stationB);
    }

    @DisplayName("지하철 노선의 하행 종점역을 삭제한다.")
    @Test
    void removeLastStation() {
        line.addSection(sectionA);
        line.addSection(sectionB);

        line.removeStation(stationC);

        목록은_다음을_포함하지_않는다(line.getStations(), stationC);
        목록은_다음을_순서대로_포함한다(line.getStations(), stationA, stationB);
    }


    @DisplayName("지하철 노선의 상행 종점역을 삭제한다.")
    @Test
    void removeFirstStation() {
        line.addSection(sectionA);
        line.addSection(sectionB);

        line.removeStation(stationA);

        목록은_다음을_포함하지_않는다(line.getStations(), stationA);
        목록은_다음을_순서대로_포함한다(line.getStations(), stationB, stationC);
    }

    @DisplayName("지하철 노선의 중간역을 삭제한다.")
    @Test
    void removeMiddleStation() {
        line.addSection(sectionA);
        line.addSection(sectionB);

        line.removeStation(stationB);

        목록은_다음을_포함하지_않는다(line.getStations(), stationB);
        목록은_다음을_순서대로_포함한다(line.getStations(), stationA, stationC);
    }

    @DisplayName("지하철 노선의 중간역을 삭제하면 재배치된 구간의 역 길이는 삭제된 구간의 합과 같다.")
    @Test
    void removeMiddleStationDistance() {
        line.addSection(sectionA);
        line.addSection(sectionB);

        line.removeStation(stationB);

        final Section newSection = line.getSections().getOrderedSections().get(0);
        final int newDistance = sectionA.getDistance() + sectionB.getDistance();
        assertThat(newSection.getDistance()).isEqualTo(newDistance);
    }

    @DisplayName("구간이 없는 노선의 역을 삭제하려하면 오류가 발생한다.")
    @Test
    void removeException() {
        assertThatThrownBy(() -> line.removeStation(stationA))
                .isInstanceOf(LineHasNotEnoughSectionException.class);
    }

    @DisplayName("구간이 하나인 노선의 역을 삭제하려하면 오류가 발생한다.")
    @Test
    void removeException2() {
        line.addSection(sectionA);

        assertThatThrownBy(() -> line.removeStation(stationA))
                .isInstanceOf(LineHasNotEnoughSectionException.class);
    }

    @DisplayName("노선에 없는 역을 삭제하려하면 오류가 발생한다.")
    @Test
    void removeException3() {
        line.addSection(sectionA);
        line.addSection(sectionB);

        final Station station = new Station("exceptionStation");

        assertThatThrownBy(() -> line.removeStation(station))
                .isInstanceOf(InvalidRemoveStationException.class);
    }
}
