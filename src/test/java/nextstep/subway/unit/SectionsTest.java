package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionsTest {
    Station 기흥역;
    Station 신갈역;
    Station 정자역;
    Line line;
    Sections sections;

    @BeforeEach
    void setUp() {
        기흥역 = new Station(11L, "기흥역");
        신갈역 = new Station(12L, "신갈역");
        정자역 = new Station(13L, "정자역");
        line = new Line(21L, "분당선", "yellow");
        sections = line.getSections();
        sections.add(new Section(line, 기흥역, 신갈역, 10));
        sections.add(new Section(line, 신갈역, 정자역, 9));
    }

    @DisplayName("구간등록")
    @Nested
    class Add {
        @Test
        void 지하철구간_사이에_새로운구간_추가() {
            // given
            Station 구성역 = new Station(14L, "구성역");

            // when
            sections.add(new Section(line, 구성역, 정자역, 3));

            // then
            assertAll(
                () -> assertThat(sections.size()).isEqualTo(3),
                () -> assertThat(sections.getStationNames()).containsExactly("기흥역", "신갈역", "구성역", "정자역"),
                () -> assertThat(getDistances()).containsExactly(10, 6, 3)
                     );
        }

        @Test
        void 새로운구간을_기존지하철_노선의_상행종점으로_추가요청() {
            // given
            Station 구성역 = new Station(14L, "구성역");

            // when
            sections.add(new Section(line, 구성역, 기흥역, 3));

            // then
            assertAll(
                () -> assertThat(line.getSections().size()).isEqualTo(3),
                () -> assertThat(line.getSections().getStationNames()).containsExactly("구성역", "기흥역", "신갈역", "정자역"),
                () -> assertThat(getDistances()).containsExactly(3, 10, 9)
                     );
        }

        @Test
        void 새로운구간을_기존지하철_노선의_하행종점으로_추가요청() {
            // given
            Station 구성역 = new Station(14L, "구성역");

            // when
            sections.add(new Section(line, 정자역, 구성역, 3));

            // then
            assertAll(
                () -> assertThat(line.getSections().size()).isEqualTo(3),
                () -> assertThat(line.getSections().getStationNames()).containsExactly("기흥역", "신갈역", "정자역", "구성역"),
                () -> assertThat(getDistances()).containsExactly(10, 9, 3)
                     );
        }


        @ParameterizedTest
        @ValueSource(ints = {9, 10, 11})
        void 역사이에_새로운역을_등록할경우_기존역사이_길이보다_크거나같으면_등록못함(int distance) {
            // given
            Station 구성역 = new Station(14L, "구성역");

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                sections.add(new Section(line, 구성역, 정자역, distance));
            });

            // then
            assertThat(exception.getResponseCode()).isEqualTo(CommonCode.PARAM_INVALID);
        }

        @Test
        void 상행역과_하행역_이미_노선에_모두_등록되어있다면_추가못함() {
            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                sections.add(new Section(line, 기흥역, 정자역, 10));
            });

            // then
            assertThat(exception.getResponseCode()).isEqualTo(CommonCode.PARAM_INVALID);
        }

        @Test
        void 상행역과_하행역_둘중_하나도_포함되어있지_않으면_추가못함() {
            Station 수원역 = new Station(14L, "수원역");
            Station 오리역 = new Station(14L, "오리역");

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                sections.add(new Section(line, 수원역, 오리역, 10));
            });

            // then
            assertThat(exception.getResponseCode()).isEqualTo(CommonCode.PARAM_INVALID);
        }
    }

    @DisplayName("정렬된 지하철역 리스트 조회")
    @Test
    void getStationsSorted() {
        // given
        Station 구성역 = new Station(14L, "구성역");
        line.addSection(구성역, 기흥역, 3);

        // when
        List<Station> stations = sections.getStationsSorted();

        // then
        assertAll(
            () -> assertThat(stations).containsExactlyInAnyOrder(구성역, 기흥역, 신갈역, 정자역),
            () -> assertThat(stations).hasSize(4)
                 );
    }

    @DisplayName("정렬된 구간 리스트 조회")
    @Test
    void getSectionsSorted() {
        // given
        Station 구성역 = new Station(14L, "구성역");
        line.addSection(구성역, 기흥역, 3);

        // when

        List<Section> sortedSection = sections.getSectionsSorted();

        // then
        assertAll(
            () -> assertThat(sortedSection).hasSize(3),
            () -> assertThat(sortedSection.get(0).getUpStation().getName()).isEqualTo("구성역"),
            () -> assertThat(sortedSection.get(2).getDownStation().getName()).isEqualTo("정자역")
                 );
    }

    @DisplayName("구간 제거")
    @Test
    void removeSection() {
        // when
        sections.removeSection(정자역.getId());

        // then
        assertThat(line.getSections().getStationNames()).doesNotContain("정자역");
    }

    @DisplayName("상행 종점역 조회")
    @Test
    void getUpEndStation() {
        // when
        Station station = sections.getUpEndStation();

        // then
        assertThat(station.getName()).isEqualTo("기흥역");
    }

    @DisplayName("하행 종점역 조회")
    @Test
    void getDownEndStation() {
        // when
        Station station = sections.getDownEndStation();

        // then
        assertThat(station.getName()).isEqualTo("정자역");
    }

    private List<Integer> getDistances() {
        return sections
            .getSectionsSorted()
            .stream()
            .map(Section::getDistance)
            .collect(Collectors.toList());
    }
}
