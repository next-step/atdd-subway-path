package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 마들역;
    private Station 노원역;
    private Station 중계역;

    private Section 노원_마들;
    private Section 중계_노원;
    private Section 중계_마들;

    private Line 칠호선;

    @BeforeEach
    void setUp() {
        칠호선 = new Line();

        마들역 = new Station("마들역");
        노원역 = new Station("노원역");
        중계역 = new Station("중계역");

        노원_마들 = new Section(칠호선, 노원역, 마들역, 10);
        중계_노원 = new Section(칠호선, 중계역, 노원역, 10);
        중계_마들 = new Section(칠호선, 중계역, 마들역, 20);
    }

    @Test
    void addSection() {
        // when
        칠호선.addSection(노원_마들);

        // then
        assertThat(칠호선.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        // when
        칠호선.addSection(중계_노원);
        칠호선.addSection(노원_마들);

        // then
        assertThat(칠호선.getStations()).containsExactlyInAnyOrder(노원역, 마들역, 중계역);
    }

    @Test
    void removeSection() {
        // give
        칠호선.addSection(중계_노원);
        칠호선.addSection(노원_마들);

        // when
        칠호선.removeSection(노원_마들);

        // then
        assertThat(칠호선.getSections().size()).isEqualTo(1);
    }

    @Test
    void 상행_종점역을_상행으로_새로운_역을_하행으로_등록() {
        //given
        칠호선.addSection(중계_마들);

        //when
        칠호선.addSection(중계_노원);

        //then
        assertThat(칠호선.getStations()).containsExactly(중계역, 노원역, 마들역);
        assertThat(중계_마들.getUpStation()).isEqualTo(노원역);
        assertThat(중계_마들.getDistance()).isEqualTo(10);
    }

    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        //given
        칠호선.addSection(노원_마들);

        //when
        칠호선.addSection(중계_노원);

        //then
        assertThat(칠호선.getStations()).containsExactly(중계역, 노원역, 마들역);
    }

    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        //given
        칠호선.addSection(중계_노원);

        //when
        칠호선.addSection(노원_마들);

        //then
        assertThat(칠호선.getStations()).containsExactly(중계역, 노원역, 마들역);
    }
}
