package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.AlreadyRegisteredStationException;
import nextstep.subway.section.exception.DistanceNotLongerThanExistingSectionException;
import nextstep.subway.section.exception.InvalidSectionRegistrationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("노선 단위 테스트")
class LineTest {
    private Station 노선_상행_역;
    private Station 노선_하행_역;
    private Line 노선;

    @BeforeEach
    void setUp() {
        노선_상행_역 = new Station(1L, "노선_상행_역");
        노선_하행_역 = new Station(2L, "노선_하행_역");
        노선 = new Line("노선", "bg-red-600", new Section(노선_상행_역, 노선_하행_역, 10));
    }

    @DisplayName("구간 추가 기능 테스트")
    @Nested
    class RegisterSection {
        @DisplayName("성공 케이스")
        @Nested
        class Success {
            @DisplayName("역 사이에 새로운 구간 추가")
            @Test
            void addSectionBetweenStations() {
                // given
                Station 가운데_추가될_역 = new Station(3L, "가운데_추가될_역");
                Section newSection = new Section(노선_상행_역, 가운데_추가될_역, 4);

                // when
                노선.registerSection(newSection);

                // then : 노선_상행_역-노선_하행_역 & 노선_하행_역-양재시민의숲역 구간이 있어야함 각각 길이는 4, 6
                List<Section> sections = 노선.getSections();
                assertThat(sections).hasSize(2);

                Section firstSection = sections.get(0);
                assertSection(firstSection, "노선_상행_역", "가운데_추가될_역", 4);

                Section secondSection = sections.get(1);
                assertSection(secondSection, "가운데_추가될_역", "노선_하행_역", 6);
            }

            private void assertSection(Section target, String upStationName, String downStationName, int distance) {
                assertThat(target.getUpStationName()).isEqualTo(upStationName);
                assertThat(target.getDownStationName()).isEqualTo(downStationName);
                assertThat(target.getDistance()).isEqualTo(distance);
            }

            @DisplayName("상행역에 새로운 구간 추가")
            @Test
            void addSectionUpStation() {
                // given
                Station 상행종점에_추가될_역 = new Station(3L, "상행종점에_추가될_역");
                Section newSection = new Section(상행종점에_추가될_역, 노선_상행_역, 5);

                // when
                노선.registerSection(newSection);

                // then
                Long 상행_종점 = 노선.getFirstStationId();
                assertThat(상행_종점).isEqualTo(상행종점에_추가될_역.getId());

                List<Section> sections = 노선.getSections();
                assertThat(sections).hasSize(2);

                Section firstSection = sections.get(0);
                assertSection(firstSection, "상행종점에_추가될_역", "노선_상행_역", 5);

                Section secondSection = sections.get(1);
                assertSection(secondSection, "노선_상행_역", "노선_하행_역", 10);
            }

            @DisplayName("하행역에 새로운 구간 추가")
            @Test
            void addSectionDownStation() {
                // given
                Station 하행종점에_추가될_역 = new Station(3L, "하행종점에_추가될_역");
                Section newSection = new Section(노선_하행_역, 하행종점에_추가될_역, 5);

                // when
                노선.registerSection(newSection);

                // then
                Long 하행_종점 = 노선.getLastStationId();
                assertThat(하행_종점).isEqualTo(하행종점에_추가될_역.getId());

                List<Section> sections = 노선.getSections();
                assertThat(sections).hasSize(2);

                Section firstSection = sections.get(0);
                assertSection(firstSection, "노선_상행_역", "노선_하행_역", 10);

                Section secondSection = sections.get(1);
                assertSection(secondSection, "노선_하행_역", "하행종점에_추가될_역", 5);
            }
        
        }

        @DisplayName("실패 케이스")
        @Nested
        class Fail {
            @DisplayName("새로운 구간 내에 기존 구간들의 역들 중에 하나라도 포함되어있지 않으면 예외 발생")
            @Test
            void registerSectionFailByDoNotHaveAtLeastOneSameStation() {
                // given
                Station 여의도역 = new Station(3L, "여의도역");
                Station 샛강역 = new Station(4L, "샛강역");
                Section newSection = new Section(여의도역, 샛강역, 12);

                // when, then
                assertThatThrownBy(() -> 노선.registerSection(newSection))
                        .isInstanceOf(InvalidSectionRegistrationException.class);
            }

            @DisplayName("이미 등록된 구간을 추가하면 예외 발생")
            @Test
            void registerSectionFailByAlreadyRegisteredSection() {
                // given
                Section sectionForLine = new Section(노선_상행_역, 노선_하행_역, 10);

                // when, then
                assertThatThrownBy(() -> 노선.registerSection(sectionForLine))
                        .isInstanceOf(AlreadyRegisteredStationException.class);
            }

            @DisplayName("역 사이에 새로운 구간을 추가할 때 거리가 같거나 긴 경우 예외 발생")
            @ParameterizedTest(name = "입력한 distance = {0}")
            @ValueSource(ints = {10, 15})
            void registerSectionFailByDistance(int newSectionDistance) {
                // given
                Station 가운데_추가될_역 = new Station(3L, "가운데_추가될_역");
                Section newSection = new Section(노선_상행_역, 가운데_추가될_역, newSectionDistance);

                // when, then
                assertThatThrownBy(() -> 노선.registerSection(newSection))
                        .isInstanceOf(DistanceNotLongerThanExistingSectionException.class);
            }
        }
    }

    @DisplayName("Section을 순서대로 반환한다.")
    @Test
    void getSections() {
        // given
        Station 상행종점에_추가될_역 = new Station(3L, "상행종점에_추가될_역");
        Section newSection = new Section(상행종점에_추가될_역, 노선_상행_역, 5);
        노선.registerSection(newSection);

        // when
        List<Section> sections = 노선.getSections();

        // then
        assertThat(sections.get(0).getUpStation()).isEqualTo(상행종점에_추가될_역);
        assertThat(sections.get(1).getUpStation()).isEqualTo(노선_상행_역);
    }

    @DisplayName("구간 삭제 기능 테스트")
    @Nested
    class RemoveSection {
        @DisplayName("성공 케이스")
        @Nested
        class Success {
            @DisplayName("상행 종점 역 제거")
            @Test
            void removeFirstStation() {
                // given : 역1 -(거리)- 역2 ...
                // 상행종점에_추가될_역 -(5)- 노선_상행_역 -(10)- 노선_하행_역
                Station 상행종점에_추가될_역 = new Station(3L, "상행종점에_추가될_역");
                Section newSection = new Section(상행종점에_추가될_역, 노선_상행_역, 5);
                노선.registerSection(newSection);

                // when
                노선.removeSection(상행종점에_추가될_역);

                // then
                List<Section> sections = 노선.getSections();
                assertThat(sections).hasSize(1);

                Section remainSection = sections.get(0);
                assertThat(remainSection.getUpStationName()).isEqualTo("노선_상행_역");
                assertThat(remainSection.getDownStationName()).isEqualTo("노선_하행_역");
                assertThat(remainSection.getDistance()).isEqualTo(10);
            }

            @DisplayName("하행 종점 역 제거")
            @Test
            void removeLastStation() {
                // given : 역1 -(거리)- 역2 ...
                // 상행종점에_추가될_역 -(5)- 노선_상행_역 -(10)- 노선_하행_역
                Station 상행종점에_추가될_역 = new Station(3L, "상행종점에_추가될_역");
                Section newSection = new Section(상행종점에_추가될_역, 노선_상행_역, 5);
                노선.registerSection(newSection);

                // when
                노선.removeSection(노선_하행_역);

                // then
                List<Section> sections = 노선.getSections();
                assertThat(sections).hasSize(1);

                Section remainSection = sections.get(0);
                assertThat(remainSection.getUpStationName()).isEqualTo("상행_종점에_추가될_역");
                assertThat(remainSection.getDownStationName()).isEqualTo("노선_상행_역");
                assertThat(remainSection.getDistance()).isEqualTo(5);
            }

            @DisplayName("가운데 역 제거")
            @Test
            void removeMidStation() {
                // given : 역1 -(거리)- 역2 ...
                // 상행종점에_추가될_역 -(5)- 노선_상행_역 -(10)- 노선_하행_역
                Station 상행종점에_추가될_역 = new Station(3L, "상행종점에_추가될_역");
                Section newSection = new Section(상행종점에_추가될_역, 노선_상행_역, 5);
                노선.registerSection(newSection);

                // when
                노선.removeSection(노선_상행_역);

                // then
                List<Section> sections = 노선.getSections();
                assertThat(sections).hasSize(1);

                Section remainSection = sections.get(0);
                assertThat(remainSection.getUpStationName()).isEqualTo("상행종점에_추가될_역");
                assertThat(remainSection.getDownStationName()).isEqualTo("노선_하행_역");
                assertThat(remainSection.getDistance()).isEqualTo(15);
            }
        }

        @DisplayName("실패 케이스")
        @Nested
        class Fail {

        }
    }
}
