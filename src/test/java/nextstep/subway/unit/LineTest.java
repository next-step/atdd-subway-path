package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    @DisplayName("구간을 등록한다.")
    @Test
    void addSection() {
        //given


        //when
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //then
        assertThat(신림선.getSections().getStationIds().size()).isEqualTo(3);
    }

    @DisplayName("구간을 조회한다.")
    @Test
    void getStations() {
        //given

        //when
        List<Long> stationIds = 신림선.getSections().getStationIds();

        //then
        assertThat(stationIds.size()).isEqualTo(2);
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void removeSection() {
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
        assertThat(신림선.getSections().getStationIds().size()).isEqualTo(2);
    }
}
