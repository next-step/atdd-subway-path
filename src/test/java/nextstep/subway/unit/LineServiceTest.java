package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.domain.*;
import nextstep.subway.fixture.LineFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    void findByStationIds() {
        // given
        lineRepository.save(LineFixture.createLine(0L, 1L));
        lineRepository.save(LineFixture.createLine(1L, 2L));

        // when
        Lines lines = lineService.findByStationIds(List.of(1L, 2L));

        // then
        assertThat(lines.size()).isEqualTo(2);
    }
}
