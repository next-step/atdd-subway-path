package nextstep.subway.unit;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private Station 강남역;
    private Station 역삼역;
    private Line 이호선;
    private Section 강남_역삼_구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        이호선 = new Line("2호선", "green");
        강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남_역삼_구간);
    }

    @DisplayName("노선의 하행역에 구간을 등록할 수 있다.")
    @Test
    void addDownSection() {
        // given
        Station 선릉역 = new Station("선릉역");
        Section 역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 10);

        // when
        이호선.addSection(역삼_선릉_구간);

        // then
        assertThat(이호선.getSectionList()).hasSize(2);
        assertThat(이호선.getSectionList()).containsExactly(강남_역삼_구간, 역삼_선릉_구간);
    }

    @DisplayName("노선의 상행역에 구간을 등록할 수 있다.")
    @Test
    void addUpSection() {
        // given
        Station 서초역 = new Station("서초역");
        Section 서초_강남_구간 = new Section(이호선, 서초역, 강남역, 10);

        // when
        이호선.addSection(서초_강남_구간);

        // then
        assertThat(이호선.getSectionList()).hasSize(2);
        assertThat(이호선.getSectionList()).containsExactly(서초_강남_구간, 강남_역삼_구간);
    }

    @DisplayName("노선의 중간에 구간을 등록할 수 있다.")
    @Test
    void addMiddleSection() {
        // given
        Station 신규역 = new Station("신규역");
        Section 강남_신규_구간 = new Section(이호선, 강남역, 신규역, 5);

        // when
        이호선.addSection(강남_신규_구간);

        // then
        assertThat(이호선.getSectionList()).hasSize(2);
        assertThat(이호선.getSectionList()).containsExactly(강남_신규_구간, 강남_역삼_구간);
    }

    @DisplayName("연결할 수 없는 구간 등록시 예외가 발생한다.")
    @Test
    void verifyConnectedSection() {
        // given
        Station 서초역 = new Station("서초역");
        Station 선릉역 = new Station("선릉역");
        Section 서초_선릉_구간 = new Section(이호선, 서초역, 선릉역, 10);

        // then
        assertThatThrownBy(() -> 이호선.addSection(서초_선릉_구간))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("연결할 수 없는 구간입니다.");
    }

    @DisplayName("중복된 역 구간 등록시 예외가 발생한다.")
    @Test
    void verifyDuplicationStation() {
        // given
        Section 역삼_강남_구간 =  new Section(이호선, 역삼역, 강남역, 10);

        // then
        assertThatThrownBy(() -> 이호선.addSection(역삼_강남_구간))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복된 역은 등록 불가합니다.");
    }

    @DisplayName("노선의 역 목록을 조회한다.")
    @Test
    void getStationList() {
        // when
        List<Station> 역_목록 = 이호선.getStationList();

        // then
        assertThat(역_목록).containsExactly(강남_역삼_구간.getUpStation(), 강남_역삼_구간.getDownStation());
    }

    @DisplayName("노선의 구간 목록을 조회한다.")
    @Test
    void getSectionList() {
        // given
        Station 서초역 = new Station("서초역");
        Section 서초_강남_구간 = new Section(이호선, 서초역, 강남역, 10);

        이호선.addSection(서초_강남_구간);

        // when
        List<Section> 구간_목록 = 이호선.getSectionList();

        // then
        assertThat(구간_목록).containsExactly(서초_강남_구간, 강남_역삼_구간);
    }

    @DisplayName("노선의 하행 구간을 제거한다.")
    @Test
    void removeDownSection() {
        // given
        Station 선릉역 = new Station("선릉역");
        Section 역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 15);
        이호선.addSection(역삼_선릉_구간);

        // when
        이호선.removeSection(선릉역);

        // then
        assertThat(이호선.getSectionList()).hasSize(1);
    }

    @DisplayName("노선의 상행 구간을 제거한다.")
    @Test
    void removeUpSection() {
        // given
        Station 선릉역 = new Station("선릉역");
        Section 역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 15);
        이호선.addSection(역삼_선릉_구간);

        // when
        이호선.removeSection(강남역);

        // then
        assertThat(이호선.getSectionList()).hasSize(1);
    }

    @DisplayName("노선의 중간 구간을 제거한다.")
    @Test
    void removeMiddleSection() {
        // given
        Integer 강남_역삼_길이 = 강남_역삼_구간.getDistance();

        Station 선릉역 = new Station("선릉역");
        Section 역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 15);
        이호선.addSection(역삼_선릉_구간);

        // when
        이호선.removeSection(역삼역);

        // then
        assertThat(이호선.getSectionList()).hasSize(1);
        Integer 이호선_구간_거리 = 이호선.getSectionList().stream()
            .mapToInt(Section::getDistance)
            .sum();
        assertThat(이호선_구간_거리).isEqualTo(강남_역삼_길이 + 역삼_선릉_구간.getDistance());
    }

    @DisplayName("존재하지 않는 구간은 삭제시 예외가 발생한다.")
    @Test
    void verifyExistsSection() {
        // given
        Station 서초역 = new Station("서초역");

        // then
        assertThatThrownBy(() -> 이호선.removeSection(서초역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("존재하는 구간만 삭제 가능하다.");
    }

    @DisplayName("유일한 구간은 삭제 시 예외가 발생한다.")
    @Test
    void verifyIsOnlySection() {
        // then
        assertThatThrownBy(() -> 이호선.removeSection(역삼역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유일한 구간은 삭제가 불가하다.");
    }
}
