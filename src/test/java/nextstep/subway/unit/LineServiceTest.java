package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Line 그린선 = lineRepository.save(new Line(1L, "2호선", "green"));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L,
                new SectionRequest(
                        1L, 2L, 10
                ));

        // then
        // line.sections 메서드를 통해 검증
        List<Section> sections = 그린선.sections();
        assertThat(sections).hasSize(1)
                .extracting("line.name", "upStation.name", "downStation.name", "distance")
                .containsExactly(
                        tuple("2호선", "강남역", "선릉역", 10)
                );
    }
}
