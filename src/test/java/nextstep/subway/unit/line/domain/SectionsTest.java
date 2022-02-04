package nextstep.subway.unit.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

@DisplayName("Sections 단위 테스트")
class SectionsTest {
    private long sectionIdCounter;
    private Sections sections;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    @BeforeEach
    void setUp() {
        this.sectionIdCounter = 0;
        this.distance = new Distance(100);
        this.upStation = new Station(1L, "상행");
        this.downStation = new Station(2L, "하행");
        this.sections = new Sections(new ArrayList<>());
        addSection(upStation, downStation, distance);
    }

    private void addSection(Station upStation, Station downStation, Distance distance) {
        Section section = Section.builder()
            .id(++sectionIdCounter)
            .upStation(upStation)
            .downStation(downStation)
            .distance(distance)
            .build();
        this.sections.add(section);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addCase1() {
        Station newStation = new Station(3L, "새로운 하행");
        addSection(downStation, newStation, distance);

        assertThat(sections.toStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addCase2() {
        Station newStation = new Station(3L, "새로운 상행");
        addSection(newStation, upStation, distance);

        assertThat(sections.toStations()).containsExactly(newStation, upStation, downStation);
    }

    @DisplayName("구간 목록 중간에 새로운 구간을 추가할 경우")
    @Test
    void addCase3() {
        int beforeLength = sections.totalDistance();
        Station newStation = new Station(3L, "새로운 중간행");
        addSection(upStation, newStation, new Distance(1));

        assertThat(sections.toStations()).containsExactly(upStation, newStation, downStation);
        assertThat(sections.totalDistance()).isEqualTo(beforeLength);
    }

    @DisplayName("노선 구간 중간에 새로운 구간을 등록할때 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addFailCase1() {
        Station newStation = new Station(3L, "새로운 중간행");
        assertThatThrownBy(() -> addSection(upStation, newStation, new Distance(99999999))).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addFailCase2() {
        assertThatThrownBy(() -> addSection(upStation, downStation, distance)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있지 않다면 추가할 수 없음")
    @Test
    void addFailCase3() {
        Station newUpStation = new Station(3L, "새로운 상행");
        Station newDownStation = new Station(4L, "새로운 하행");
        assertThatThrownBy(() -> addSection(newUpStation, newDownStation, distance)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void toStations() {
        Station newUpStation = new Station(3L, "새로운 상행");
        addSection(newUpStation, upStation, distance);
        assertThat(sections.toStations()).containsExactly(newUpStation, upStation, downStation);
    }

    @DisplayName("구간 목록에서 마지막 역 삭제")
    @Test
    void deleteCase1() {
        Station newDownStation = new Station(3L, "새로운 구간의 하행");
        Station insertNewDownStation = new Station(4L, "중간에 삽입될 하행");
        addSection(downStation, newDownStation, distance);
        addSection(downStation, insertNewDownStation, new Distance(1));
        sections.delete(newDownStation);

        assertThat(sections.toStations()).containsExactly(upStation, downStation, insertNewDownStation);
    }

    @DisplayName("구간 목록에서 첫번째 역 삭제")
    @Test
    void deleteCase2() {
        Station newDownStation = new Station(3L, "새로운 구간의 하행");
        addSection(downStation, newDownStation, distance);
        sections.delete(upStation);

        assertThat(sections.toStations()).containsExactly(downStation, newDownStation);
    }

    @DisplayName("구간 목록에서 중간 역 삭제")
    @Test
    void deleteCase3() {
        Station centerStation = downStation;
        Station newDownStation = new Station(3L, "새로운 구간의 하행");
        addSection(centerStation, newDownStation, new Distance(120));
        int beforeLength = sections.totalDistance();

        sections.delete(centerStation);

        assertThat(sections.toStations())
            .containsExactly(upStation, newDownStation);
        assertThat(sections.totalDistance()).isEqualTo(beforeLength);
    }

    @DisplayName("구간 목록이 1개만 있을땐 삭제할 수 없다.")
    @Test
    void deleteFailCase1() {
        assertThatThrownBy(() -> sections.delete(downStation))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 구간의 길이를 더한 값 반환")
    @Test
    void totalDistance() {
        assertThat(sections.totalDistance()).isEqualTo(distance.getValue());
    }

    @DisplayName("리스트 합치기")
    @Test
    void union() {
        Section dummySection = Section.builder().build();
        Sections dummySections = new Sections(Collections.singletonList(dummySection));

        assertThat(sections.union(dummySections).getValues().size()).isEqualTo(2);
    }
}
