package nextstep.subway.unit;

import nextstep.subway.controller.dto.SectionCreateRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.repository.StationRepository;
import nextstep.subway.service.LineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@DisplayName("구간 서비스 단위 테스트 without Mock")
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    Station 신림역;
    Station 보라매역;
    Station 보라매병원역;
    Line 신림선;

    @BeforeEach
    public void setUp() {
        신림역 = new Station("신림역");
        보라매역 = new Station("보라매역");
        보라매병원역 = new Station("보라매병원역");

        stationRepository.saveAll(List.of(신림역, 보라매역, 보라매병원역));

        신림선 = lineRepository.save(Line.builder()
                .name("신림선")
                .color("BLUE")
                .upStation(신림역)
                .downStation(보라매역)
                .distance(10L)
                .build());
    }

    @Test
    @DisplayName("지하철 마지막 구간을 등록한다.")
    void 지하철_마지막구간_등록() {
        // given

        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(22L)
                .build();
        lineService.addSection(신림선.getId(), 구간_생성_요청);

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매역, 보라매병원역);
    }

    @Test
    @DisplayName("지하철 중간구간을 등록한다.")
    void 지하철_중간구간_등록() {
        // given

        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(신림역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(5L)
                .build();
        lineService.addSection(신림선.getId(), 구간_생성_요청);

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매병원역, 보라매역);
    }

    @Test
    @DisplayName("지하철 처음구간을 등록한다.")
    void 지하철_처음구간_등록() {
        // given

        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매병원역.getId()))
                .downStationId(String.valueOf(신림역.getId()))
                .distance(10L)
                .build();
        lineService.addSection(신림선.getId(), 구간_생성_요청);

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(보라매병원역, 신림역, 보라매역);
    }

    @Test
    @DisplayName("지하철노선의 마지막역을 삭제한다.")
    void 지하철구간_마지막역_삭제() {
        // given
        Section 보라매보라매병원역 = Section.builder()
                .line(신림선)
                .upStation(보라매역)
                .downStation(보라매병원역)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        // when
        lineService.removeSection(신림선.getId(), 보라매병원역.getId());


        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매역);
    }

    @DisplayName("지하철 구간의 시작역을 삭제한다.")
    @Test
    void 지하철구간_시작역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .line(신림선)
                .upStation(보라매역)
                .downStation(보라매병원역)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //when
        lineService.removeSection(신림선.getId(), 신림역.getId());

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(보라매역, 보라매병원역);

    }

    @DisplayName("지하철 구간의 중간역을 삭제한다.")
    @Test
    void 지하철구간_중간역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .line(신림선)
                .upStation(보라매역)
                .downStation(보라매병원역)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        //when
        lineService.removeSection(신림선.getId(), 보라매역.getId());

        //then
        assertThat(신림선.getSections().getOrderedStations()).containsExactly(신림역, 보라매병원역);
        assertThat(신림선.getSections().getSections().get(0).getDistance()).isEqualTo(21);
    }
}
