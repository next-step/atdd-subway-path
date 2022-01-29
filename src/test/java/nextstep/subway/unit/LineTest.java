package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 등록할 경우")
    @Test
    void addSection() {
        Line line = new Line("2호선", "빨간색");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 신사역 = new Station("신사역");
        line.registerSection(강남역, 양재역, 100);
        line.registerSection(양재역, 신사역, 100);

        assertThat(line.getSections()).hasSize(2);
    }

    @DisplayName("구간 목록 사이에 새로운 구간을 등록할 경우")
    @Test
    void addSection2() {
        Line line = new Line("2호선", "빨간색");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 신사역 = new Station("신사역");
        line.registerSection(강남역, 양재역, 100);
        line.registerSection(강남역, 신사역, 50);

        assertThat(line.getSections()).hasSize(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        Line line = new Line("2호선", "빨간색");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 신사역 = new Station("신사역");
        line.registerSection(강남역, 양재역, 100);
        line.registerSection(강남역, 신사역, 50);

        List<Section> sections = line.getSections();
        assertThat(sections).extracting(Section::getUpStation).extracting(Station::getName).containsExactly("강남역", "신사역");
        assertThat(sections).extracting(Section::getDownStation).extracting(Station::getName).containsExactly("신사역", "양재역");
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        Line line = new Line("2호선", "빨간색");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 신사역 = new Station("신사역");
        line.registerSection(강남역, 양재역, 100);
        line.registerSection(양재역, 신사역, 100);

        line.deleteSection(신사역);
        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);
        assertThat(sections).extracting(Section::getUpStation).extracting(Station::getName).containsExactly("강남역");
    }
}
