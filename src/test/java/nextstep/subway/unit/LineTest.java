package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.exception.SubwayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 Unit Test")
class LineTest {

    private Line 신분당선;
    private Station 강남역;
    private Station 양재역;
    private Section 최초_구간;

    @BeforeEach
    void init() {
        신분당선 = new Line("신분당선", "빨강");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        최초_구간 = new Section(신분당선, 강남역, 양재역, 10);
    }

    /**
     * given line 에 추가할 section 이 존재하고
     * when line 에 addSection(section) 을 진행하면
     * then line.sections 에 section 이 추가된다.
     */
    @DisplayName("지하철 노선에 구간을 추가 - 뒤에")
    @Test
    void addLineSectionNextToDown() {
        // given
        신분당선.addSection(최초_구간);

        // when
        Section newSection = section(신분당선, "양재역", "양재시민의숲", 8);
        신분당선.addSection(newSection);
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(신분당선.getSections()).contains(newSection);
        assertThat(stations).containsExactly(강남역, newSection.getUpStation(), newSection.getDownStation());
    }

    /**
     * given line 에 추가할 section 이 존재하고
     * when line 에 addSection(section) 을 진행하면
     * then line.sections 에 section 이 추가된다.
     */
    @DisplayName("지하철 노선에 구간을 추가 - 중간에")
    @Test
    void addSectionBetweenUpAndDown() {
        // given
        신분당선.addSection(최초_구간);

        // when
        Section newSection = section(신분당선, "강남역", "양재시민의숲", 8);
        신분당선.addSection(newSection);
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(신분당선.getSections()).contains(newSection);
        assertThat(stations).containsExactly(newSection.getUpStation(), newSection.getDownStation(), 양재역);
    }

    /**
     * given line 에 추가할 section 이 존재하고
     * when line 에 addSection(section) 을 진행하면
     * then line.sections 에 section 이 추가된다.
     */
    @DisplayName("지하철 노선에 구간을 추가 - 앞에")
    @Test
    void addSectionFrontOfUp() {
        // given
        신분당선.addSection(최초_구간);

        // when
        Section newSection = section(신분당선, "양재시민의숲", "강남역", 8);
        신분당선.addSection(newSection);
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(신분당선.getSections()).contains(newSection);
        assertThat(stations).containsExactly(newSection.getUpStation(), 강남역, 양재역);
    }

    /**
     * given 초기화된(첫 구간 들어간) line 에 section 을 추가하고
     * when line 에서 마지막 section(station) 을 삭제하면
     * then line 에 처음에 추가한 section 하나만 남아있게 된다.
     */
    @DisplayName("지하철 노선 구간 삭제 - 맨 뒤")
    @Test
    void removeSectionLast() {
        // given
        신분당선.addSection(최초_구간);

        Section newSection = section(신분당선, "양재역", "양재시민의숲", 8);
        신분당선.addSection(newSection);

        assertThat(신분당선.getSections()).contains(newSection);

        // when
        신분당선.removeSection(new Station("양재시민의숲"));

        // then
        assertThat(신분당선.getSections())
                .containsOnly(section(신분당선,"강남역", "양재역", 10));
    }

    /**
     * given 초기화된(첫 구간 들어간) line 에 section 을 추가하고
     * when line 에서 첫 section(station) 을 삭제하면
     * then line 에 후에 추가한 section 하나만 남아있게 된다.
     */
    @DisplayName("지하철 노선 구간 삭제 - 맨 앞")
    @Test
    void removeSectionFirst() {
        // given
        신분당선.addSection(최초_구간);

        Section newSection = section(신분당선, "양재역", "양재시민의숲", 8);
        신분당선.addSection(newSection);

        assertThat(신분당선.getSections()).contains(newSection);

        // when
        신분당선.removeSection(new Station("강남역"));

        // then
        assertThat(신분당선.getSections())
                .containsOnly(section(신분당선, "양재역", "양재시민의숲", 8));
    }

    /**
     * given 초기화된(첫 구간 들어간) line 에 section 을 추가하고
     * when line 에서 중간 section(station) 을 삭제하면
     * then line 에 첫 역과 끝 역을 포함한 section 하나만 남아있게 된다.
     */
    @DisplayName("지하철 노선 구간 삭제 - 중간")
    @Test
    void removeSectionMiddle() {
        // given
        신분당선.addSection(최초_구간);

        Section newSection = section(신분당선, "양재역", "양재시민의숲", 8);
        신분당선.addSection(newSection);

        assertThat(신분당선.getSections()).contains(newSection);

        // when
        신분당선.removeSection(new Station("양재역"));

        // then
        assertThat(신분당선.getSections())
                .containsOnly(section(신분당선, "강남역","양재시민의숲", 18));
    }

    private Section section(Line line, String upStationName, String downStationName, int distance) {
        Station newUpStation = new Station(upStationName);
        Station newDownStation = new Station(downStationName);
        return new Section(line, newUpStation, newDownStation, distance);
    }

    /**
     * given line 을 초기화 하고(첫 구간을 추가하고)
     * when  section 을 삭제하면
     * then "한 개의 구간만이 존재합니다." 예외가 발생한다.
     */
    @DisplayName("지하철 노선 구간 삭제 실패 - 구간이 1개인 경우")
    @Test
    void removeSectionFailCauseFinalSection() {
        // given
        신분당선.addSection(최초_구간);

        // when, then
        assertThatThrownBy(() -> 신분당선.removeSection(최초_구간.getDownStation()))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionMessage.ONLY_ONE_SECTION.msg());
    }

    /**
     * given line 을 초기화 하고(2개 이상의 구간을 추가하고)
     * when  노선에 포함되지 않은 역을 삭제하면
     * then "역이 노선에 존재하지 않습니다." 예외가 발생한다.
     */
    @DisplayName("지하철 노선 구간 삭제 실패 - 노선에 없는 역일 경우")
    @Test
    void removeSectionFailCauseNotExist() {
        // given
        신분당선.addSection(최초_구간);
        Section newSection = section(신분당선, "양재역", "양재시민의숲", 8);
        신분당선.addSection(newSection);

        // when, then
        assertThatThrownBy(() -> 신분당선.removeSection(new Station("정자역")))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionMessage.DOES_NOT_EXIST_STATION.msg());
    }
}
