package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("구간 생성")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 논현역 = stationRepository.save(Station.of("논현역"));
        Station 신논현역 = stationRepository.save(Station.of("신논현역"));
        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red"));

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), SectionRequest.of(논현역.getId(), 신논현역.getId(), 5));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(신분당선.sections().size()).isEqualTo(1);
        assertThat(신분당선.sections().stream().findFirst().orElseThrow().getUpStation().getName()).isEqualTo("논현역");
        assertThat(신분당선.sections().stream().findFirst().orElseThrow().getDownStation().getName()).isEqualTo("신논현역");
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 논현역 = stationRepository.save(Station.of("논현역"));
        Station 신논현역 = stationRepository.save(Station.of("신논현역"));
        Station 강남역 = stationRepository.save(Station.of("강남역"));
        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red"));

        // when
        // 구간 등록 및 삭제
        lineService.addSection(신분당선.getId(), SectionRequest.of(논현역.getId(), 신논현역.getId(), 5));
        lineService.addSection(신분당선.getId(), SectionRequest.of(신논현역.getId(), 강남역.getId(), 5));
        lineService.deleteSection(신분당선.getId(), 강남역.getId());

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(신분당선.sections().size()).isEqualTo(1);
        assertThat(신분당선.sections().stream().findFirst().orElseThrow().getUpStation().getName()).isEqualTo("논현역");
        assertThat(신분당선.sections().stream().findFirst().orElseThrow().getDownStation().getName()).isEqualTo("신논현역");
    }
    
}
