package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotAddSectionException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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

        List<String> namesOfStations = getNamesOfStations(loopLine);

        // then
        assertThat(namesOfStations).containsExactly(sunreoung.getName(), gangnam.getName(), yeoksam.getName());

    }

    @DisplayName("하행 종점역 추가")
    @Test
    void addLastDownSection() {
        // given
        Section section = Section.of(loopLine, yeoksam, sunreoung, 10);

        // when
        loopLine.addSection(section);

        List<String> namesOfStations = getNamesOfStations(loopLine);

        // then
        assertThat(namesOfStations).containsExactly(gangnam.getName(), yeoksam.getName(), sunreoung.getName());

    }

    @DisplayName("추가하려는 구간이 이미 존재하는 경우 구간 추가 실패")
    @Test
    void addSectionExistBothException() {
        //given
        Section existSection = Section.of(loopLine, yeoksam, sunreoung, 10);
        loopLine.addSection(existSection);

        Section sectionForAdd = Section.of(loopLine, gangnam, sunreoung, 10);

        //when
        ThrowableAssert.ThrowingCallable actual = () -> loopLine.addSection(sectionForAdd);

        //then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage(String.format("추가하려는 역이 이미 노선에 존재합니다. %s, %s", gangnam.getName(), sunreoung.getName()));
    }

    @DisplayName("기존 구간의 길이와 같거나 큰 길이의 구간 추가 실패")
    @ParameterizedTest(name = "구간 길이 추가 실패 [{index}] [{arguments}]")
    @ValueSource(ints = {10, 11})
    void addSectionGreaterThanDistanceException(int distance) {
        //given
        Section sectionForAdd = Section.of(loopLine, gangnam, sunreoung, distance);

        //when
        ThrowableAssert.ThrowingCallable actual = () -> loopLine.addSection(sectionForAdd);

        //then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("기존 구간의 길이보다 같거나 크면 추가할 수 없습니다. 기존 구간의 길이 : 10, 신규 구간의 길이 : " + distance);
    }


    @DisplayName("존재하지 않은 역을 추가하는 경우 구간 추가 실패")
    @Test
    void addSectionNonStationException() {
        //given
        Station cityHall = Station.from("시청역");
        Station seoul = Station.from("서울역");
        Section sectionForAdd = Section.of(loopLine, cityHall, seoul, 10);

        //when
        ThrowableAssert.ThrowingCallable actual = () -> loopLine.addSection(sectionForAdd);

        //then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("기존 구간과 연결되는 역이 없습니다.");
    }

    @DisplayName("역과 역사이 신규 구간 추가")
    @ParameterizedTest(name = "역과 역 사이 추가 [{index}] [{arguments}]")
    @MethodSource
    void addSection(Station upStation, Station downStation, int distance, int expectedFistDistance, int expectedSecondDistance) {
        // given
        Section section = Section.of(loopLine, upStation, downStation, distance);

        // when
        loopLine.addSection(section);

        List<String> namesOfStations = getNamesOfStations(loopLine);
        List<Integer> distances = loopLine.distances();

        // then
        assertAll(
                () -> assertThat(namesOfStations).containsExactly(gangnam.getName(), sunreoung.getName(), yeoksam.getName()),
                () -> assertThat(distances).containsExactly(expectedFistDistance, expectedSecondDistance)
        );
    }

    private static Stream<Arguments> addSection() {
        return Stream.of(
                Arguments.of(Station.from("강남역"), Station.from("선릉역"), 2, 2, 8),
                Arguments.of(Station.from("선릉역"), Station.from("역삼역"), 4, 6, 4)
        );
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // when
        List<String> namesOfStations = getNamesOfStations(loopLine);

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
        int sizeOfStations = loopLine.stations().size();

        //then
        assertThat(sizeOfStations).isEqualTo(2);

    }

    private List<String> getNamesOfStations(Line line) {
        return line.stations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
    }
}
