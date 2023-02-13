package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.CannotDeleteSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    @Test
    void addSection() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        Line line = new Line("2호선", "green");
        Section section = new Section(line, upStation, downStation, 10);

        // when - then
        assertDoesNotThrow(() -> line.addSection(section));
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        Line line = new Line("2호선", "green");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        line.addSection(section);

        // then
        var stations = line.getAllStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stations).hasSize(2);
        assertThat(stations).contains("강남역", "교대역");
    }

    @Test
    void removeSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        Line line = new Line("2호선", "green");
        Section section1 = new Section(line, 강남역, 교대역, 10);
        Section section2 = new Section(line, 교대역, 서초역, 10);
        line.addSection(section1);
        line.addSection(section2);

        // when
        line.removeSection(서초역);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getAllStations().size()).isEqualTo(2);
        assertThat(line.getAllStations().stream().map(Station::getName).collect(Collectors.toList())).contains("강남역", "교대역");
    }

    @DisplayName("기존 구간의 사이에 새로운 구간을 추가한다.")
    @Test
    void insertSectionIntoExistingSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 서초역 = new Station("서초역");
        Line 이호선 = new Line("이호선", "green");
        Section section = new Section(이호선, 강남역, 서초역, 10);

        assertDoesNotThrow(() -> 이호선.addSection(section));
        assertThat(이호선.getSections().size()).isEqualTo(1);

        // when
        Station 교대역 = new Station("교대역");
        assertDoesNotThrow(() -> 이호선.addSection(new Section(이호선, 강남역, 교대역, 5)));
        assertThat(이호선.getSections().size()).isEqualTo(2);
        assertThat(이호선.getAllStations()).containsExactly(강남역, 교대역, 서초역);
    }

    @DisplayName("기존 구간의 사이에 새로운 구간을 추가할 때, 기존 구간의 거리보다 긴 거리를 지정하면 예외가 발생한다.")
    @Test
    void insertSectionIntoExistingSectionThrowsException() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Line 이호선 = new Line("이호선", "green");
        Section section = new Section(이호선, 강남역, 교대역, 10);

        assertDoesNotThrow(() -> 이호선.addSection(section));
        assertThat(이호선.getSections().size()).isEqualTo(1);

        // when
        assertThrows(
                BadRequestException.class,
                () -> 이호선.addSection(new Section(이호선, 강남역, 교대역, 5)),
                "Both stations are already registered."
        );
    }

    @DisplayName("새로운 구간을 추가할 때, 상행역과 하행역 모두가 이미 노선에 등록되어있다면 예외가 발생한다.")
    @Test
    void addSectionThrowsException() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Line 이호선 = new Line("이호선", "green");
        Section section = new Section(이호선, 강남역, 교대역, 5);

        assertDoesNotThrow(() -> 이호선.addSection(section));
        assertThat(이호선.getSections().size()).isEqualTo(1);

        // when
        Station 서초역 = new Station("서초역");
        assertThrows(
                BadRequestException.class,
                () -> 이호선.addSection(new Section(이호선, 강남역, 서초역, 10)),
                "Distance must be less than existing section's distance."
        );
    }

    @DisplayName("새로운 구간을 추가할 때, 상행역과 하행역 중 하나라도 노선에 포함되어있지 않다면 예외가 발생한다.")
    @Test
    void addSectionThrowsException2() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Line 이호선 = new Line("이호선", "green");
        Section section = new Section(이호선, 강남역, 교대역, 10);

        assertDoesNotThrow(() -> 이호선.addSection(section));
        assertThat(이호선.getSections().size()).isEqualTo(1);

        // when
        Station 서초역 = new Station("서초역");
        Station 방배역 = new Station("방배역");
        assertThrows(
                BadRequestException.class,
                () -> 이호선.addSection(new Section(이호선, 서초역, 방배역, 5)),
                "Neither UpStation nor DownStation is registered in the line."
        );
    }

    @DisplayName("기존 구간의 앞에 새로운 구간을 추가한다.")
    @Test
    void insertSectionBeforeExistingSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 교대역 = new Station("교대역");
        Line 이호선 = new Line("이호선", "green");
        Section section = new Section(이호선, 강남역, 교대역, 10);

        assertDoesNotThrow(() -> 이호선.addSection(section));
        assertThat(이호선.getSections().size()).isEqualTo(1);

        // when
        Station 서초역 = new Station("서초역");
        assertDoesNotThrow(() -> 이호선.addSection(new Section(이호선, 교대역, 서초역, 5)));
        assertThat(이호선.getSections().size()).isEqualTo(2);
        assertThat(이호선.getAllStations()).containsExactly(강남역, 교대역, 서초역);
    }

    @DisplayName("기존 구간의 뒤에 새로운 구간을 추가한다.")
    @Test
    void insertSectionAfterExistingSection() {
        // given
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        Line 이호선 = new Line("이호선", "green");
        Section section = new Section(이호선, 교대역, 서초역, 10);

        assertDoesNotThrow(() -> 이호선.addSection(section));
        assertThat(이호선.getSections().size()).isEqualTo(1);

        // when
        Station 강남역 = new Station("강남역");
        assertDoesNotThrow(() -> 이호선.addSection(new Section(이호선, 강남역, 교대역, 5)));
        assertThat(이호선.getSections().size()).isEqualTo(2);
        assertThat(이호선.getAllStations()).containsExactly(강남역, 교대역, 서초역);
    }

    @DisplayName("구간을 여러개 등록하고 노선에 등록된 모든 역을 조회한다.")
    @Test
    void findAllStations() {
        // given
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");

        Station 강남역 = new Station("강남역");
        Line 이호선 = new Line("이호선", "green");
        Section firstSection = new Section(이호선, 강남역, 교대역, 10);
        Section secondSection = new Section(이호선, 교대역, 서초역, 10);
        이호선.addSection(firstSection);
        이호선.addSection(secondSection);


        // when
        List<Station> stations = 이호선.getAllStations();

        // then
        assertThat(stations).hasSize(3);
        assertThat(stations).containsExactly(강남역, 교대역, 서초역);
    }

    @DisplayName("노선에 등록된 역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");

        Station 강남역 = new Station("강남역");
        Line 이호선 = new Line("이호선", "green");
        Section firstSection = new Section(이호선, 강남역, 교대역, 10);
        Section secondSection = new Section(이호선, 교대역, 서초역, 10);
        이호선.addSection(firstSection);
        이호선.addSection(secondSection);

        // when
        이호선.removeSection(교대역);

        // then
        assertThat(이호선.getAllStations()).containsExactly(강남역, 서초역);
    }

    @DisplayName("노선에 등록되어있지 않은 역을 제거한다.")
    @Test
    void deleteStationNotRegisteredInLine() {
        // given
        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");
        Station 강남역 = new Station("강남역");

        Line 이호선 = new Line("이호선", "green");
        Section firstSection = new Section(이호선, 강남역, 교대역, 10);
        Section secondSection = new Section(이호선, 교대역, 서초역, 10);
        이호선.addSection(firstSection);
        이호선.addSection(secondSection);

        // when
        Station 민정역 = new Station("민정역");

        // then
        assertThatCode(() -> 이호선.removeSection(민정역))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage("Station is not registered in the line.");
    }
}
