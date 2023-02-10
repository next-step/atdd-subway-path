package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("구간 목록 관련 기능")
class SectionsTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 정자역;
    private Line line;

    @BeforeEach
    void setUp() {
        this.강남역 = new Station("강남역");
        this.역삼역 = new Station("역삼역");
        this.선릉역 = new Station("선릉역");
        this.정자역 = new Station("정자역");
        this.line = new Line("2호선", "bg-green-500");
    }

    @DisplayName("구간 추가 관련 기능")
    @Nested
    class AddSectionTest {
        @DisplayName("구간 목록에 구간을 추가한다.")
        @Test
        void add() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 역삼역, 선릉역, 5);

            sections.add(section1);
            sections.add(section2);

            assertThat(sections.getSections()).contains(section1, section2);
        }

        @DisplayName("기존 구간 사이에 신규 구간을 추가한다")
        @Test
        void addSectionBetweenExistingSection() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 강남역, 선릉역, 5);

            sections.add(section1);
            sections.add(section2);

            assertAll(
                    () -> assertThat(sections.getStations()).containsExactly(강남역, 선릉역, 역삼역),
                    () -> assertThat(section1.getDistance()).isEqualTo(5)
            );
        }

        @DisplayName("기존 구간 사이에 신규 구간을 추가시 신규 구간이 역과 역 사이 길이보다 크거나 같으면 에러 처리한다.")
        @ParameterizedTest(name = "Add Section Distance : {0}")
        @ValueSource(ints = {5, 6, 10})
        void addSectionDistanceMoreThanExistingSectionDistance(int distance) {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 선릉역, 5);
            Section section2 = new Section(line, 강남역, 역삼역, distance);
            sections.add(section1);

            assertThatThrownBy(() -> sections.add(section2)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("노선의 상행 종점으로 신규 구간을 추가한다.")
        @Test
        void addLineSectionUpStation() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 선릉역, 강남역, 15);
            sections.add(section1);

            sections.add(section2);

            assertThat(sections.getSections()).contains(section2, section1);
        }

        @DisplayName("노선의 하행 종점으로 신규 구간을 추가한다.")
        @Test
        void addLineSectionDownStation() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 역삼역, 선릉역, 15);
            sections.add(section1);

            sections.add(section2);

            assertThat(sections.getSections()).containsExactly(section1, section2);
        }

        @DisplayName("신규 구간 추가시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 에러 처리한다.")
        @Test
        void addLineSectionAlreadyAddedInLine() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 강남역, 역삼역, 5);
            sections.add(section1);

            assertThatThrownBy(() -> sections.add(section2)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("신규 구간 추가시 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 에러 처리한다.")
        @Test
        void addLineSectionNonIncludeInLine() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 선릉역, 정자역, 5);
            sections.add(section1);

            assertThatThrownBy(() -> sections.add(section2)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("구간 조회 관련 기능")
    @Nested
    class FindSectionTest {
        @DisplayName("구간 목록 조회")
        @Test
        void getSections() {
            Sections sections = new Sections();
            Section section = new Section(line, 강남역, 역삼역, 10);
            sections.add(section);

            List<Section> sectionList = sections.getSections();

            assertThat(sectionList).containsExactly(section);
        }

        @DisplayName("비어있는 구간 목록 조회시 빈 리스트를 반환한다.")
        @Test
        void getStationsEmptySections() {
            Sections sections = new Sections();

            assertThat(sections.getSections()).isEmpty();
        }

        @DisplayName("특정 역이 포함된 구간 목록을 조회한다.")
        @Test
        void findSectionByStation() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 역삼역, 선릉역, 5);
            sections.add(section1);
            sections.add(section2);

            assertThat(sections.findByStation(역삼역)).containsExactly(section1, section2);
        }

        @DisplayName("구간 목록의 역 목록을 조회한다.")
        @Test
        void getStations() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 역삼역, 선릉역, 5);
            sections.add(section1);
            sections.add(section2);

            List<Station> stations = sections.getStations();

            assertThat(stations).containsExactly(강남역, 역삼역, 선릉역);
        }

        @DisplayName("구간 목록의 하행 종점역을 조회한다.")
        @Test
        void getDownStation() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 역삼역, 선릉역, 5);
            sections.add(section1);
            sections.add(section2);

            Station downStation = sections.getLineDownStation();

            assertThat(downStation).isEqualTo(선릉역);
        }

        @DisplayName("구간 목록의 하행 종점역을 조회한다.")
        @Test
        void getUpStation() {
            Sections sections = new Sections();
            Section section1 = new Section(line, 강남역, 역삼역, 10);
            Section section2 = new Section(line, 역삼역, 선릉역, 5);
            sections.add(section1);
            sections.add(section2);

            Station upStation = sections.getLineUpStation();

            assertThat(upStation).isEqualTo(강남역);
        }
    }

    @Nested
    class RemoveSectionTest {
        @DisplayName("구간 목록의 구간을 제거한다.")
        @Test
        void remove() {
            Sections sections = new Sections();
            Section expected = new Section(line, 강남역, 역삼역, 10);
            sections.add(expected);
            sections.add(new Section(line, 역삼역, 선릉역, 5));

            sections.remove(선릉역);

            assertThat(sections.getSections()).containsExactly(expected);
        }

        @DisplayName("상행역과 하행역만 포함된 구간 목록에 삭제를 요청할 경우 에러 처리한다.")
        @Test
        void removeFailSectionsOnlyContainUpStationAndDownStation() {
            Sections sections = new Sections();
            Section expected = new Section(line, 강남역, 역삼역, 10);
            sections.add(expected);

            assertThatThrownBy(() -> sections.remove(선릉역)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("노선 조회시 상행 종점역부터 하행 종점역 순으로 역 목록을 조회한다.")
    @Test
    void showStationsOrderByUpStationToDownStation() {
        Sections sections = new Sections();
        sections.add(new Section(line, 역삼역, 선릉역, 7));
        sections.add(new Section(line, 강남역, 역삼역, 10));
        sections.add(new Section(line, 정자역, 선릉역, 5));

        assertThat(sections.getStations()).containsExactly(강남역, 역삼역, 정자역, 선릉역);
    }
}
