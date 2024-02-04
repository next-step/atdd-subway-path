package nextstep.subway.unit;

import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.application.LineService;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 강남_선릉_노선;

    @BeforeEach
    void init() {
        강남역 = new Station(1L, "강남역");
        선릉역 = new Station(2L, "선릉역");
        삼성역 = new Station(3L, "삼성역");
        강남_선릉_노선 = new Line(1L, "노선", "red", 강남역, 선릉역, 10);
    }
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        stationRepository.save(강남역);
        stationRepository.save(선릉역);
        stationRepository.save(삼성역);
        final Line line = new Line( "노선", "red", 강남역, 선릉역, 10);
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findLineById(line.getId());
        assertThat(lineResponse.getStations()).hasSize(3);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("선릉역");
        assertThat(lineResponse.getStations().get(2).getName()).isEqualTo("삼성역");
    }
}
