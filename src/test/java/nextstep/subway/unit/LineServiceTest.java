package nextstep.subway.unit;

import nextstep.subway.controller.dto.LineResponse;
import nextstep.subway.controller.dto.SectionCreateRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.repository.StationRepository;
import nextstep.subway.service.LineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


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

    /**
     * given stationRepository와 lineRepository를 활용하여 초기값 셋팅
     * when lineService.addSection 호출
     * then line.getSections 메서드를 통해 검증
     */
    @Test
    @DisplayName("구간을 등록한다.")
    void addSection() {
        // given
        Station 신림역 = Station.builder().id(1L).name("신림역").build();
        Station 보라매역 = Station.builder().id(2L).name("보라매역").build();
        Station 보라매병원역 = Station.builder().id(3L).name("보라매병원역").build();
        stationRepository.saveAll(List.of(신림역, 보라매역, 보라매병원역));

        Line 신림선 = Line.builder()
                .name("신림선")
                .color("BLUE")
                .build();
        Section 신림보라매구간 = Section.builder()
                .line(신림선)
                .upStation(신림역)
                .downStation(보라매역)
                .distance(10L)
                .build();
        신림선.addSection(신림보라매구간);
        신림선 = lineRepository.save(신림선);

        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(22L)
                .build();
        lineService.addSection(신림선.getId(), 구간_생성_요청);


        //then
        LineResponse lineResponse = lineService.findLineById(신림선.getId());
        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
