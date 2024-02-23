package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위 테스트")
class LineTest {

    Station 신림역;
    Station 보라매역;
    Station 보라매병원역;
    Line 신림선;
    private final Long 신림역_아이디= 1L;
    private final Long 보라매역_아이디= 2L;
    private final Long 보라매병원역_아이디= 3L;

    @BeforeEach
    public void setUp() {
        신림역 = new Station("신림역");
        ReflectionTestUtils.setField(신림역, "id", 신림역_아이디);
        보라매역 = new Station("보라매역");
        ReflectionTestUtils.setField(보라매역, "id", 보라매역_아이디);
        보라매병원역 = new Station("보라매병원역");
        ReflectionTestUtils.setField(보라매병원역, "id", 보라매병원역_아이디);

        신림선 = Line.builder()
                .name("신림선")
                .upStation(신림역)
                .downStation(보라매역)
                .color("BLUE")
                .distance(10L)
                .build();
    }

    @DisplayName("지하철 마지막구간을 등록한다.")
    @Test
    void 지하철_마지막구간_등록() {
        //given

        //when
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(10L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매역, 보라매병원역);
    }

    @DisplayName("지하철 중간구간을 등록한다.")
    @Test
    void 지하철_중간구간_등록() {
        //given

        //when
        Section 신림보라매병원구간 = Section.builder()
                .upStation(신림역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(5L)
                .build();
        신림선.addSection(신림보라매병원구간);

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매병원역, 보라매역);
    }

    @DisplayName("지하철 처음구간을 등록한다.")
    @Test
    void 지하철_처음구간_등록() {
        //given

        //when
        Section 보라매병원신림구간 = Section.builder()
                .upStation(보라매병원역)
                .downStation(신림역)
                .line(신림선)
                .distance(5L)
                .build();
        신림선.addSection(보라매병원신림구간);

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(보라매병원역, 신림역, 보라매역);
    }

    @DisplayName("구간을 조회한다.")
    @Test
    void 지하철_구간_조회() {
        //given

        //when
        List<Station> orderedStations = 신림선.getSections().getOrderedStations();

        //then
        assertThat(orderedStations).containsExactly(신림역, 보라매역);
    }

    @DisplayName("지하철 구간의 마지막역을 삭제한다.")
    @Test
    void 지하철구간_마지막역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //when
        신림선.removeSection(보라매병원역.getId());

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매역);
    }

    @DisplayName("지하철 구간의 시작역을 삭제한다.")
    @Test
    void 지하철구간_시작역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(10L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //when
        신림선.removeSection(신림역.getId());

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(보라매역, 보라매병원역);
    }

    @DisplayName("지하철 구간의 중간역을 삭제한다.")
    @Test
    void 지하철구간_중간역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(10L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //when
        신림선.removeSection(보라매역.getId());

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매병원역);
        assertThat(신림선.getSections().getSections().get(0).getDistance()).isEqualTo(20);
    }
}
