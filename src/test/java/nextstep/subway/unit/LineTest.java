package nextstep.subway.unit;

import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Station 당고개역, 이수역, 사당역;

    @BeforeEach
    void setUpStation() {
        당고개역 = 당고개역();
        이수역 = 이수역();
        사당역 = 사당역();
    }

    @DisplayName("새로운 구간을 등록한다. - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addSection() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);

        // when : 기능 수행
        line.addSection(section(line, 당고개역, 사당역, 3));

        // then : 결과 확인
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "사당역", "이수역"
                );
    }

    @DisplayName("새로운 구간을 등록한다. - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection2() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);

        // when : 기능 수행
        line.addSection(section(line, 사당역, 당고개역, 3));

        // then : 결과 확인
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "사당역", "당고개역", "이수역"
                );
    }

    @DisplayName("새로운 구간을 등록한다. - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection3() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);

        // when : 기능 수행
        line.addSection(section(line, 이수역, 사당역, 3));

        // then : 결과 확인
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "이수역", "사당역"
                );
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같을때 예외 발생")
    void addSectionThrowExceptionIsINVALID_DISTANCE(int distance) {
        // given
        Line line = line(당고개역, 이수역);

        // when then
        assertThatThrownBy(() -> line.addSection(section(line, 당고개역, 사당역, distance)))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    @DisplayName("새로운 구간을 등록할 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionThrowExceptionIsALREADY_SECTION() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        line.addSection(section(line, 당고개역, 사당역, 3));

        // when then
        assertThatThrownBy(() -> line.addSection(section(line, 당고개역, 사당역, 3)))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining(ErrorCode.ALREADY_SECTION.getMessage());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionThrowExceptionIsCAN_NOT_BE_ADDED_SECTION() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        line.addSection(section(line, 당고개역, 사당역, 3));
        Station 동작역 = 동작역();
        Station 이촌역 = 이촌역();

        // when  then
        assertThatThrownBy(() -> line.addSection(section(line, 동작역, 이촌역, 3)))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining(ErrorCode.CAN_NOT_BE_ADDED_SECTION.getMessage());
    }

    @DisplayName("노선 조회시 응답 역 목록은 상행역에서 하행역 순으로 정렬되어야 한다.")
    @Test
    void getSections() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        line.addSection(section(line, 당고개역, 사당역, 3));

        // when : 기능 수행
        Set<Station> stations = line.getStations();

        // then : 결과 확인
        assertThat(stations).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "사당역", "이수역"
                );
    }

    @Test
    void removeSection() {
        Line line = line(당고개역, 이수역);
        Section section = section(line, 당고개역, 사당역, 3);
        line.addSection(section);

        // when : 기능 수행
        line.removeSection(이수역);

        // then : 결과 확인
        assertThat(line.getStations()).hasSize(2)
                .extracting("name")
                .containsExactly(
                        "당고개역", "사당역"
                );
    }


    private Line line(Station upStation, Station downStation) {
        return Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .section(section(upStation, downStation))
                .build();
    }

    private Station 당고개역() {
        return new Station("당고개역");
    }

    private Station 이수역() {
        return new Station("이수역");
    }

    private Station 사당역() {
        return new Station("사당역");
    }

    private Station 동작역() {
        return new Station("동작역");
    }

    private Station 이촌역() {
        return new Station("이촌역");
    }

    private Section section(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .build();
    }

    private Section section(Line line, Station upStation, Station downStation, int distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}
