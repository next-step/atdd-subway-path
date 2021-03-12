package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    private Station 야탑역;
    private Station 이매역;
    private Station 서현역;

    private Line 분당선;

    @BeforeEach
    void setUp() {
        // given (라인을 생성한다)
        야탑역 = new Station("야탑역");
        이매역 = new Station("이매역");
        서현역 = new Station("서현역");
        분당선 = new Line("분당선", "YELLOW", 야탑역, 이매역, 10);
    }

    @Test
    void getStations() {
        // when (역 정보를 가져온다)
        List<Station> stations = 분당선.getStations();

        // then (역 개수를 확인한다)
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    void addSection() {
        // when (구간 정보를 추가한다)
        Section section = new Section(분당선, 이매역, 서현역, 5);
        분당선.addSection(section);

        // then (구간 개수를 확인한다)
        assertThat(분당선.getSections()).hasSize(2);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        // when (중간에 구간 정보를 추가한다) & then (익셉션 발생)
        Section section = new Section(분당선, 야탑역, 서현역, 5);
        assertThatThrownBy(() -> 분당선.addSection(section)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        // when (이미 존재하는 역을 추가한다) & then (익셉션 발생)
        Section section = new Section(분당선, 이매역, 야탑역, 5);
        assertThatThrownBy(() -> 분당선.addSection(section)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void removeSection() {
        // given (기존 라인에 구간을 하나 더 추가한다)
        Section section = new Section(분당선, 이매역, 서현역, 5);
        분당선.addSection(section);

        // when (역을 삭제한다)
        분당선.removeSectionByStationId(서현역.getId());

        // then (구간 개수를 확인한다)
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // when (구간을 1개만 추가 후 삭제 요청한다) & then (익셉션 발생)
        assertThatThrownBy(() -> 분당선.removeSectionByStationId(이매역.getId())).isInstanceOf(RuntimeException.class);
    }
}
