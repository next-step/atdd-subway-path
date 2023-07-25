package nextstep.subway.unit.line;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.addition.SectionAdditionHandlerMapping;
import nextstep.subway.line.domain.entity.deletion.SectionDeletionHandlerMapping;
import nextstep.subway.station.entity.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    final int SECTION_DEFAULT_DISTANCE = 10;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 익명역;
    Station 판교역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        판교역 = new Station(5L, "판교역");
        이호선 = new Line("이호선", "Green", SECTION_DEFAULT_DISTANCE, 강남역, 역삼역);
    }

    @Nested
    @DisplayName("구간을 등록할 때")
    class AddSection {
        @DisplayName("기존 구간 A-C에 신규 구간 C-D 추가 시 성공한다.")
        @Test
        void addSectionACCD() {
            // when
            이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

            // then
            assertThat(이호선.getStations()).contains(선릉역);
        }

        @DisplayName("기존 구간 A-C에 신규 구간 A-B 추가 시 성공한다.")
        @Test
        void addSectionACAB() {
            // when
            int smallerDistanceThanSectionAC = 이호선.getDistance() - 1;
            이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 강남역, 익명역, smallerDistanceThanSectionAC));

            // then
            assertThat(이호선.getStations()).contains(익명역);
        }

        @DisplayName("기존 구간 A-C에 신규 구간 B-C 추가 시 성공한다.")
        @Test
        void addSectionACBC() {
            // when
            int smallerDistanceThanSectionAC = 이호선.getDistance() - 1;
            이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 익명역, 역삼역, smallerDistanceThanSectionAC));

            // then
            assertThat(이호선.getStations()).contains(익명역);
        }

        @DisplayName("기존 구간 A-C에 신규 구간 B-A 추가 시 성공한다.")
        @Test
        void addSectionACBA() {
            // when
            이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 익명역, 강남역, SECTION_DEFAULT_DISTANCE));

            // then
            assertThat(이호선.getStations()).contains(익명역);
        }
    }

    @Nested
    @DisplayName("구간 등록 실패는")
    class AddSectionFailed {

        @DisplayName("구간 내 역이 A-B-C인 상황에서")
        @BeforeEach
        void addAdditionalSection() {
            이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));
        }

        @DisplayName("새 구간 B-A를 추가할 때 B가 이미 존재하는 역인 경우에 발생.")
        @Test
        void stationRegistrationFailByNewTopStationAdvocateAlreadyExists() {
            // when
            Assertions.assertThatThrownBy(() -> 이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 강남역, SECTION_DEFAULT_DISTANCE)))
                    .isInstanceOf(CreationValidationException.class);
        }

        @DisplayName("기존 구간 B-C 보다 신규 구간 D-C 길이가 크거나 같은 경우에 발생.")
        @Test
        void stationRegistrationBetweenStationsFailBySameOrBiggerDistance() {
            // when
            int sameDistanceComparedToSectionAC = SECTION_DEFAULT_DISTANCE;
            Assertions.assertThatThrownBy(() -> 이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 익명역, 역삼역, sameDistanceComparedToSectionAC)))
                    .isInstanceOf(CreationValidationException.class);

            // then
            assertThat(이호선.getStations()).doesNotContain(익명역);
        }

        @DisplayName("상행역과 하행역이 이미 모두 노선에 등록돼있는 구간을 추가하는 경우에 발생.")
        @Test
        void stationRegistrationFailByAlreadyExistingTopStationAndDownStation() {
            // when
            Assertions.assertThatThrownBy(() -> 이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 강남역, 역삼역, SECTION_DEFAULT_DISTANCE)))
                    .isInstanceOf(CreationValidationException.class);
            Assertions.assertThatThrownBy(() -> 이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE)))
                    .isInstanceOf(CreationValidationException.class);
        }

        @DisplayName("상행역과 하행역이 모두 노선에 포함되있지 않은 구간을 추가하는 경우에 발생.")
        @Test
        void stationRegistrationFailByLineDoNotContainSectionRelatedStations() {
            // when
            Assertions.assertThatThrownBy(() -> 이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 익명역, 판교역, SECTION_DEFAULT_DISTANCE)))
                    .isInstanceOf(CreationValidationException.class);

            // then
            assertThat(이호선.getStations()).doesNotContain(익명역);
            assertThat(이호선.getStations()).doesNotContain(판교역);
        }
    }


    @DisplayName("모든 지하철 조회")
    @Test
    void getStations() {
        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역);
    }

    @DisplayName("모든 지하철 조회 시, 상행종착역-하행종착역 순으로 반환")
    @Test
    void checkGetStationsReturnInOrder() {
        // given
        int smallerDistanceThanSectionAC = 이호선.getDistance() - 1;
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 강남역, 익명역, smallerDistanceThanSectionAC));

        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 익명역, 역삼역);
    }

    @DisplayName("구간 삭제, 상행 종착역 노선에서 제거")
    @Test
    void removeSectionRemoveTopStationFromLine() {
        // given
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

        // when
        이호선.removeSection(new SectionDeletionHandlerMapping(), 강남역);

        // then
        assertThat(이호선.getStations()).doesNotContain(강남역);
    }

    @DisplayName("구간 삭제, 상행&하행 종착역이 아닌 역 노선에서 제거")
    @Test
    void removeSectionRemoveNotTopNotLastStationFromLine() {
        // given
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

        // when
        이호선.removeSection(new SectionDeletionHandlerMapping(), 역삼역);

        // then
        assertThat(이호선.getStations()).doesNotContain(역삼역);
    }

    @DisplayName("구간 삭제 실패, 노선 내 하나의 구간만 존재")
    @Test
    void removeSectionFailedByOnlyOneSectionExists() {
        // when
        Assertions.assertThatThrownBy(() -> 이호선.removeSection(new SectionDeletionHandlerMapping(), 강남역))
                .isInstanceOf(DeletionValidationException.class);
        Assertions.assertThatThrownBy(() -> 이호선.removeSection(new SectionDeletionHandlerMapping(), 역삼역))
                .isInstanceOf(DeletionValidationException.class);

        // then
        assertThat(이호선.getStations()).hasSize(2);
    }

    @DisplayName("구간 삭제 실패, 노선에 등록돼있지 않은 역 제거")
    @Test
    void removeSectionFailedByNotExistingStation() {
        // given
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

        // when
        Assertions.assertThatThrownBy(() -> 이호선.removeSection(new SectionDeletionHandlerMapping(), 익명역))
                .isInstanceOf(DeletionValidationException.class);
    }
}
