package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Line line;
    private Sections sections;
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    private final int DEFAULT_DISTANCE = 10;

    @BeforeEach
    void setUp() {
        line = new Line("1호선", "blue");
        sections = line.getSections();
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
        sections.add(AtoB);
        Section BtoC = Section.builder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(DEFAULT_DISTANCE)
                .build();
        sections.add(BtoC);
    }

    @Nested
    @DisplayName("추가하려는 구간의 포함관계 확인")
    class IncludeSection {

        @Test
        @DisplayName("노선에 구간과 하행역은 같고 상행역은 다른 역이 있다. ")
        void isBetweenDown() {
            //given 하행역은 같고 상행역은 다른 구간을 생성한다.
            Section EtoC = Section.builder(line)
                    .setUpStation(stationE)
                    .setDownStation(stationC)
                    .setDistance(DEFAULT_DISTANCE / 2)
                    .build();

            //then isBetweenDown이 true를 반환
            assertThat(sections.isBetweenDown(EtoC)).isTrue();
        }

        @Test
        @DisplayName("노선에 구간과 하행역은 다르고 상행역은 같은 역이 있다.")
        void isBetweenUp() {
            //given 하행역은 같고 상행역은 다른 구간을 생성한다.
            Section BtoE = Section.builder(line)
                    .setUpStation(stationB)
                    .setDownStation(stationE)
                    .setDistance(DEFAULT_DISTANCE / 2)
                    .build();

            //then isBetweenUp이 true를 반환
            assertThat(sections.isBetweenUp(BtoE)).isTrue();
        }


        @Test
        @DisplayName("노선에 구간과 하행역, 상행역이 겹치는 역이 없다. ")
        void isBetween() {
            //given 하행역은 같고 상행역은 다른 구간을 생성한다.
            Section EtoD = Section.builder(line)
                    .setUpStation(stationE)
                    .setDownStation(stationD)
                    .setDistance(DEFAULT_DISTANCE / 2)
                    .build();

            //then isBetweenDown, isBetweenUp이 false를 반환
            assertAll(
                    () -> assertThat(sections.isBetweenDown(EtoD)).isFalse(),
                    () -> assertThat(sections.isBetweenUp(EtoD)).isFalse()
            );
        }
    }

    @Nested
    @DisplayName("역 위치 확인")
    class stationLocation {

        @Test
        @DisplayName("상행 종점이다")
        void isFirst() {
            //then 첫번째 역 확인
            assertAll(
                    () -> assertThat(sections.equalFirstStation(stationA)).isTrue(),
                    () -> assertThat(sections.equalFirstStation(stationB)).isFalse()
            );
        }

        @Test
        @DisplayName("하행 종점이다")
        void isLast() {
            //then 마지막 역 확인
            assertAll(
                    () -> assertThat(sections.equalLastStation(stationC)).isTrue(),
                    () -> assertThat(sections.equalLastStation(stationB)).isFalse()
            );
        }

        @Test
        @DisplayName("노선에 포함되는 역이다.")
        void contains() {
            //then 포함되는 역 확인
            assertAll(
                    () -> assertThat(sections.contains(stationA)).isTrue(),
                    () -> assertThat(sections.contains(stationB)).isTrue(),
                    () -> assertThat(sections.contains(stationD)).isFalse()
            );
        }
    }

    @Nested
    @DisplayName("추가할 수 있는 구간 확인")
    class AddSection {
        @Test
        @DisplayName("상행 종점으로 추가할 수 있는 구간이다.")
        void canAddFirstSection() {
            //when 상행 종점으로 추가하려는 구간을 생성한다.
            Section EtoA = Section.builder(line)
                    .setUpStation(stationE)
                    .setDownStation(stationA)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();
            //then 상행 종점으로 추가할 수.있는 역이다,
            assertThat(sections.canAddFirstSection(EtoA)).isTrue();
        }

        @Test
        @DisplayName("하행 종점으로 추가할 수 있는 구간이다.")
        void canAddLastSection() {
            //when 하행 종점으로 추가하려는 구간을 생성한다.
            Section CtoD = Section.builder(line)
                    .setUpStation(stationC)
                    .setDownStation(stationD)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();
            //then 하행 종점으로 추가할 수.있는 역이다,
            assertThat(sections.canAddLastSection(CtoD)).isTrue();
        }

        @Test
        @DisplayName("상행 종점으로 추가할 수 없는 구간이다.")
        void canNotAddFirstSection() {
            //when 상행 종점으로 추가하려는 구간을 생성한다.
            Section AtoC = Section.builder(line)
                    .setUpStation(stationA)
                    .setDownStation(stationC)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();
            Section DtoE = Section.builder(line)
                    .setUpStation(stationD)
                    .setDownStation(stationE)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();

            //then 상행 종점으로 추가할 수.없는 역이다,
            assertAll(
                    () -> assertThat(sections.canAddFirstSection(AtoC)).isFalse(),
                    () -> assertThat(sections.canAddFirstSection(DtoE)).isFalse()

            );
        }

        @Test
        @DisplayName("하행 종점으로 추가할 수 없는 구간이다.")
        void canNotAddLastSection() {
            //when 하행 종점으로 추가하려는 구간을 생성한다.
            Section CtoA = Section.builder(line)
                    .setUpStation(stationC)
                    .setDownStation(stationA)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();
            Section DtoE = Section.builder(line)
                    .setUpStation(stationD)
                    .setDownStation(stationE)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();

            //then 하행 종점으로 추가할 수.없는 역이다,
            assertAll(
                    () -> assertThat(sections.canAddLastSection(CtoA)).isFalse(),
                    () -> assertThat(sections.canAddLastSection(DtoE)).isFalse()

            );
        }
    }

    @Nested
    @DisplayName("구간 추가")
    class Add {
        @Test
        @DisplayName("종점 구간을 추가한다.")
        void addTerminal() {
            //given 종점 구간을 생성한다.
            Section section = Section.builder(line)
                    .setUpStation(stationC)
                    .setDownStation(stationD)
                    .setDistance(DEFAULT_DISTANCE)
                    .build();
            //when 종점 구간을 추가한다.
            sections.add(section);
            //then 추가한 역이 종점이 된다.
            assertThat(sections.equalLastStation(stationD)).isTrue();
        }

        @Test
        @DisplayName("상행역이 같은 구간을 추가한다.")
        void addBetweenUp() {
            //given 구간을 생성한다.
            Section section = Section.builder(line)
                    .setUpStation(stationB)
                    .setDownStation(stationE)
                    .setDistance(DEFAULT_DISTANCE / 2)
                    .build();
            //when 구간을 추가한다.
            sections.addBetweenUp(section);
            //then 추가한 구간이 두번째로 조회된다. 하행 구간의 길이가 줄어든다
            assertAll(
                    () -> assertThat(sections.getValuesOrderBy().get(1)).isEqualTo(section),
                    () -> assertThat(sections.getValuesOrderBy().get(2).getDistance())
                            .isEqualTo(DEFAULT_DISTANCE / 2)
            );
        }

        @Test
        @DisplayName("하행역이 같은 구간을 추가한다.")
        void addBetweenDown() {
            //given 구간을 생성한다.
            Section section = Section.builder(line)
                    .setUpStation(stationD)
                    .setDownStation(stationB)
                    .setDistance(DEFAULT_DISTANCE / 2)
                    .build();
            //when 구간을 추가한다.
            sections.addBetweenDown(section);
            //then 추가한 구간이 두번째로 조회된다. 상행구간의 길이가 줄어든다.
            assertAll(
                    () -> assertThat(sections.getValuesOrderBy().get(1)).isEqualTo(section),
                    () -> assertThat(sections.getValuesOrderBy().get(0).getDistance())
                            .isEqualTo(DEFAULT_DISTANCE / 2)
            );
        }

        @Nested
        @DisplayName("역 삭제")
        class Remove {
            @Test
            @DisplayName("상행 종점 역을 삭제한다.")
            void removeFirstStation() {
                //when 상행 종점 역을 삭제한다.
                sections.removeFirstStation();
                //then 역이 조회되지 않는다.
                assertThat(sections.contains(stationA)).isFalse();
            }

            @Test
            @DisplayName("하행 종점 역을 삭제한다.")
            void removeLastStation() {
                //when 상행 종점 역을 삭제한다.
                sections.removeLastStation();
                //then 역이 조회되지 않는다.
                assertThat(sections.contains(stationC)).isFalse();
            }

            @Test
            @DisplayName("사이 역을 삭제한다.")
            void removeBetween() {
                //when 사이 역을 삭제한다.
                sections.removeBetween(stationB);
                //then 역이 조회되지 않는다. 남은 구간은 합쳐진다.
                assertAll(
                        () -> assertThat(sections.contains(stationB)).isFalse(),
                        () -> assertThat(sections.getValuesOrderBy().get(0).getDistance())
                                .isEqualTo(DEFAULT_DISTANCE * 2)
                );
            }
        }
    }

}
