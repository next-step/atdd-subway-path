package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.line.exception.SectionAlreadyExistsException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LineSectionsTest {

    private LineSections lineSections;
    private Line 신분당선;
    private Station 신사역;
    private Station 논현역;
    private Station 신논현역;
    private Station 강남역;

    @BeforeEach
    void setUp() {
        lineSections = new LineSections();
        신분당선 = new Line("신분당선", "bg-red-600", lineSections);
        신사역 = new Station("신사역");
        논현역 = new Station("논현역");
        신논현역 = new Station("신논현역");
        강남역 = new Station("강남역");
    }

    @Nested
    @DisplayName("addSection 테스트")
    class AddSection {

        @Test
        @DisplayName("첫 번째 구간을 추가한다")
        void addFirstSection() {
            // when
            LineSection section = new LineSection(신분당선, 신사역, 논현역, 10L);
            lineSections.addSection(section);

            // then
            assertThat(lineSections.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("기존 구간의 상행 종점에 새로운 구간을 추가한다")
        void addSectionToUpTerminus() {
            // when
            lineSections.addSection(new LineSection(신분당선, 논현역, 신논현역, 10L));
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 5L));

            // then
            assertThat(lineSections.size()).isEqualTo(2);
            assertThat(lineSections.getStations()).containsExactly(신사역, 논현역, 신논현역);
        }

        @Test
        @DisplayName("기존 구간의 하행 종점에 새로운 구간을 추가한다")
        void addSectionToDownTerminus() {
            // when
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));
            lineSections.addSection(new LineSection(신분당선, 논현역, 신논현역, 5L));

            // then
            assertThat(lineSections.size()).isEqualTo(2);
            assertThat(lineSections.getStations()).containsExactly(신사역, 논현역, 신논현역);
        }

        @Test
        @DisplayName("이미 존재하는 구간을 추가하려고 하면 예외를 발생시킨다")
        void throwExceptionWhenAddingExistingSection() {
            // when
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));

            // then
            assertThatThrownBy(() -> lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 5L)))
                .isInstanceOf(SectionAlreadyExistsException.class);
        }
    }

    @Nested
    @DisplayName("deleteSection 테스트")
    class DeleteSection {

        @Test
        @DisplayName("상행 종점역을 삭제한다")
        void deleteFirstStation() {
            // given
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));
            lineSections.addSection(new LineSection(신분당선, 논현역, 신논현역, 5L));

            // when
            lineSections.deleteSection(신사역);

            // then
            assertThat(lineSections.size()).isEqualTo(1);
            assertThat(lineSections.getStations()).containsExactly(논현역, 신논현역);
        }

        @Test
        @DisplayName("하행 종점역을 삭제한다")
        void deleteLastStation() {
            // given
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));
            lineSections.addSection(new LineSection(신분당선, 논현역, 신논현역, 5L));

            // when
            lineSections.deleteSection(신논현역);

            // then
            assertThat(lineSections.size()).isEqualTo(1);
            assertThat(lineSections.getStations()).containsExactly(신사역, 논현역);
        }

        @Test
        @DisplayName("중간 역을 삭제한다")
        void deleteMiddleStation() {
            // given
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));
            lineSections.addSection(new LineSection(신분당선, 논현역, 신논현역, 5L));

            // when
            lineSections.deleteSection(논현역);

            // then
            assertThat(lineSections.size()).isEqualTo(1);
            assertThat(lineSections.getStations()).containsExactly(신사역, 신논현역);
        }

        @Test
        @DisplayName("구간이 하나만 남았을 때 삭제하려고 하면 예외를 발생시킨다")
        void throwExceptionWhenDeletingLastSection() {
            // given
            lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));

            // when, then
            assertThatThrownBy(() -> lineSections.deleteSection(논현역))
                .isInstanceOf(SubwayException.class);
        }
    }

    @Test
    @DisplayName("getStations 메서드는 모든 역을 순서대로 반환한다")
    void getStations() {
        lineSections.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));
        lineSections.addSection(new LineSection(신분당선, 논현역, 신논현역, 5L));
        lineSections.addSection(new LineSection(신분당선, 신논현역, 강남역, 7L));

        // when
        List<Station> stations = lineSections.getStations();

        // then
        assertThat(stations).containsExactly(신사역, 논현역, 신논현역, 강남역);
    }
}
