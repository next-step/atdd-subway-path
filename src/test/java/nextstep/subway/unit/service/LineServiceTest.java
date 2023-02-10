package nextstep.subway.unit.service;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Service] 구간 테스트 without Mock")
@SpringBootTest
@Transactional
public class LineServiceTest {

    private Station 강남역;
    private Station 역삼역;
    private Line line;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");

        stationRepository.saveAll(List.of(강남역, 역삼역));
        line = lineRepository.save(new Line("2호선", "green"));
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        SectionRequest request = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);

        lineService.addSection(line.getId(), request);

        assertThat(line.getStations()).containsExactly(강남역, 역삼역);
    }
}
