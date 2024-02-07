package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.dto.AddSectionCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.helper.fixture.StationFixture.*;
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
    @DisplayName("addSection을 호출하면 섹션이 추가된다.")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(강남역_엔티티);
        Station 역삼역 = stationRepository.save(역삼역_엔티티);
        Station 선릉역 = stationRepository.save(선릉역_엔티티);
        Line line = Line.create("신분당선", "bg-red-600", 강남역, 역삼역, 10);
        lineRepository.save(line);

        // 비교 대상
        int initialSectionSize = line.getSections().size();

        // when
        // lineService.addSection 호출
        AddSectionCommand command = new AddSectionCommand(
                line.getId(), 역삼역.getId(), 선릉역.getId(), 10
        );
        lineService.addSection(command);

        // then
        // line.getSections 메서드를 통해 검증
        assertEquals(line.getSections().size() - initialSectionSize, 1);
    }
}
