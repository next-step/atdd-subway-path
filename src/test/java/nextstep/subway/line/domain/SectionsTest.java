package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.AlreadyExistStation;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NotExistedStation;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    private Line line;
    private Station 신길역;
    private Station 대방역;
    private Station 노량진역;
    private Station 영등포역;

    @BeforeEach
    public void setUp(){
        line = new Line("1호선", "blue");

        영등포역 = new Station("영등포역");
        신길역 = new Station("신길역");
        대방역 = new Station("대방역");
        노량진역 = new Station("노량진");

    }

    @DisplayName("새로운 구간을 추가한다. 기존의 상행역은 새로 등록된 구간의 하행역이 된다." +
            "기존 :             신길역 ---> 대방역" +
            "신규 : 영등포역 ---> 신길역 ---> 대방역"
    )
    @Test
    public void addSection_case1() {
        //Given
        Sections sections = new Sections();
        Section section = new Section(line, 신길역, 대방역, 5);
        sections.addSection(section);

        //When
        Section section2 = new Section(line, 영등포역, 신길역, 5);
        sections.addSection(section2);

        //Then
        List<Station> expected = sections.getStations();
        assertAll(
                () -> assertThat(expected.size()).isEqualTo(3),
                () -> assertThat(expected).containsExactlyElementsOf(Arrays.asList(영등포역, 신길역, 대방역)),
                () -> assertThat(sections.countTotalDistance()).isEqualTo(section.getDistance() + section2.getDistance())
        );
    }

    @DisplayName("새로운 구간을 추가한다. 새로운 역을 하행 종점으로 등록할 경우" +
            "기존 :  신길역 ---> 대방역 " +
            "신규 :  신길역 ---> 대방역 ---> 노량진역")
    @Test
    public void addSection_case2() {
        //Given
        Sections sections = new Sections();
        Section section = new Section(line, 신길역, 대방역, 5);
        sections.addSection(section);

        //When
        Section section2 = new Section(line, 대방역, 노량진역, 12);
        sections.addSection(section2);

        //Then
        assertAll(
                () -> assertThat(sections.getSections().size()).isEqualTo(2),
                () -> assertThat(sections.getStations()).containsExactlyElementsOf(Arrays.asList(신길역, 대방역, 노량진역)),
                () -> assertThat(sections.countTotalDistance()).isEqualTo(section.getDistance() + section2.getDistance())
        );
    }

    @DisplayName("역 사이에 새로운 역을 등록한다. 새로운 상행역은 기존 상행역과 동일하다." +
            "기존 : 영등포역 ---> 대방역  " +
            "신규 : 영등포역 ---> 신길역 ---> 대방역")
    @Test
    public void addSection_case3(){
        //Given
        int section1Distance = 10;
        Sections newSections = new Sections();
        newSections.addSection(new Section(line, 영등포역, 대방역, section1Distance));

        //When
        int section2Distance = 5;
        newSections.addSection(new Section(line, 영등포역, 신길역, section2Distance));

        List<Station> stations =newSections.getStations();

        //Then
        assertAll(
                () -> assertThat(stations.size()).isEqualTo(3),
                () -> assertThat(stations).containsExactlyElementsOf(Arrays.asList(영등포역, 신길역, 대방역)),
                () -> assertThat(newSections.countTotalDistance()).isEqualTo(section1Distance)
        );
    }

    @DisplayName("역 사이에 새로운 역을 등록한다. 새로운 하행역은 기존 하행역과 동일하다." +
            "기존 :             신길역 ---> 노량진역" +
            "신규 :   신길역 ---> 대방역 ---> 노량진역")
    @Test
    public void addSection_case4(){
        //Given
        int section1Distance = 10;
        Sections newSections = new Sections();
        newSections.addSection(new Section(line, 신길역, 노량진역, section1Distance));

        //When
        int section2Distance = 5;
        newSections.addSection(new Section(line, 대방역, 노량진역, section2Distance));

        //Then
        List<Station> stations = newSections.getStations();
        assertAll(
                () -> assertThat(stations.size()).isEqualTo(3),
                () -> assertThat(stations).containsExactlyElementsOf(Arrays.asList(신길역, 대방역, 노량진역)),
                () -> assertThat(newSections.countTotalDistance()).isEqualTo(section1Distance)
        );
    }

    @DisplayName("역 사이에 새로운 역을 등록한다. 새로 등록되는 구간이 기존 등록된 구간보다 크거나 같을 경우 예외처리 ")
    @ParameterizedTest
    @CsvSource(value = {"5:5","5:6","5:8"}, delimiter = ':')
    public void addSection_case3_exception(int distance1, int distance2){
        assertThatThrownBy(() -> {

            Sections newSections = new Sections();
            newSections.addSection(new Section(line, 영등포역, 대방역, distance1));

            Section section2 = new Section(line, 영등포역, 신길역, distance2);
            newSections.addSection(section2);

        }).isInstanceOf(InvalidDistanceException.class);

    }

    @DisplayName("상행역과 하행역 둘 중하나도 포함되어있지 않을 경우 예외처리")
    @Test
    public void addSectionNotExistedStations(){
        assertThatThrownBy(() -> {

            Sections newSections = new Sections();
            newSections.addSection(new Section(line, 영등포역, 대방역, 10));
            Section section2 = new Section(line, 영등포역, 신길역, 3);
            newSections.addSection(section2);

            Section section3 = new Section(line, 노량진역, new Station("용산역"), 5);
            newSections.addSection(section3);

        }).isInstanceOf(NotExistedStation.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있을 경우 예외처리")
    @Test
    public void alreadyExistStation(){
        assertThatThrownBy(() -> {

            Sections newSections = new Sections();
            newSections.addSection(new Section(line, 영등포역, 대방역, 10));
            Section section2 = new Section(line, 영등포역, 대방역, 3);
            newSections.addSection(section2);

        }).isInstanceOf(AlreadyExistStation.class);
    }

}
