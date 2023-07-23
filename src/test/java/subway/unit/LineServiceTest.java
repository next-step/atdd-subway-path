package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.repository.LineRepository;
import subway.repository.StationRepository;
import subway.service.LineService;
import subway.utils.LineAssertions;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 강남역;
    private Station 논현역;
    private Station 광교역;

    @BeforeEach
    void setUp() {
        this.강남역 = Station.builder()
            .name("강남역")
            .build();

        this.논현역 = Station.builder()
            .name("논현역")
            .build();

        this.광교역 = Station.builder()
            .name("광교역")
            .build();
    }

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 저장된_강남역 = stationRepository.save(강남역);
        Station 저장된_논현역 = stationRepository.save(논현역);
        Station 저장된_광교역 = stationRepository.save(광교역);

        Line 신분당선 = Line.builder()
            .name("신분당선")
            .color("bg-red-600")
            .distance(30L)
            .upStation(저장된_강남역)
            .downStation(저장된_논현역)
            .build();
        Line 저장후_신분당선 = lineRepository.save(신분당선);

        // when
        // lineService.addSection 호출
        SectionRequest request = SectionRequest.builder()
            .upStationId(저장된_논현역.getId())
            .downStationId(저장된_광교역.getId())
            .distance(10L)
            .build();
        Line 구간저장후_노선 = lineService.addSection(저장후_신분당선.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        LineAssertions.구간추가후_검증(구간저장후_노선);
    }
}
