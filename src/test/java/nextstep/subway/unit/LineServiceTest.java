package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 강남역.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 양재역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 10);
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(신분당선.getId());
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }
}
