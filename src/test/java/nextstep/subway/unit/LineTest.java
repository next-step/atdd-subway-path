package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station gangnam;
    private Station yeoksam;
    private Station sunreoung;
    private Line loopLine;


    @BeforeEach
    void setUp() {
        gangnam = Station.from("강남역");
        yeoksam = Station.from("역삼역");
        sunreoung = Station.from("선릉역");

        loopLine = Line.of("이호선", "green");
        Section section = new Section(loopLine, gangnam, yeoksam, 10);
        loopLine.addSection(section);
    }

    @DisplayName("상행 종점역 추가")
    @Test
    void addFirstUpSection() {
        // given
        Section section = Section.of(loopLine, sunreoung, gangnam, 10);

        // when
        loopLine.addSection(section);

        List<String> namesOfStations = loopLine.stations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        // then
        assertThat(namesOfStations).containsExactly(sunreoung.getName(), gangnam.getName(), yeoksam.getName());

    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Section section = Section.of(loopLine, yeoksam, sunreoung, 10);

        // when
        loopLine.addSection(section);

        List<String> namesOfStations = loopLine.stations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        // then
        assertThat(namesOfStations).containsExactly(gangnam.getName(), yeoksam.getName(), sunreoung.getName());

    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // when
        List<String> namesOfStations = loopLine.stations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        // then
        assertThat(namesOfStations).containsExactly(gangnam.getName(), yeoksam.getName());
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Section section = Section.of(loopLine, yeoksam, sunreoung, 10);
        loopLine.addSection(section);

        // when
        loopLine.deleteSection(sunreoung);
        List<Section> sections = loopLine.getSections();

        //then
        assertThat(sections).hasSize(1);

    }
}
