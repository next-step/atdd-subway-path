package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given
        Station 마들역 = new Station("마들역");
        Station 노원역 = new Station("노원역");
        Line line = new Line();

        Section 노원_마들 = new Section(line, 노원역, 마들역, 10);

        // when
        line.addSection(노원_마들);

        // then
        assertThat(line.getSections().contains(노원_마들)).isTrue();
    }

    @Test
    void getStations() {
        // given
        Station 마들역 = new Station("마들역");
        Station 노원역 = new Station("노원역");
        Station 중계역 = new Station("중계역");
        Line line = new Line();

        Section 노원_마들 = new Section(line, 노원역, 마들역, 10);
        Section 중계_노원 = new Section(line, 중계역, 노원역, 10);

        // when
        line.addSection(중계_노원);
        line.addSection(노원_마들);

        // then
        assertThat(line.getStations()).containsExactlyInAnyOrder(노원역, 마들역, 중계역);
    }

    @Test
    void removeSection() {
        // give
        Station 마들역 = new Station("마들역");
        Station 노원역 = new Station("노원역");
        Station 중계역 = new Station("중계역");
        Line line = new Line();

        Section 노원_마들 = new Section(line, 노원역, 마들역, 10);
        Section 중계_노원 = new Section(line, 중계역, 노원역, 10);
        line.addSection(중계_노원);
        line.addSection(노원_마들);

        // when
        line.removeSection(노원_마들);

        // then
        assertThat(line.getSections().contains(노원_마들)).isFalse();
    }
}
