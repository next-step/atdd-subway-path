package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.line.exception.InvalidDownStationException;
import nextstep.subway.line.exception.InvalidSectionLengthException;
import nextstep.subway.line.exception.InvalidUpStationException;
import nextstep.subway.line.exception.SectionAlreadyExistsException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LineTest {

    private Line 신분당선;
    private Station 신사역 = new Station("신사역");
    private Station 논현역 = new Station("논현역");
    private Station 신논현역 = new Station("신논현역");
    private Station 강남역 = new Station("강남역");

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "bg-red-60", new LineSections());
        신분당선.addSection(new LineSection(신분당선, 신사역, 논현역, 10L));
    }

    @DisplayName("addSection 테스트")
    @Nested
    class AddSectionTest {

        @Test
        @DisplayName("하행 종착역에 새로운 지하철 구간을 추가한다.")
        void addSection() {
            // when
            신분당선.addSection(new LineSection(신분당선, 논현역, 신논현역, 10L));

            // then
            LineSections lineSections = 신분당선.getLineSections();
            assertThat(lineSections.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("상행 종착역에 새로운 지하철 구간을 추가한다.")
        void addSection1() {
            // when
            신분당선.addSection(new LineSection(신분당선, 신논현역, 신사역, 10L));

            // then
            LineSections lineSections = 신분당선.getLineSections();
            assertThat(lineSections.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("지하철 노선 가운데 새로운 지하철 구간을 추가한다.")
        void addSection2() {
            //given
            신분당선.addSection(new LineSection(신분당선, 논현역, 강남역, 10L));

            // when
            신분당선.addSection(new LineSection(신분당선, 논현역, 신논현역, 5L));

            // then
            LineSections lineSections = 신분당선.getLineSections();
            assertThat(lineSections.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("새로운 구간의 상행역이 노선에 등록되어 있지 않고 하행역이 상행 종착역이 아니면 InvalidDownStationException 예외가 발생한다.")
        void addSectionException1() {
            // given
            LineSection lineSection = new LineSection(신분당선, 강남역, 논현역, 10L);

            // when, then
            assertThrows(InvalidDownStationException.class, () -> {
                신분당선.addSection(lineSection);
            });
        }

        @Test
        @DisplayName("새로운 구간이 이미 등록되어 있으면 InvalidSectionLengthException 예외가 발생한다.")
        void addSectionException2() {
            // given
            LineSection lineSection = new LineSection(신분당선, 신사역, 신논현역, 10L);

            // when, then
            assertThrows(InvalidSectionLengthException.class, () -> {
                신분당선.addSection(lineSection);
            });
        }

        @Test
        @DisplayName("상행역이 기존 상행역에 존재하고, 하행역이 기존 노선에 존재하면 InvalidDownStationException 예외가 발생한다.")
        void addSectionException3() {
            // given
            신분당선.addSection(new LineSection(신분당선, 논현역, 강남역, 10L));
            LineSection lineSection = new LineSection(신분당선, 논현역, 신사역, 5L);

            // when, then
            assertThrows(InvalidDownStationException.class, () -> {
                신분당선.addSection(lineSection);
            });
        }

        @Test
        @DisplayName("상행역이 기존 상행역에 존재하고, 하행역이 기존 하행역보다 길면 InvalidSectionLengthException 예외가 발생한다.")
        void addSectionException4() {
            // given
            LineSection lineSection = new LineSection(신분당선, 신사역, 논현역, 10L);

            // when, then
            assertThrows(SectionAlreadyExistsException.class, () -> {
                신분당선.addSection(lineSection);
            });
        }
    }

    @DisplayName("removeSection 테스트")
    @Nested
    class RemoveSectionTest {

        @Test
        @DisplayName("노선에 등록된 상행 종점역을 삭제한다.")
        void removeSection() {
            // given
            신분당선.addSection(new LineSection(신분당선, 논현역, 신논현역, 10L));

            // when
            신분당선.deleteSection(신사역);

            // then
            assertThat(신분당선.getLineSections().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("노선에 등록된 하행 종점역을 삭제한다.")
        void removeSection2() {
            // given
            신분당선.addSection(new LineSection(신분당선, 논현역, 신논현역, 10L));

            // when
            신분당선.deleteSection(신논현역);

            // then
            assertThat(신분당선.getLineSections().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("노선에 등록된 중간역을 삭제한다.")
        void removeSection3() {
            // given
            신분당선.addSection(new LineSection(신분당선, 논현역, 신논현역, 10L));

            // when
            신분당선.deleteSection(논현역);

            // then
            assertThat(신분당선.getLineSections().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("삭제할 구간이 1개인 경우 SubwayException 예외가 발생한다.")
        void removeSectionException2() {
            // when, then
            assertThrows(SubwayException.class, () -> {
                신분당선.deleteSection(논현역);
            });
        }
    }

    @DisplayName("getStations 테스트")
    @Nested
    class getStationsTest {

        @Test
        @DisplayName("지하철 구간의 모든역을 조회한다.")
        void getStations() {
            // given
            신분당선.addSection(new LineSection(신분당선, 논현역, 신논현역, 10L));

            // when
            LineSections lineSections = 신분당선.getLineSections();
            List<Station> stations = lineSections.getStations();

            // then
            assertThat(stations.size()).isEqualTo(3);
        }
    }
}