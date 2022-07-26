package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import nextstep.subway.fake.SubwayExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    protected Line 분당선;
    protected Section 선릉_영통_거리10_구간;
    protected Section 영통_구의_거리10_구간;
    protected Section 강남_선릉_거리7_구간;
    protected Section 영통_신촌_거리7_구간;
    protected Section 신촌_영통_거리7_구간;
    protected Section 영통_강남_거리7_구간;


    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
        선릉_영통_거리10_구간 = new Section(분당선, FakeStationFactory.선릉역(), FakeStationFactory.영통역(), 10);
        영통_구의_거리10_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.구의역(), 10);
        영통_강남_거리7_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.강남역(), 7);
        강남_선릉_거리7_구간 = new Section(분당선, FakeStationFactory.강남역(), FakeStationFactory.선릉역(), 10);
        영통_신촌_거리7_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.신촌역(), 7);
        신촌_영통_거리7_구간 = new Section(분당선, FakeStationFactory.신촌역(), FakeStationFactory.영통역(), 7);
    }

    @Test
    @DisplayName("지하철 구간 추가 테스트")
    void addSection() {
        //when 구간 목록에 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 선릉_영통_거리10_구간);

        //then Section의 길이를 확인한다
        assertThat(sections.getValues())
                .hasSize(1)
                .containsAnyOf(선릉_영통_거리10_구간);
    }

    @Test
    @DisplayName("새로운 구간을 상행 종점으로 추가한다")
    void 상행_종점에_구간_추가() {
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_거리10_구간);
        sections.add(영통_구의_거리10_구간);

        //when
        sections.add(강남_선릉_거리7_구간);

        //then
        assertThat(sections.findConnectedStations()).containsExactly(
                FakeStationFactory.강남역(),
                FakeStationFactory.선릉역(),
                FakeStationFactory.영통역(),
                FakeStationFactory.구의역()
        );
    }

    @Test
    @DisplayName("지하철 구간 등록 실패 테스트 - 기존 거리보다 긴 구간을 추가한다.")
    void 상행역_기준_중간_지점에_구간_추가() {
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_거리10_구간);

        //when
        sections.add(영통_강남_거리7_구간);

        //then
        assertThatThrownBy(
                () -> sections.add(영통_신촌_거리7_구간)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage(SubwayExceptionMessage.기존_거리보다_길거나_같은_구간을_등록할_경우());
    }

    @Test
    @DisplayName("하행역 기준으로 새로운 구간을 중간에 추가한다")
    void 하행역_기준_중간_지점에_구간_추가() {
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_거리10_구간);
        sections.add(영통_구의_거리10_구간);

        //when
        sections.add(신촌_영통_거리7_구간);

        //then
        assertThat(선릉_영통_거리10_구간.getDistance()).isEqualTo(3);
        assertThat(sections.findConnectedStations()).containsExactly(
                FakeStationFactory.선릉역(),
                FakeStationFactory.신촌역(),
                FakeStationFactory.영통역(),
                FakeStationFactory.구의역()
        );
    }

    @Test
    void 상행역이_동일한_역을_조회한다() {
        //given
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_거리10_구간);
        sections.add(영통_구의_거리10_구간);

        //when
        Section sameUpStationSection = sections.findSameStationSection(영통_신촌_거리7_구간);
        Station findSameUpStation = sameUpStationSection.getUpStation();

        //then
        assertThat(findSameUpStation.getName()).isEqualTo("영통역");
    }

    @Test
    void 하행역이_동일한_역을_조회한다() {
        //given
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_거리10_구간);
        sections.add(영통_구의_거리10_구간);

        //when
        Section sameDownStation = sections.findSameStationSection(신촌_영통_거리7_구간);
        Station findSameDownStation = sameDownStation.getDownStation();

        //then
        assertThat(findSameDownStation.getName()).isEqualTo("영통역");
    }

    @Test
    @DisplayName("등록된 구간에서 상행 종점을 조회한다")
    void 상행_종점을_조회한다() {
        //given
        Sections sections = 분당선.getSections();

        //when
        sections.add(강남_선릉_거리7_구간);

        //then
        assertThat(sections.findUpTerminus()).isEqualTo(FakeStationFactory.강남역());
    }

    @Test
    void 연결된_다음역을_찾는다() {
        //given
        Sections sections = 분당선.getSections();

        //when
        sections.add(강남_선릉_거리7_구간);

        //then
        assertThat(sections.findNextStation(FakeStationFactory.강남역())).isEqualTo(FakeStationFactory.선릉역());
    }


    /* given 구간 목록에 구간을 추가한다.
     * when, then
     * 상행역과 하행역이 전부 포함되어있지 않은 역을 추가하고
     * IllegalArgumentException 과 지정된 메세지를 리턴받는다.
     */
    @Test
    @DisplayName("구간 등록 - 상행역과 하행역이 모두 포함되어 있지 않을 때")
    void 구간_등록_실패_테스트_두번째() {
        //when
        Sections sections = 분당선.getSections();
        sections.add(강남_선릉_거리7_구간);

        //then
        assertThatThrownBy(() -> sections.add(영통_구의_거리10_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayExceptionMessage.아무런_역도_포함되지_않은_경우());
    }

    @Test
    @DisplayName("지하철 구간에 등록된 지하철역 조회 테스트")
    void getStations() {
        //when 구간 목록에 구간을 추가한다
        구간을_추가한다(분당선.getSections(), 선릉_영통_거리10_구간);

        //then Section의 길이를 확인한다
        List<Station> stations = 분당선.findAllStation();
        assertThat(stations).hasSize(2);
        assertThat(분당선.findAllStation()).containsAnyOf(FakeStationFactory.선릉역(), FakeStationFactory.영통역());
    }

    @Test
    @DisplayName("지하철 구간 삭제 테스트")
    void removeSection() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 선릉_영통_거리10_구간, 영통_구의_거리10_구간);

        //when 구간을 삭제한다.
        sections.remove(영통_구의_거리10_구간.getDownStation());

        //then Section의 길이를 확인한다
        assertThat(sections.getValues())
                .hasSize(1)
                .containsAnyOf(선릉_영통_거리10_구간);
    }

    @Test
    @DisplayName("지하철 구간 삭제 실패 테스트 - 구간이 하나이상 등록되어야만 삭제가 가능하다")
    void 등록된_구간이_하나일때_삭제() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 영통_구의_거리10_구간);

        //then 삭제가 실패했을 때 IllegalArgumentException 발생 여부를 확인한다.
        assertThatThrownBy(() -> sections.remove(영통_구의_거리10_구간.getDownStation()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두개 이상의 구간이 등록되어야 제거가 가능합니다.");
    }

    @Test
    @DisplayName("지하철 구간 삭제 실패 테스트 - 하행 종점역이 아니면 삭제할 수 없다.")
    void 하행종점이_아닌_역을_삭제() {
        //given 지하철 역과 노선을 생성하고 구간을 추가한다
        Sections sections = 분당선.getSections();
        구간을_추가한다(sections, 선릉_영통_거리10_구간);
        구간을_추가한다(sections, 영통_구의_거리10_구간);

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

}
