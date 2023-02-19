package nextstep.subway.domain;

import nextstep.subway.domain.exception.DuplicateAddSectionException;
import nextstep.subway.domain.exception.IllegalAddSectionException;
import nextstep.subway.domain.exception.IllegalDistanceSectionException;
import nextstep.subway.domain.exception.IllegalRemoveMinSectionSize;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class SectionsTest {

    private Sections givenSections;
    private Section givenSection;
    private Station 양재역;
    private Station 강남역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        신분당선 = new Line("신분당선", "red");
        givenSection = new Section(신분당선, 강남역, 양재역, 10);
        givenSections = new Sections(Lists.newArrayList(givenSection));
    }

    @Nested
    @DisplayName("역 제거 요청")
    class RemoveSection {

        /**
         * When 마지막 역 제거 요청 시
         * Then 제거가 된다
         */
        @DisplayName("마지막역 제거 요청 시 구간이 제거가 된다")
        @Test
        void 마지막역_제거_요청_시_구간이_제거가_된다() {
            // Given
            Station 정자역 = new Station("정자역");
            givenSections.addSection(신분당선, 양재역, 정자역, 10);

            // When
            givenSections.removeSection(정자역);

            // Then
            assertThat(givenSections.getOrderedStations()).containsExactly(강남역, 양재역);
        }

        /**
         * Given 노선에 새로운 구간 추가를 요청 하고
         * When 지하철 노선의 어느 구간이든 제거를 요청 하면
         * Then 노선에 구간이 제거된다
         * Then 거리는 합산이 된다
         */
        @DisplayName("어느 구간이든 제거 할 수 있다")
        @Test
        void 지하철_노선에_어느_구간이든_제거_할_수_있다() {
            // given
            Station 정자역 = new Station("정자역");
            givenSections.addSection(신분당선, 양재역, 정자역, 10);

            // when
            givenSections.removeSection(양재역);

            // then
            assertThat(givenSections.getOrderedStations()).containsExactly(강남역, 정자역);
            Section 강남역_구간 = givenSections.findSectionByStation(강남역);
            assertThat(강남역_구간.getDistance()).isEqualTo(20);
            assertThat(강남역_구간.getUpStation()).isEqualTo(강남역);
            assertThat(강남역_구간.getDownStation()).isEqualTo(정자역);
        }

        /**
         * Given 노선에 새로운 구간 추가를 요청 하고
         * When 종점을 제거 요청 시
         * Then 다음으로 오던 역이 종점이 된다
         */
        @DisplayName("지하철 노선에 종점을 제거 요청 시 다음으로 오던 역이 종점이 된다")
        @Test
        void 지하철_노선에_종점을_제거_요청_시_다음으로_오던_역이_종점이_된다() {
            // given
            Station 정자역 = new Station("정자역");
            givenSections.addSection(신분당선, 양재역, 정자역, 10);

            // when
            givenSections.removeSection(강남역);

            // then
            assertThat(givenSections.getOrderedStations()).containsExactly(양재역, 정자역);
            Section 양재역_구간 = givenSections.findSectionByStation(양재역);
            assertThat(양재역_구간.getDistance()).isEqualTo(10);
            assertThat(양재역_구간.getUpStation()).isEqualTo(양재역);
            assertThat(양재역_구간.getDownStation()).isEqualTo(정자역);
        }

        /**
         * When 구간이 하나인 노선에서 마지막 구간을 제거할 때
         * Then 제거 할 수 없다
         */
        @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때 제거 할 수 없다")
        @Test
        void 구간이_하나인_노선에서_마지막_구간을_제거할_때_제거_할_수_없다() {
            // When && Then
            assertThatThrownBy(() -> givenSections.removeSection(강남역))
                .isInstanceOf(IllegalRemoveMinSectionSize.class)
                .hasMessage("하나의 노선에 하나의 구간은 있어야합니다");
        }

        /**
         * When 노선의 구간에 해당역이 없는데 제거 요청을하면
         * Then 제거 할 수 없다
         */
        @DisplayName("노선의 구간에 해당역이 없는데 제거 요청을하면 제거 할 수 없다")
        @Test
        void 노선의_구간에_해당역이_없는데_제거_요청을하면_제거_할_수_없다() {
            Station 정자역 = new Station("정자역");

            // When && Then
            assertThatThrownBy(() -> givenSections.removeSection(정자역))
                .isInstanceOf(IllegalRemoveMinSectionSize.class)
                .hasMessage("하나의 노선에 하나의 구간은 있어야합니다");
        }

    }

    /**
     * When & Then 구간이 없는지 확인 할 수 있다
     */
    @DisplayName("구간이 없는지 확인 할 수 있다")
    @Test
    void 구간이_없는지_확인_할_수_있다() {
        // When
        assertThat(givenSections.isSectionsEmpty()).isFalse();
    }

    /**
     * When 구간에 역들을 조회 시
     * Then 상행역 부터 조회가 된다
     */
    @DisplayName("구간에 역들을 조회 시 상행역 부터 조회가 된다")
    @Test
    void 구간에_역들을_조회_시_상행역_부터_조회가_된다() {
        // When
        List<Station> stations = givenSections.getOrderedStations();

        // Then
        assertThat(stations).containsExactly(강남역, 양재역);
    }

    /**
     * When 구간에 상행역 조회 시
     * Then 조회가 가능하다
     */
    @DisplayName("구간에 상행역 조회 요청 시 조회가 가능하다")
    @Test
    void 구간에_상행역_조회_요청_시_조회가_가능하다() {
        // When
        Section sectionByGangNam = givenSections.findSectionByStation(강남역);

        // Then
        assertThat(sectionByGangNam).isEqualTo(givenSection);
    }

    /**
     * When 구간에 하행역 조회 시
     * Then 조회가 가능하다
     */
    @DisplayName("구간에 하행역 조회 요청 시 조회가 가능하다")
    @Test
    void 구간에_하행역_조회_요청_시_조회가_가능하다() {
        // When
        Section sectionByYangJaeStation = givenSections.findSectionByStation(양재역);

        // Then
        assertThat(sectionByYangJaeStation).isEqualTo(givenSection);
    }

    @Nested
    @DisplayName("구간 추가 요청")
    class addSection {

        /**
         * Given 새로운 역을 추가 후
         * When 새로운 구간에 기존 구간의 상행 종점으로 추가 시
         * Then 새로운 구간의 하행역은 기존 구간의 상행 종점역 이여야 한다.
         */
        @DisplayName("새로운 역을 추가 후 새로운 구간에 기존 구간의 상행 종점으로 추가 시 새로운 구간의 하행역은 기존 구간의 상행 종점역 이여야 한다")
        @Test
        void 새로운_역을_추가_후_새로운_구간에_기존_구간의_상행_종점으로_추가_시_새로운_구간의_하행역은_기존_구간의_상행_종점역_이여야_한다() {
            // Given
            Station 논현역 = new Station("논현역");

            // When
            givenSections.addSection(신분당선, 논현역, 강남역, 7);
            Section sectionByNonhyeon = givenSections.findSectionByStation(논현역);

            // Then
            List<Section> sections = givenSections.getSections();
            assertThat(sections).containsExactly(givenSection, sectionByNonhyeon);
        }

        /**
         * Given 새로운 역을 추가 후
         * When 기존 구간 중간에 새로운 구간 추가 시
         * Then 추가가 된다
         * Then 거리가 재조정이 된다
         */
        @DisplayName("기존 구간 중간에 새로운 구간을 추가 시 구간이 추가가 된다")
        @Test
        void 기존_구간_중간에_새로운_구간을_추가_시_구간이_추가가_된다() {
            // Given
            Station 새로운역 = new Station("새로운역");

            // When
            givenSections.addSection(신분당선, 강남역, 새로운역, 4);

            // Then
            List<Section> sections = givenSections.getSections();
            assertThat(sections.get(0).getUpStation()).isEqualTo(새로운역);
            assertThat(sections.get(0).getDownStation()).isEqualTo(양재역);
            assertThat(sections.get(0).getDistance()).isEqualTo(6);
            assertThat(sections.get(1).getUpStation()).isEqualTo(강남역);
            assertThat(sections.get(1).getDownStation()).isEqualTo(새로운역);
            assertThat(sections.get(1).getDistance()).isEqualTo(4);
        }

        /**
         * Given 새로운 역을 추가 후
         * When 기존 구간 맨 뒤에 새로운 구간 추가 시
         * Then 추가가 된다
         */
        @DisplayName("기존 구간 맨 뒤에 새로운 구간을 추가 시 구간이 추가가 된다")
        @Test
        void 기존_구간_맨_뒤에_새로운_구간을_추가_시_구간이_추가가_된다() {
            // Given
            Station 판교역 = new Station("판교역");

            // When
            givenSections.addSection(신분당선, 양재역, 판교역, 4);

            // Then
            List<Section> sections = givenSections.getSections();
            Section sectionByPangyo = givenSections.findSectionByStation(판교역);

            // Then
            assertThat(sections).containsExactly(givenSection, sectionByPangyo);
        }

        /**
         * When 기존 구간과 동일한 구간 추가 요청시
         * Then 추가가 안된다
         */
        @DisplayName("기존 구간과 동일한 구간 추가 요청시 추가가 안된다")
        @Test
        void 기존_구간과_동일한_구간_추가_요청시_추가가_안된다() {
            // When && Then
            assertThatThrownBy(() -> givenSections.addSection(신분당선, 강남역, 양재역, 4))
                .isInstanceOf(DuplicateAddSectionException.class)
                .hasMessage("이미 추가되어있는 구간 요청입니다.");
        }

        /**
         * When 기존 구간에 기존 거리보다 새로운 구간의 거리가 같거나 더 클 시
         * Then 추가가 안된다
         */
        @DisplayName("기존 구간에 기존 거리보다 새로운 구간의 거리가 같거나 더 클 시 추가가 안된다")
        @Test
        void 기존_구간에_기존_거리보다_새로운_구간의_거리가_더_클_시_추가가_안된다() {
            // Given
            Station 판교역 = new Station("판교역");

            // When && Then
            assertThatThrownBy(() -> givenSections.addSection(신분당선, 강남역, 판교역, 10))
                .isInstanceOf(IllegalDistanceSectionException.class)
                .hasMessage("잘못된 구간 사이 거리 요청입니다.");
        }

        /**
         * When 기존 구간에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
         * Then 추가가 안된다
         */
        @DisplayName("기존 구간에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가가 안된다")
        @Test
        void 기존_구간에_상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가가_안된다() {
            // Given
            Station 판교역 = new Station("판교역");
            Station 수지구청역 = new Station("수지구청역");

            // When && Then
            assertThatThrownBy(() -> givenSections.addSection(신분당선, 수지구청역, 판교역, 10))
                .isInstanceOf(IllegalAddSectionException.class)
                .hasMessage("역을 추가 할 수 없습니다");
        }

        /**
         * When 기존 구간들에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간 요청 시
         * Then 추가가 안된다
         */
        @DisplayName("기존 구간들에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간 요청 시 추가가 안된다")
        @Test
        void 기존_구간들에_상행역과_하행역_둘_중_하나도_포함되어있지_않은_구간_요청_시_추가가_안된다() {
            // Given
            Station 수지구청역 = new Station("수지구청역");
            Station 판교역 = new Station("판교역");

            // When && Then
            assertThatThrownBy(() -> givenSections.addSection(신분당선, 수지구청역, 판교역, 10))
                .isInstanceOf(IllegalAddSectionException.class)
                .hasMessage("역을 추가 할 수 없습니다");
        }

    }

}