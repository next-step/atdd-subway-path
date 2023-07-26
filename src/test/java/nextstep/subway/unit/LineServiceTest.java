package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        Station 첫번째역 = new Station(1L, "첫번째역");
        Station 두번째역 = new Station(2L, "두번째역");
        Station 세번째역 = new Station(3L, "세번째역");
        stationRepository.save(첫번째역);
        stationRepository.save(두번째역);
        stationRepository.save(세번째역);
        LineResponse lineResponse = lineService.saveLine(new LineRequest("첫번째노선", "BLUE", 1L, 2L, 10L));

        // when
        lineService.addSection(lineResponse.getId(), SectionRequest.builder().distance(10L).upStationId(2L).downStationId(3L).build());
        // then
        Line line = lineRepository.findById(lineResponse.getId()).orElseThrow(()-> new IllegalArgumentException("테스트에 노선 찾기 실패"));
        assertThat(line.getSections().getStations().stream().map(station -> station.getName()).collect(
            Collectors.toList())).containsExactly("첫번째역","두번째역","세번째역");
        // 여기 줄줄이 delimiter law 어겼음
    }
}
