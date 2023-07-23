package nextstep.subway.unit.line;

import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.addition.SectionAdditionHandlerMapping;
import nextstep.subway.line.domain.entity.deletion.handler.SectionDeletionHandlerMapping;
import nextstep.subway.line.domain.entity.deletion.SectionDeletionOperator;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 삭제 Operator 관련 기능")
public class SectionDeletionOperatorTest {

    final int SECTION_DEFAULT_DISTANCE = 10;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 익명역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        이호선 = new Line("이호선", "Green", SECTION_DEFAULT_DISTANCE, 강남역, 역삼역);
    }

    @Nested
    @DisplayName("구간 삭제에 성공은")
    class DeleteSectionSuccess {

        @BeforeEach
        @DisplayName("구간이 두개 이상인 경우")
        void setUp() {
            // given
            이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));
        }

        @DisplayName("상행 종착역 삭제할 때 발생")
        @Test
        void deleteTopStationSection() {
            // when
            new SectionDeletionOperator(new SectionDeletionHandlerMapping())
                    .apply(이호선.getSections(), 강남역);

            // then
            assertThat(이호선.getStations()).doesNotContain(강남역);
        }

        @DisplayName("하행 종착역 삭제할 때 발생")
        @Test
        void deleteLastStationSection() {
            // when
            new SectionDeletionOperator(new SectionDeletionHandlerMapping())
                    .apply(이호선.getSections(), 선릉역);

            // then
            assertThat(이호선.getStations()).doesNotContain(선릉역);
        }

        @DisplayName("하행 종착역 삭제할 때 발생")
        @Test
        void deleteMiddleStationSection() {
            // when
            new SectionDeletionOperator(new SectionDeletionHandlerMapping())
                    .apply(이호선.getSections(), 역삼역);

            // then
            assertThat(이호선.getStations()).doesNotContain(역삼역);
        }
    }

    @Nested
    @DisplayName("구간 삭제 실패는")
    class SectionDeletionFails {
        @DisplayName("노선 내 구간이 하나일 때 발생")
        @Test
        void deletionFailedBySingularSectionExists() {
            assertThatThrownBy(() -> new SectionDeletionOperator(new SectionDeletionHandlerMapping())
                    .apply(이호선.getSections(), 강남역))
                    .isInstanceOf(DeletionValidationException.class);
        }

        /**
         * When SectionDeletionOperator에 노선에 존재하지 않는 역의 구간 삭제를 요청하면
         * Then DeletionValidationException을 던진다.
         */
        @DisplayName("노선 내 해당 역이 존재하지 않을 때 발생")
        @Test
        void deletionFailedByStationNotExists() {
            assertThatThrownBy(() -> new SectionDeletionOperator(new SectionDeletionHandlerMapping())
                    .apply(이호선.getSections(), 익명역))
                    .isInstanceOf(DeletionValidationException.class);
        }
    }
}
