package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        //given
        칠호선.addSection(중계_마들);

        //then
        assertThatThrownBy(() -> 칠호선.addSection(new Section(칠호선, 중계역, 노원역, 20)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 역 사이 길이보다 크거나 같으면 등록 불가능 합니다.");
    }

    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        //given
        칠호선.addSection(중계_마들);
        칠호선.addSection(중계_노원);

        //then
        assertThatThrownBy(() -> 칠호선.addSection(중계_마들))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우 등록 불가능 합니다.");
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        //given
        칠호선.addSection(노원_마들);

        //when
        Station 하계역 = new Station("하계역");
        Section 하계_중계 = new Section(칠호선, 하계역, 중계역, 10);

        //then
        assertThatThrownBy(() -> 칠호선.addSection(하계_중계))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 노선에 포함되어있지 않은 경우 등록 불가능 합니다.");
    }

    @Test
    void 구간_삭제() {
        //given
        칠호선.addSection(중계_노원);
        칠호선.addSection(노원_마들);

        //when
        칠호선.removeSection(노원역);

        //then
        assertThat(칠호선.getStations()).doesNotContain(노원역);
        assertThat(칠호선.getDistance()).isEqualTo(20);
    }

    @Test
    void 구간이_하나인_노선에서_구간을_제거할_수_없음() {
        //given
        칠호선.addSection(중계_노원);

        //then
        assertThatThrownBy(() -> 칠호선.removeSection(중계역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 하나인 노선에서는 구간 삭제가 불가능합니다.");
    }

    @Test
    void 노선에_등록되어_있지_않은_역을_제거할_수_없음() {
        //given
        칠호선.addSection(노원_마들);
        칠호선.addSection(중계_노원);

        //when
        Station 하계역 = new Station("하계역");

        //then
        assertThatThrownBy(() -> 칠호선.removeSection(하계역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("입력한 역이 등록된 구간이 존재하지 않습니다.");
    }
}
