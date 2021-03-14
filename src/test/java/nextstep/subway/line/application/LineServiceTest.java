package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Station 태평역 = stationRepository.save(new Station("태평역"));
        Station 가천대역 = stationRepository.save(new Station("가천대역"));
        Station 복정역 = stationRepository.save(new Station("복정역"));
        LineResponse 분당선 = lineService.saveLine(new LineRequest("분당선", "yellow", 태평역.getId(), 가천대역.getId(), 3));

        // when
        // lineService.addSection 호출
        lineService.addSection(분당선.getId(), new SectionRequest(
            가천대역.getId(),
            복정역.getId(),
            3
        ));

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineService.findLineById(분당선.getId());
        assertThat(
            line.getSections()
                .stream()
                .flatMap(section ->
                    Stream.of(section.getUpStation(), section.getDownStation())
                )
                .distinct()
                .collect(Collectors.toList())
        ).contains(태평역, 가천대역, 복정역);
    }
}
