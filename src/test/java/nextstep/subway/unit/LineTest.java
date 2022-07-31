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

    private Line line;
    private Station upStation;
    private Station downStation;
    private Section section;

    @BeforeEach
    void init() {
        line = new Line("신분당선", "빨강");
        upStation = new Station("강남역");
        downStation = new Station("양재역");
        section = new Section(line, upStation, downStation, 10);
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
        line.addSection(section);

        // when
        Section newSection = section(line, "양재역", "양재시민의숲", 8);
        line.addSection(newSection);
        List<Station> stations = line.getStations();

        // then
        assertThat(line.getSections()).contains(newSection);
        assertThat(stations).containsExactly(upStation, newSection.getUpStation(), newSection.getDownStation());
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
        line.addSection(section);

        // when
        Section newSection = section(line, "강남역", "양재시민의숲", 8);
        line.addSection(newSection);
        List<Station> stations = line.getStations();

        // then
        assertThat(line.getSections()).contains(newSection);
        assertThat(stations).containsExactly(newSection.getUpStation(), newSection.getDownStation(), downStation);
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
        line.addSection(section);

        // when
        Section newSection = section(line, "양재시민의숲", "강남역", 8);
        line.addSection(newSection);
        List<Station> stations = line.getStations();

        // then
        assertThat(line.getSections()).contains(newSection);
        assertThat(stations).containsExactly(newSection.getUpStation(), upStation, downStation);
    }

    /**
     * given 초기화된(첫 구간 들어간) line 에 section 을 추가하고
     * when line 에서 section 을 삭제하면
     * then line 에 처음에 추가한 section 하나만 남아있게 된다.
     */
    @DisplayName("지하철 노선 구간 삭제")
    @Test
    void removeSection() {
        // given
        line.addSection(section);

        Section newSection = section(line, "양재역", "양재시민의숲", 8);
        line.addSection(newSection);

        assertThat(line.getSections()).contains(newSection);

        // when
        line.removeSection(newSection.getDownStation());

        // then
        assertThat(line.getSections()).containsOnly(section);
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
        line.addSection(section);

        // when, then
        assertThatThrownBy(() -> line.removeSection(section.getDownStation()))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionMessage.ONLY_ONE_SECTION.msg());

    }
}
