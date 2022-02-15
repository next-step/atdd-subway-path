package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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

    /**
     * scenario LineService#addSection 노선 id, 상행역 id, 하행역 id, 구간 거리 를 통해 노선에 구간을 등록한다
     * given lineRepository, stationService stub 설정을 통해 초기값 셋팅
     * when lineService.addSection 호출
     * then line.findLineById 메서드를 통해 검증
     */
    @Test
    void addSection() {
        // given
        Line 분당선 = new Line("신분당선", "red-100");
        lineRepository.save(분당선);

        Station 강남역 = new Station("강남역");
        stationRepository.save(강남역);

        Station 역삼역 = new Station("역삼역");
        stationRepository.save(역삼역);

        // when
        lineService.addSection(분당선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        Line line = lineRepository.findById(분당선.getId()).orElseThrow(RuntimeException::new);
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
