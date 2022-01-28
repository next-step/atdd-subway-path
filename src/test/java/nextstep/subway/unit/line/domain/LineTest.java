package nextstep.subway.unit.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.model.Distance;
import nextstep.subway.line.domain.model.Line;
import nextstep.subway.station.domain.model.Station;

@DisplayName("Line 단위 테스트")
class LineTest {
    private Line line;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    @BeforeEach
    void setUp() {
        this.upStation = new Station(1L, "초기 상행");
        this.downStation = new Station(2L, "초기 하행");
        this.distance = new Distance(100);
        this.line = new Line(1L, "초기 노선", "bg-red-500");
        this.line.addSection(upStation, downStation, distance);
    }

    @DisplayName("구간 정보 수정")
    @Test
    void edit() {
        String changedName = "변경된 이름";
        String changedColor = "bg-red-500";

        line.edit(changedName, changedColor);

        assertThat(line.getName()).isEqualTo(changedName);
        assertThat(line.getColor()).isEqualTo(changedColor);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionCase1() {
        Station newStation = new Station(3L, "새로운 하행");
        line.addSection(downStation, newStation, distance);

        assertThat(line.getStations())
            .withFailMessage(line.getStations().stream().map(Station::getName).collect(Collectors.joining(",")))
            .containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addSectionCase2() {
        Station newStation = new Station(3L, "새로운 상행");
        line.addSection(newStation, upStation, distance);

        assertThat(line.getStations())
            .containsExactly(newStation, upStation, downStation);
    }

    @DisplayName("구간 목록 중간에 새로운 구간을 추가할 경우")
    @Test
    void addSectionCase3() {
        int beforeLength = line.getLength();
        Station newStation = new Station(3L, "새로운 중간행");
        line.addSection(upStation, newStation, new Distance(1));

        assertThat(line.getStations())
            .withFailMessage(line.getStations().stream().map(Station::getName).collect(Collectors.joining(",")))
            .containsExactly(upStation, newStation, downStation);
        assertThat(line.getLength())
            .isEqualTo(beforeLength);
    }

    @DisplayName("노선 구간 중간에 새로운 구간을 등록할때 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionFailCase1() {
        Station newStation = new Station(3L, "새로운 중간행");
        assertThatThrownBy(() -> line.addSection(upStation, newStation, new Distance(99999999)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionFailCase2() {
        assertThatThrownBy(() -> line.addSection(upStation, downStation, distance))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있지 않다면 추가할 수 없음")
    @Test
    void addSectionFailCase3() {
        Station newUpStation = new Station(3L, "새로운 상행");
        Station newDownStation = new Station(4L, "새로운 하행");
        assertThatThrownBy(() -> line.addSection(newUpStation, newDownStation, distance))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        Station newUpStation = new Station(3L, "새로운 상행");
        line.addSection(newUpStation, upStation, distance);
        assertThat(line.getStations())
            .containsExactly(newUpStation, upStation, downStation);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void deleteSection() {
        Station newDownStation = new Station(3L, "새로운 구간의 하행");
        line.addSection(downStation, newDownStation, distance);
        line.deleteSection(newDownStation.getId());

        assertThat(line.getStations())
            .containsExactly(upStation, downStation);
    }

    @DisplayName("모든 구간의 길이를 더한 값 반환")
    @Test
    void getLength() {
        assertThat(line.getLength()).isEqualTo(distance.getValue());
    }
}
