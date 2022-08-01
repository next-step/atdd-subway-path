package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Line 분당선 = new Line("분당선", "red");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        분당선.addSection(new Section(분당선, 양재역, 양재시민의숲역, 10));

        assertThat(분당선.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        Line 분당선 = new Line("분당선", "red");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        분당선.addSection(new Section(분당선, 양재역, 양재시민의숲역, 10));

        assertThat(분당선.getStations()).hasSize(2);
        assertThat(분당선.getStations()).contains(양재역, 양재시민의숲역);
    }

    @Test
    void removeSection() {
        Line 분당선 = new Line("분당선", "red");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        Station 청계산입구역 = new Station("청계산입구역");
        분당선.addSection(new Section(분당선, 양재역, 양재시민의숲역, 10));
        분당선.addSection(new Section(분당선, 양재시민의숲역, 청계산입구역, 10));

        분당선.removeSection();
        assertThat(분당선.getSections().size()).isEqualTo(1);
    }
}
