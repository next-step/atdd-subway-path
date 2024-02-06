package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.dto.SaveLineSectionCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = Line.create("신분당선", "bg-red-600", 강남역, 역삼역, 10);
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        SaveLineSectionCommand command = new SaveLineSectionCommand(line.getId(), 역삼역.getId(), 선릉역.getId(), 10);
        lineService.saveLineSection(command);

        // then
        // line.getSections 메서드를 통해 검증
        assertEquals(line.getSections().size(), 2);
    }
}
