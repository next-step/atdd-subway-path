package nextstep.subway.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.repository.StationRepository;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

//    @Autowired
//    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.getSections 메서드를 통해 검증
    }
}
