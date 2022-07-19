package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
    }

    @Test
    @DisplayName("비어있는 상태에서 구간 추가")
    void emptyStateAddSection() {
        //when
        노선_구간_추가("잠실역", "강남역");

        //then
        assertThat(line.getSections().sections()).hasSize(1);
    }

    @Test
    @DisplayName("존재하는 상태에서 구간 추가")
    void existStateAddSection() {
        //given
        노선_구간_추가("잠실역", "강남역");

        //when
        노선_구간_추가("강남역", "역삼역");

        //then
        assertThat(line.getSections().sections()).hasSize(2);
    }

    @Test
    @DisplayName("해당 노선의 모든 구간 역 정보를 조회")
    void getStations() {
        //given, when
        노선_구간_추가("잠실역", "강남역");

        //then
        assertThat(line.getStations()).containsExactly(new Station("잠실역"), new Station("강남역"));
    }

    @Test
    @DisplayName("노선 구간 삭제")
    void removeSection() {
        //given
        노선_구간_추가("잠실역", "강남역");

        //when
        line.removeStations(new Station("강남역"));

        //then
        assertThat(line.getSections().sections()).isEmpty();
    }

    @Test
    @DisplayName("등록된 역이 없는데 삭제할 경우 예외")
    void notRegisteredStationRemoveException() {
        assertThatThrownBy(() -> line.removeStations(new Station("강남역")))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("등록된 구간이 없습니다.");
    }

    @Test
    @DisplayName("하행 종점역 정보가 맞지 않는 경우 예외")
    void downStationNotEqualRemoveException() {
        //when
        노선_구간_추가("잠실역", "강남역");

        //then
        assertThatThrownBy(() -> line.removeStations(new Station("역삼역")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("하행 종점역 정보가 다릅니다.");
    }

    private void 노선_구간_추가(final String upStation, final String downStation) {
        line.addSections(new Station(upStation), new Station(downStation), 10);
    }
}
