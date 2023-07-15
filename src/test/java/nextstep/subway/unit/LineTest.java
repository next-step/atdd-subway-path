package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line line;
    Station station1 = new Station(1L, "station1");
    Station station2 = new Station(2L, "station2");
    Station station3 = new Station(3L, "station3");
    Station station4 = new Station(4L, "station4");

    @BeforeEach
    void init() {
        line = new Line();
    }

    @Nested
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    @DisplayName("구간 추가 테스트")
    public class AddSectionTest {

        @Test
        @DisplayName("1. 구간_1개")
        void addSection_first() {

            // given
            int distance_1_2 = 10;
            
            // when
            line.addSection(station1, station2, distance_1_2);

            // then
            List<Section> sections = line.getSections();
            구간_길이_검증(sections, 1);
            구간검증(sections, station1, station2, distance_1_2);
        }

        /**
         * 노선 [station1]-[station2] -> 노선 [station1]-[station2]-[station3]
         */
        @Test
        @DisplayName("2. 구간_2개_맨뒤")
        void addSection_second_atTheEnd() {

            // given
            int distance_1_2 = 10;
            int distance_2_3 = 6;
            line.addSection(station1, station2, distance_1_2);

            // when
            line.addSection(station2, station3, distance_2_3);

            // then
            List<Section> sections = line.getSections();
            구간_길이_검증(sections, 2);
            구간검증(sections, station1, station2, distance_1_2);
            구간검증(sections, station2, station3, distance_2_3);
        }

        /**
         * 노선 [station1]-[station2] -> 노선 [station1]-[station3]-[station2]
         */
        @Test
        @DisplayName("3. 구간_구간2개_중간 (상행역기준)")
        void addSection_second_atTheMiddle_basedOnUpStation() {
            
            // given
            int beforeDistance_1_2 = 10;
            line.addSection(station1, station2, beforeDistance_1_2);

            int afterDistance_1_3 = 6;
            int afterDistance_3_2 = beforeDistance_1_2 - afterDistance_1_3;
            

            // when
            line.addSection(station1, station3, afterDistance_1_3);

            // then
            List<Section> sections = line.getSections();
            구간_길이_검증(sections, 2);
            구간검증(sections, station1, station3, afterDistance_1_3);
            구간검증(sections, station3, station2, afterDistance_3_2);
        }

        /**
         * 노선 [station1]-[station2] -> 노선 [station1]-[station3]-[station2]
         */
        @Test
        @DisplayName("4. 구간_구간2개_중간 (하행역기준)")
        void addSection_second_atTheMiddle_basedOnDownStation() {
            
            // given
            int beforeDistance_1_2 = 10;
            line.addSection(station1, station2, beforeDistance_1_2);

            int afterDistance_3_2 = 4;
            int afterDistance_1_3 = beforeDistance_1_2 - afterDistance_3_2;
            

            // when
            line.addSection(station3, station2, afterDistance_3_2);

            // then
            List<Section> sections = line.getSections();
            구간_길이_검증(sections, 2);
            구간검증(sections, station1, station3, afterDistance_1_3);
            구간검증(sections, station3, station2, afterDistance_3_2);
        }

        /**
         * 노선 [station1]-[station2] -> 노선 [station3]-[station1]-[station2]
         */
        @Test
        @DisplayName("5. 구간_구간2개_맨앞")
        void addSection_second_atTheStart() {
            
            // given
            int distance_1_2 = 10;
            line.addSection(station1, station2, distance_1_2);

            int distance_3_1 = 4;

            // when
            line.addSection(station3, station1, distance_3_1);

            // then
            List<Section> sections = line.getSections();
            구간_길이_검증(sections, 2);
            구간검증(sections, station3, station1, distance_3_1);
            구간검증(sections, station1, station2, distance_1_2);
        }

        /**
         * 노선 [station1]-[station2] -> 노선 [station1]-[station3]-[station2]
         */
        @Test
        @DisplayName("6. 구간_오류: 추가하려는 구간의 길이가 너무 긴 경우 (상행선 기준)")
        void addSection_error_distanceTooLong_basedOnUpStation() {
            
            // given
            int distance_1_2 = 10;
            int distance_1_3 = 11;
            line.addSection(station1, station2, distance_1_2);
            
            // when & then
            Assertions.assertThatThrownBy(() -> line.addSection(station1, station3, distance_1_3))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        /**
         * 노선 [station1]-[station2] -> 노선 [station1]-[station3]-[station2]
         */
        @Test
        @DisplayName("7. 구간_오류: 추가하려는 구간의 길이가 너무 긴 경우 (하행선 기준)")
        void addSection_error_distanceTooLong_basedOnDownStation() {

            // given
            int distance_1_2 = 10;
            int distance_1_3 = 10;
            line.addSection(station1, station2, distance_1_2);

            // when & then
            Assertions.assertThatThrownBy(() -> line.addSection(station3, station2, distance_1_3))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        @DisplayName("8. 구간_오류: 추가하려는 구간의 상/하행선 모두 노선에 없는 경우")
        void addSection_error_notExistStationInLine() {
            
            // given
            int distance_1_2 = 10;
            line.addSection(station1, station2, distance_1_2);
            
            // when & then
            Assertions.assertThatThrownBy(() -> line.addSection(station3, station4, 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        @DisplayName("9. 구간_오류: 추가하려는 구간의 상/하행선 모두 노선에 있는 경우")
        void addSection_error_allExistStationInLine() {
            
            // given
            int distance_1_2 = 10;
            line.addSection(station1, station2, distance_1_2);
            
            // when & then
            Assertions.assertThatThrownBy(() -> line.addSection(station1, station2, 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private static void 구간_길이_검증(List<Section> sections, int size) {
        assertThat(sections).hasSize(size);
    }

    @DisplayName("노선의 정거장이 순서대로 정렬되는지 확인한다.")
    @RepeatedTest(6)
    void getStations() {

        // given
        line.addSection(station1, station2, 1);
        line.addSection(station2, station3, 1);
        line.addSection(station3, station4, 1);

        Collections.shuffle(line.getSections()); // section의 순서를 섞는다.

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(station1, station2, station3, station4);
    }

    @DisplayName("노선이 하나도 없는 경우를 확인한다.")
    @Test
    void getStations_noStation() {

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).hasSize(0);
    }

    @Test
    void removeSection() {
    }


    @Test
    @DisplayName("구간 조회 테스트")
    void getSection() {

        // given
        int distance_1_2 = 10;
        line.addSection(station1, station2, distance_1_2);
        line.addSection(station2, station3, 20);

        // when
        Section section = line.getSection(station1.getId(), station2.getId());

        // then
        assertThat(section.getLine()).isEqualTo(line);
        구간검증(List.of(section), station1, station2, distance_1_2);
    }
    
    private void 구간검증(List<Section> sections, Station upStation, Station downStation, int distance) {
        
        Optional<Section> targetSection = sections.stream()
                .filter(it -> it.getUpStation() == upStation && it.getDownStation() == downStation)
                .findFirst();
        
        assertThat(targetSection.isPresent()).isTrue();
        
        Section section = targetSection.get();
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
        assertThat(section.getDistance()).isEqualTo(distance);
    }
}
