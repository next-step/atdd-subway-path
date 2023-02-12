package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Line line;
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    private final int DEFAULT_DISTANCE = 10;

    @BeforeEach
    void setUp() {
        line = new Line("1호선", "blue");
        stationA = new Station("A");
        stationB = new Station("B");
        stationC = new Station("C");
        stationD = new Station("D");
        stationE = new Station("E");
        Section AtoB = Section.builder(line)
                .setUpStation(stationA)
                .setDownStation(stationB)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(AtoB);
    }

    @Test
    @DisplayName("상행역 종점 구간 추가")
    void addSectionFirstSection() {
        //when 노선의 상행 종점을 하행역으로 하는 구간을 추가한다.
        Section CtoA = Section.builder(line)
                .setUpStation(stationC)
                .setDownStation(stationA)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(CtoA);
        //then 역을 조회하면 추가한 구간의 상행역이 노선의 상행 종점이다.
        assertThat(line.getAllStations()).containsExactly(stationC, stationA, stationB);
    }

    @Test
    @DisplayName("하행역 종점 구간 추가")
    void addSectionLastSection() {
        //when 노선의 상행 종점을 하행역으로 하는 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //then 역을 조회하면 추가한 구간의 상행역이 노선의 상행 종점이다.
        assertThat(line.getAllStations()).containsExactly(stationA, stationB, stationC);
    }

    @Test
    @DisplayName("기존 노선에 상행역, 하행역 모두 없는 구간 추가")
    void addSectionNoneContain() {
        //when 노선의 상행 종점을 하행역으로 하는 구간을 추가한다.
        Section DtoE = Section.builder(line)
                .setUpStation(stationD)
                .setDownStation(stationE)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        //then 오류 발생
        assertThatThrownBy(() -> line.addSection(DtoE))
                .isInstanceOf(SubwayException.class);
    }


    @Test
    @DisplayName("기존 노선에 상행역, 하행역 모두 있는 구간 추가")
    void addSectionContainAll() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);

        //when 기존 노선에 상행역, 하행역 모두 있는 구간 추가한다.
        Section AtoC = Section.builder(line)
                .setUpStation(stationA)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        //then 오류 발생
        assertThatThrownBy(() -> line.addSection(AtoC))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("하행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualDownStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 하행역이 같은 구간을 추가한다.
        Section DtoC = Section.builder(line)
                .setUpStation(stationD)
                .setDownStation(stationC)
                .setDistance(5)
                .build();
        line.addSection(DtoC);
        //then 추가된 구간이 사이에 조회된다. 이전 노선의 길이가 줄어든다. 상행 ~ 하행 순서로 역이 조회된다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationA, stationB, stationD, stationC),
                () -> assertThat(BtoC.getDistance()).isEqualTo(5),
                () -> assertThat(BtoC.getDownStation()).isEqualTo(stationD)
        );
    }

    @Test
    @DisplayName("길이가 더 길고 하행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualDownStationAndGreaterDistance() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 길이가 같거나 길고 하행역이 같은 구간을 추가한다.
        Section DtoC = Section.builder(line)
                .setUpStation(stationD)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE*2)
                .build();
        //then 오류 발생
        assertThatThrownBy(() -> line.addSection(DtoC))
                .isInstanceOf(SubwayException.class);
    }
    @Test
    @DisplayName("길이가 더 같고 하행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualDownStationAndEqualDistance() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 길이가 같거나 길고 하행역이 같은 구간을 추가한다.
        Section DtoC = Section.builder(line)
                .setUpStation(stationD)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        //then 오류 발생
        assertThatThrownBy(() -> line.addSection(DtoC))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("상행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualUpStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 상행역이 같은 구간을 추가한다.
        Section BtoD = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationD)
                .setDistance(DEFAULT_DISTANCE/2)
                .build();
        line.addSection(BtoD);
        //then 추가된 구간이 사이에 조회된다. 이전 노선의 길이가 줄어든다. 상행~하행 순서로 역이 조회된다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationA, stationB, stationD, stationC),
                () -> assertThat(BtoC.getDistance()).isEqualTo(DEFAULT_DISTANCE/2),
                () -> assertThat(BtoC.getUpStation()).isEqualTo(stationD)
        );
    }

    @Test
    @DisplayName("길이가 더 길고 상행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualUpStationAndGreaterDistance() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 상행역이 같은 구간을 추가한다.
        Section BtoD = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationD)
                .setDistance(DEFAULT_DISTANCE*2)
                .build();
        //then 오류 발생
        assertThatThrownBy(() -> line.addSection(BtoD))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("길이가 같고 상행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualUpStationAndEqualDistance() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 상행역이 같은 구간을 추가한다.
        Section BtoD = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationD)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        //then 오류 발생
        assertThatThrownBy(() -> line.addSection(BtoD))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("역 삭제")
    void removeStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 노선에서 사이 역1개를 삭제한다.
        line.remove(stationB);
        //then 노선을 조회하면 삭제한 역이x 조회되지 않고, 구간의 길이는 두 구간의 합이다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationA, stationC),
                () -> assertThat(line.getSections().getValuesOrderBy()).hasSize(1),
                () -> assertThat(line.getSections().getValuesOrderBy()
                        .get(0).getDistance()).isEqualTo(DEFAULT_DISTANCE*2)
        );
    }

    @Test
    @DisplayName("상행 종점역 삭제")
    void removeFirstStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 노선에서 상행 종점역을 삭제한다.
        line.remove(stationA);
        //then 노선을 조회하면 삭제한 역이x 조회되지 않는다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationB, stationC),
                () -> assertThat(line.getSections().getValuesOrderBy()).hasSize(1),
                () -> assertThat(line.getSections().getValuesOrderBy()
                        .get(0).getDistance()).isEqualTo(DEFAULT_DISTANCE)
        );
    }

    @Test
    @DisplayName("하행 종점역 삭제")
    void removeLastStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        line.addSection(BtoC);
        //when 노선에서 사이 역1개를 삭제한다.
        line.remove(stationC);
        //then 노선을 조회하면 삭제한 역이x 조회되지 않고, 구간의 길이는 두 구간의 합이다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationA, stationB),
                () -> assertThat(line.getSections().getValuesOrderBy()).hasSize(1),
                () -> assertThat(line.getSections().getValuesOrderBy()
                        .get(0).getDistance()).isEqualTo(DEFAULT_DISTANCE)
        );
    }

    @Test
    @DisplayName("구간이 하나인 노선은 역을 삭제할 수 없다.")
    void removeWhenHaveOneSection() {
        //then 역을 삭제하면 오류가 난다.
        assertAll(
                ()->assertThatThrownBy(() -> line.remove(stationA)).isInstanceOf(SubwayException.class)
                        .hasMessageContaining(SubwayExceptionMessage.STATION_CANNOT_REMOVE.getMessage()),
                ()->assertThatThrownBy(() -> line.remove(stationB)).isInstanceOf(SubwayException.class)
                        .hasMessageContaining(SubwayExceptionMessage.STATION_CANNOT_REMOVE.getMessage())
        );

    }

    @Test
    @DisplayName("노선에 없는 역은 삭제할 수 없다.")
    void removeNotContainStation() {
        //then 역을 삭제하면 오류가 난다.
        assertAll(
                ()->assertThatThrownBy(() -> line.remove(stationC)).isInstanceOf(SubwayException.class)
                        .hasMessageContaining(SubwayExceptionMessage.STATION_NOT_CONTAINED.getMessage())
        );

    }

}
