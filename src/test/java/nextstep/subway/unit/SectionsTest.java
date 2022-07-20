package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Line 분당선;
    private Section 선릉_영통_구간;
    private Section 영통_구의_구간;
    private Section 강남_선릉_구간;


    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
        선릉_영통_구간 = new Section(분당선, FakeStationFactory.선릉역(), FakeStationFactory.영통역(), 10);
        영통_구의_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.구의역(), 10);
        강남_선릉_구간 = new Section(분당선, FakeStationFactory.구의역(), FakeStationFactory.선릉역(), 10);
    }

    @Test
    @DisplayName("지하철 구간 추가 테스트")
    void addSection() {
        //when 구간 목록에 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 선릉_영통_구간);

        //then Section의 길이를 확인한다
        assertThat(sections.getList())
                .hasSize(1)
                .containsAnyOf(선릉_영통_구간);
    }

    @Test
    @DisplayName("구간 추가 실패 테스트 - 추가할 구간의 상행역이 하행 종점과 동일하지 않을 경우")
    void 구간_등록_실패_테스트() {
        //when
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_구간);

        //then
        assertThatThrownBy(() -> sections.add(강남_선릉_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 종점역이 동일한 경우에 등록할 수 있습니다.");
    }


    @Test
    @DisplayName("지하철 구간에 등록된 지하철역 조회 테스트")
    void getStations() {
        //when 구간 목록에 구간을 추가한다
        구간을_추가한다(분당선.getSections(), 선릉_영통_구간);

        //then Section의 길이를 확인한다
        List<Station> stations = 분당선.showAllStations();
        assertThat(stations).hasSize(2);
        assertThat(분당선.showAllStations()).containsAnyOf(FakeStationFactory.선릉역(), FakeStationFactory.영통역());
    }

    @Test
    @DisplayName("지하철 구간 삭제 테스트")
    void removeSection() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 선릉_영통_구간, 영통_구의_구간);

        //when 구간을 삭제한다.
        sections.remove(영통_구의_구간.getDownStation());

        //then Section의 길이를 확인한다
        assertThat(sections.getList())
                .hasSize(1)
                .containsAnyOf(선릉_영통_구간);
    }

    @Test
    @DisplayName("지하철 구간 삭제 실패 테스트 - 구간이 하나이상 등록되어야만 삭제가 가능하다")
    void 등록된_구간이_하나일때_삭제() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 영통_구의_구간);

        //then 삭제가 실패했을 때 IllegalArgumentException 발생 여부를 확인한다.
        assertThatThrownBy(() -> sections.remove(영통_구의_구간.getDownStation()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두개 이상의 구간이 등록되어야 제거가 가능합니다.");
    }

    @Test
    @DisplayName("지하철 구간 삭제 실패 테스트 - 하행 종점역이 아니면 삭제할 수 없다.")
    void 하행종점이_아닌_역을_삭제() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 영통_구의_구간);

        //then 삭제가 실패했을 때 IllegalArgumentException 발생 여부를 확인한다.
        assertThatThrownBy(() -> sections.remove(FakeStationFactory.강남역()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행 종점역이 아니면 제거할 수 없습니다.");
    }

    private void 구간을_추가한다(Sections sectionList, Section ... sectionParams) {
        for (Section section : sectionParams) {
            sectionList.add(section);
        }
    }

    @Test
    @DisplayName("구간 추가 실패 테스트 - 구간 목록이 비어있을 때 삭제를 시도할 경우")
    void 비어있는_구간_목록을_삭제() {
        //when
        Sections sections = 분당선.getSections();

        //then
        assertThatThrownBy(() -> sections.remove(FakeStationFactory.강남역()))
                .isInstanceOf(SubwayException.class)
                .hasMessage("등록된 구간이 없습니다.");
    }

}
