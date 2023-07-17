package nextstep.subway.unit;

import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    private Station 신사역;

    private Station 강남역;

    private Station 판교역;

    private Station 광교역;

    private Sections sections;

    @BeforeEach
    void setUp() {
        // given
        Long 신사역_아이디 = 1L;
        Long 강남역_아이디 = 2L;
        Long 판교역_아이디 = 3L;
        Long 광교역_아이디 = 4L;
        신사역 = new Station(신사역_아이디, "신사역");
        강남역 = new Station(강남역_아이디, "강남역");
        판교역 = new Station(판교역_아이디, "판교역");
        광교역 = new Station(광교역_아이디, "광교역");

        Section 신사역_강남역_구간 = new Section(신사역, 강남역, 15);
        sections = new Sections(
                new ArrayList<>() {{
                    add(신사역_강남역_구간);
                }},
                15
        );
    }

    @Test
    @DisplayName("지하철 구간을 추가한다.")
    void addSection() {
        // when
        Section 강남역_광교역_구간 = new Section(강남역, 광교역, 13);
        sections.addSection(강남역_광교역_구간);

        // then
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).containsExactly(신사역, 강남역, 광교역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(28)
        );
    }

    @Test
    @DisplayName("이미 등록되어 있는 역이 하행 종점역인 구간을 추가한다.")
    void addAlreadyRegisteredDownStation() {
        // given
        Section 강남역_판교역_구간 = new Section(강남역, 판교역, 5);
        Section 판교역_강남역_구간 = new Section(판교역, 강남역, 5);

        // when & then
        assertThatThrownBy(() -> List.of(강남역_판교역_구간, 판교역_강남역_구간)
                .forEach(sections::addSection))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("지하철 노선에 등록된 모든 역을 조회한다.")
    void getAllStations() {
        // when
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        // then
        assertThat(노선에_등록된_역_목록).containsExactly(신사역, 강남역);
    }

    @Test
    @DisplayName("지하철의 노선의 마지막 구간을 삭제한다.")
    void removeSection() {
        // given
        Section 강남역_광교역_구간 = new Section(강남역, 광교역, 13);
        sections.addSection(강남역_광교역_구간);

        // when
        sections.deleteSectionByStationId(광교역.getId());

        // then
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).doesNotContain(광교역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(15)
        );
    }

    @Test
    @DisplayName("지하철 노선의 중간 구간을 삭제한다.")
    void removeMiddleSection() {
        // given
        Section 강남역_광교역_구간 = new Section(강남역, 광교역, 13);
        sections.addSection(강남역_광교역_구간);

        // when & then
        assertThatThrownBy(() -> sections.deleteSectionByStationId(강남역.getId()))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.IS_NOT_LAST_LINE_SECTION.getMessage());
    }

    @Test
    @DisplayName("등록되어 있지 않은 구간을 삭제한다.")
    void removeNotExistSection() {
        // when & then
        assertThatThrownBy(() -> sections.deleteSectionByStationId(광교역.getId()))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("구간이 1개일 때 삭제한다.")
    void removeStandaloneSection() {
        // when & then
        assertThatThrownBy((() -> sections.deleteSectionByStationId(강남역.getId())))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage());
    }
}
