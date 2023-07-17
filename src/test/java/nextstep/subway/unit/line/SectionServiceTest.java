package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.line.controller.dto.LineRequest;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.infra.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.controller.dto.SectionRequest;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.controller.dto.StationRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.infra.StationRepository;
import nextstep.subway.utils.DBCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;
    @Autowired
    private SectionService sectionService;
    private Station first;
    private Station second;
    private Station third;
    private Long lineId;

    @BeforeEach
    void init() {
        // 역 3개 생성 및 노선 1개 생성
        first = stationRepository.save(new Station("a"));
        second = stationRepository.save(new Station("b"));
        third = stationRepository.save(new Station("c"));
        LineResponse response = lineService.saveLine(new LineRequest("신분당선", "abc-color", first.getId(), second.getId(), 1));
        lineId = response.getId();
    }

    @Test
    void addSection() {
        // given
        // 요청 값 생성
        SectionRequest req = new SectionRequest(third.getId(), second.getId(), 4);

        // when
        // sectionService.addSection 호출
        sectionService.addSection(lineId, req);

        // then
        // lineService.getLine 메서드를 통해 검증
        Line line = lineService.getLine(lineId);
        assertThat(line.getSectionList()).hasSize(2);
        assertThat(line.getDistance()).isEqualTo(5);
    }

    @Test
    void deleteSection() {
        //given section 이 생성 되었을 때
        SectionRequest req = new SectionRequest(third.getId(), second.getId(), 4);
        sectionService.addSection(lineId, req);

        // when
        // sectionService.deleteSection 호출
        sectionService.deleteSection(lineId, third.getId());

        // then
        // lineService.getLine 메서드를 통해 검증
        Line line = lineService.getLine(lineId);
        assertThat(line.getSectionList()).hasSize(1);
        assertThat(line.getDistance()).isEqualTo(1);
    }
}
