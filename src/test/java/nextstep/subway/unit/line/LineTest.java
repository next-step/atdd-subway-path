package nextstep.subway.unit.line;

import nextstep.subway.fixture.StationFixture;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    private static final Station 까치산역 = StationFixture.까치산역;

    private static final Station 신도림역 = StationFixture.신도림역;

    private static final Station 신촌역 = StationFixture.신촌역;

    private static final Station 잠실역 = StationFixture.잠실역;

    private Sections sections;

    @BeforeEach
    void setUp() {
        // given
        Section 까치산역_신촌역_구간 = new Section(까치산역, 신촌역, 23);
        sections = new Sections();
        sections.addSection(까치산역_신촌역_구간);
    }

    @Test
    @DisplayName("지하철 노선의 하행 종점역에 구간을 추가한다.")
    void addLastLineSection() {
        // when: 까치산역 - 신촌역 노선에 신촌역 - 잠실역 노선을 추가
        Section 신촌역_잠실역_노선 = new Section(신촌역, 잠실역, 5);
        sections.addSection(신촌역_잠실역_노선);

        // then: 까치산역 - 신촌역 - 잠실역이 조회
        // then: 23 + 5 = 28m의 길이가 조회
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).containsExactly(까치산역, 신촌역, 잠실역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(28)
        );
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역과 하행 종점역 사이에 구간을 추가한다.")
    void addMiddleLineSection() {
        // when: 까치산역 - 신촌역 노선에 까치산역 - 신도림역 노선을 추가
        Section 까치산역_신도림역_노선 = new Section(까치산역, 신도림역, 15);
        sections.addSection(까치산역_신도림역_노선);

        // then: 까치산역 - 신도림역 - 신촌역이 조회
        // then: 23m 길이가 조회
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).containsExactly(까치산역, 신도림역, 신촌역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(23)
        );
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역이 하행역인 구간을 추가한다.")
    void addFirstLineSection() {
        // when: 까치산역 - 신촌역 노선에 신도림역 - 까치산역 노선을 추가
        Section 신도림역_까치산역_노선 = new Section(신도림역, 까치산역, 15);
        sections.addSection(신도림역_까치산역_노선);

        // then:  신도림역 - 까치산역 - 신촌역이 조회
        // then: 15 + 23 = 38m 길이가 조회
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).containsExactly(신도림역, 까치산역, 신촌역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(38)
        );
    }

    @Test
    @DisplayName("역 사이에 기존 역 사이 길이보다 크거나 같은 노선을 등록한다.")
    void addInvalidDistanceLineSection() {
        // when: 까치산역 - 신촌역 노선(23m)에 까치산역 - 신도림역 노선(23m)을 추가한다.
        Section 까치산역_신도림역_노선 = new Section(까치산역, 신도림역, 23);

        // when & then
        assertThatThrownBy(() -> sections.addSection(까치산역_신도림역_노선))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    @Test
    @DisplayName("이미 등록되어 있는 노선을 등록한다.")
    void addAlreadyRegisteredLineSection() {
        // when: 까치산역 - 신촌역 노선에 동일한 노선인 까치산역 - 신촌역 노선을 추가한다.
        Section 까치산역_신촌역_노선 = new Section(까치산역, 신촌역, 23);

        // when & then
        assertThatThrownBy(() -> sections.addSection(까치산역_신촌역_노선))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_SECTION.getMessage());
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않은 노선을 등록한다.")
    void addLineSectionWithUnregisteredStation() {
        // when: 까치산역 - 신촌역 노선에 신도림역 - 잠실역 노선을 추가한다.
        Section 신도림역_잠실역_노선 = new Section(신도림역, 잠실역, 13);

        // when &  then
        assertThatThrownBy(() -> sections.addSection(신도림역_잠실역_노선))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("지하철 노선에 등록된 모든 역을 조회한다.")
    void getAllStations() {
        // when
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        // then
        assertThat(노선에_등록된_역_목록).containsExactly(까치산역, 신촌역);
    }

    @Test
    @DisplayName("지하철 노선의 첫번째 구간을 삭제한다.")
    void removeFirstSection() {
        // given
        Section 신촌역_잠실역_구간 = new Section(신촌역, 잠실역, 5);
        sections.addSection(신촌역_잠실역_구간);

        // when
        sections.deleteSectionByStationId(까치산역.getId());

        // then
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).doesNotContain(까치산역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("지하철 노선의 중간 구간을 삭제한다.")
    void removeMiddleSection() {
        // given
        Section 신촌역_잠실역_구간 = new Section(신촌역, 잠실역, 5);
        sections.addSection(신촌역_잠실역_구간);

        // when
        sections.deleteSectionByStationId(신촌역.getId());

        // then
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).doesNotContain(신촌역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(28)
        );
    }

    @Test
    @DisplayName("지하철의 노선의 마지막 구간을 삭제한다.")
    void removeSection() {
        // given
        Section 신촌역_잠실역_구간 = new Section(신촌역, 잠실역, 5);
        sections.addSection(신촌역_잠실역_구간);

        // when
        sections.deleteSectionByStationId(잠실역.getId());

        // then
        List<Station> 노선에_등록된_역_목록 = sections.getAllStations();

        assertAll(
                () -> assertThat(노선에_등록된_역_목록).doesNotContain(잠실역),
                () -> assertThat(sections.getTotalDistance()).isEqualTo(23)
        );
    }

    @Test
    @DisplayName("등록되어 있지 않은 구간을 삭제한다.")
    void removeNotExistSection() {
        // when & then
        assertThatThrownBy(() -> sections.deleteSectionByStationId(신도림역.getId()))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("구간이 1개일 때 삭제한다.")
    void removeStandaloneSection() {
        // when & then
        assertThatThrownBy((() -> sections.deleteSectionByStationId(신촌역.getId())))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage());
    }
}
