package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.fixture.ConstStation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Station 강남역 = stationRepository.save(ConstStation.강남역);
        Station 신논현역 = stationRepository.save(ConstStation.신논현역);
        Line 신분당선 = lineRepository.save(Line.of("신분당선", "bg-red-600"));

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), SectionRequest.of(강남역.getId(), 신논현역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> 신분당선_구간 = 신분당선.getSections();
        assertAll(
                () -> assertThat(신분당선_구간).hasSize(1),
                () -> assertThat(신분당선_구간.get(0)).extracting("upStation").isEqualTo(ConstStation.강남역),
                () -> assertThat(신분당선_구간.get(0)).extracting("downStation").isEqualTo(ConstStation.신논현역)
        );
    }
}
