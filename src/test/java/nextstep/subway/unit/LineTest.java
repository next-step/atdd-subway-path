package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

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
    void removeStation() {
        Line 분당선 = new Line("분당선", "red");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        Station 청계산입구역 = new Station("청계산입구역");
        분당선.addSection(new Section(분당선, 양재역, 양재시민의숲역, 10));
        분당선.addSection(new Section(분당선, 양재시민의숲역, 청계산입구역, 10));

        분당선.removeStation(양재시민의숲역);
        assertThat(분당선.getStations()).containsExactly(양재역, 청계산입구역);
    }

    @Test
    void addSectionAtMiddleNext() {
        Line 분당선 = new Line("분당선", "red");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        Station 청계산입구역 = new Station("청계산입구역");
        Station 판교역 = new Station("판교역");
        분당선.addSection(new Section(분당선, 양재역, 양재시민의숲역, 10));

        분당선.addSection(new Section(분당선, 양재시민의숲역, 판교역, 5));
        분당선.addSection(new Section(분당선, 양재시민의숲역, 청계산입구역, 3));

        assertThat(분당선.getSections()).hasSize(3);
        assertThat(분당선.getStations()).containsExactly(양재역, 양재시민의숲역, 청계산입구역, 판교역);
        assertThat(분당선.getSections().stream().filter(it -> it.getUpStation() == 청계산입구역 && it.getDownStation() == 판교역).findFirst().get().getDistance()).isEqualTo(5-3);
    }

    @Test
    void addSectionAtMiddlePrev() {
        Line 분당선 = new Line("분당선", "red");
        Station 양재역 = new Station("양재역");
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        Station 강남역 = new Station("강남역");
        Station 청계산입구역 = new Station("청계산입구역");
        분당선.addSection(new Section(분당선, 양재역, 청계산입구역, 10));

        분당선.addSection(new Section(분당선, 강남역, 양재역, 5));
        분당선.addSection(new Section(분당선, 양재시민의숲역, 청계산입구역, 3));

        assertThat(분당선.getSections()).hasSize(3);
        assertThat(분당선.getStations()).containsExactly(강남역, 양재역, 양재시민의숲역, 청계산입구역);
    }
}
