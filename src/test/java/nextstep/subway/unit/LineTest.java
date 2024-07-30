package nextstep.subway.unit;

import nextstep.subway.domain.SectionBuilder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayLine;
import nextstep.subway.domain.SubwayLineBuilder;
import nextstep.subway.utils.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    Persistence persistence = new Persistence();

    @Test
    @DisplayName("지하철 노선 구간을 등록한다")
    void addSection() {
        //given
        var subwayLine = createLineWithOneSection();
        var newDownStation = persistence.persist(new Station(SEOUL_STATION));

        //when
        var sectionToAdd = new SectionBuilder()
                .distance(10L)
                .upStation(subwayLine.getDownStation())
                .downStation(newDownStation)
                .build();
        subwayLine.addSection(sectionToAdd);

        //then
        assertThat(subwayLine.getDownStation().getId()).isEqualTo(newDownStation.getId());
        assertThat(subwayLine.getStations().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("구간의 상행역이 노선의 하행 종점역이 아닌 경우 구간을 등록할 수 없다")
    void addSectionFail() {
        //given
        var subwayLine = createLineWithTwoSection();

        //when
        var sectionToAdd = new SectionBuilder()
                .distance(10L)
                .upStation(subwayLine.getUpStation())
                .downStation(persistence.persist(new Station(PANGYO_STATION)))
                .build();

        //then
        assertThrows(
                UnsupportedOperationException.class,
                () -> subwayLine.addSection(sectionToAdd)
        );
    }

    @Test
    @DisplayName("구간의 하행역이 이미 노선에 등록된 경우 구간을 등록할 수 없다")
    void addSectionFail2() {
        //given
        var subwayLine = createLineWithTwoSection();

        //when
        var sectionToAdd = new SectionBuilder()
                .distance(10L)
                .upStation(subwayLine.getDownStation())
                .downStation(subwayLine.getSections().getStations().get(1))
                .build();

        //then
        assertThrows(
                UnsupportedOperationException.class,
                () -> subwayLine.addSection(sectionToAdd)
        );

    }

    @Test
    @DisplayName("노선에 등록된 역을 조회한다.")
    void getStations() {
        //given
        var subwayLine = createLineWithTwoSection();

        //when
        List<Station> stations = subwayLine.getStations();

        //then
        assertThat(stations.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("지하철 구간 제거에 성공한다")
    void removeSection() {
        //given
        var subwayLine = createLineWithTwoSection();

        //when
        subwayLine.removeSection(subwayLine.getDownStation().getId());

        //then
        assertThat(subwayLine.getStations().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("노선의 하행 종점역 이외의 역을 삭제하면 실패한다.")
    void removeSectionFail() {
        //given
        var subwayLine = createLineWithTwoSection();

        //when
        //then
        assertThrows(
                UnsupportedOperationException.class,
                () -> subwayLine.removeSection(subwayLine.getUpStation().getId())
        );
    }

    @Test
    @DisplayName("노선의 구간이 1개인 경우 역을 삭제하면 실패한다.")
    void removeSectionFail2() {
        //given
        var subwayLine = createLineWithOneSection();

        //when
        //then
        assertThrows(
                UnsupportedOperationException.class,
                () -> subwayLine.removeSection(subwayLine.getDownStation().getId())
        );
    }


    private SubwayLine createLineWithOneSection() {
        var currentUpStation = persistence.persist(new Station(GANGNAM_STATION));
        var currentDownStation = persistence.persist(new Station(YONGSAN_STATION));
        var section = new SectionBuilder()
                .distance(10L)
                .upStation(currentUpStation)
                .downStation(currentDownStation)
                .build();
        return new SubwayLineBuilder()
                .name(LINE_SINBUNDANG)
                .color(COLOR_RED)
                .section(section)
                .build();
    }

    private SubwayLine createLineWithTwoSection() {
        var subwayLine = createLineWithOneSection();

        var newDownStation = persistence.persist(new Station(PANGYO_STATION));
        var sectionToAdd = new SectionBuilder()
                .distance(10L)
                .upStation(subwayLine.getDownStation())
                .downStation(newDownStation)
                .build();
        subwayLine.addSection(sectionToAdd);
        return subwayLine;
    }
}
