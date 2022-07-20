package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    @Test
    @DisplayName("지하철 구간 추가 테스트")
    void addSection() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");
        Line 분당선 = new Line("분당선", "red");
        Section 선릉_역삼_구간 = new Section(분당선, 선릉역, 역삼역, 10);

        //when 구간 목록에 구간을 추가한다
        Sections sections = 분당선.getSections();
        sections.add(선릉_역삼_구간);

        //then Section의 길이를 확인한다
        assertThat(sections.getList())
                .hasSize(1)
                .containsAnyOf(선릉_역삼_구간);
    }

    @Test
    @DisplayName("지하철 구간에 등록된 지하철역 조회 테스트")
    void getStations() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");
        Line 분당선 = new Line("분당선", "red");
        Section 선릉_역삼_구간 = new Section(분당선, 선릉역, 역삼역, 10);

        //when 구간 목록에 구간을 추가한다
        Sections sections = 분당선.getSections();
        sections.add(선릉_역삼_구간);
        List<Station> stations = 분당선.showAllStations();

        //then Section의 길이를 확인한다
        assertThat(stations).hasSize(2);
        assertThat(분당선.showAllStations()).containsAnyOf(선릉역, 역삼역);
    }

    @Test
    @DisplayName("지하철 구간 삭제 테스트")
    void removeSection() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");
        Station 강남역 = new Station("선릉역");
        Line 분당선 = new Line("분당선", "red");
        Section 선릉_역삼_구간 = new Section(분당선, 선릉역, 역삼역, 10);
        Section 역삼_강남_구간 = new Section(분당선, 역삼역, 강남역, 10);

        Sections sections = 분당선.getSections();
        sections.add(선릉_역삼_구간);
        sections.add(역삼_강남_구간);

        //when 구간을 삭제한다.
        sections.remove(역삼_강남_구간.getDownStation());

        //then Section의 길이를 확인한다
        assertThat(sections.getList())
                .hasSize(1)
                .containsAnyOf(선릉_역삼_구간);
    }

    @Test
    @DisplayName("지하철 구간 삭제 실패 테스트 - 구간이 하나이상 등록되어야만 삭제가 가능하다")
    void 등록된_구간이_하나일때_삭제() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");
        Station 강남역 = new Station("선릉역");

        Line 분당선 = new Line("분당선", "red");

        Section 선릉_역삼_구간 = new Section(분당선, 선릉역, 역삼역, 10);
        Section 역삼_강남_구간 = new Section(분당선, 역삼역, 강남역, 10);

        Sections sections = 분당선.getSections();
        sections.add(선릉_역삼_구간);

        //then 삭제가 실패했을 때 IllegalArgumentException 발생 여부를 확인한다.
        assertThatThrownBy(() -> sections.remove(역삼_강남_구간.getDownStation()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두개 이상의 구간이 등록되어야 제거가 가능합니다.");
    }

    @Test
    @DisplayName("지하철 구간 삭제 실패 테스트 - 하행 종점역이 아니면 삭제할 수 없다.")
    void 하행종점이_아닌_역을_삭제() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");
        Station 강남역 = new Station("강남역");

        Line 분당선 = new Line("분당선", "red");

        Section 선릉_역삼_구간 = new Section(분당선, 선릉역, 역삼역, 10);
        Section 역삼_강남_구간 = new Section(분당선, 역삼역, 강남역, 10);

        Sections sections = 분당선.getSections();
        sections.add(선릉_역삼_구간);
        sections.add(역삼_강남_구간);

        //then 삭제가 실패했을 때 IllegalArgumentException 발생 여부를 확인한다.
        assertThatThrownBy(() -> sections.remove(역삼역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행 종점역이 아니면 제거할 수 없습니다.");
    }

}
