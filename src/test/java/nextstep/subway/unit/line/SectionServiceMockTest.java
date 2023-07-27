package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.line.controller.dto.LineRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.infra.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.controller.dto.SectionRequest;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.controller.dto.StationRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.infra.StationRepository;
import nextstep.subway.station.service.StationService;
import nextstep.subway.unit.fake.LineMemoryRepository;
import nextstep.subway.unit.fake.SectionMemoryRepositoryStub;
import nextstep.subway.unit.fake.StationMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SectionServiceMockTest {
    private final LineRepository lineRepository = new LineMemoryRepository();
    private final StationRepository stationRepository = new StationMemoryRepository();
    private final StationService stationService = new StationService(stationRepository);
    private final LineService lineService = new LineService(stationService, lineRepository);
    private final SectionService sectionService = new SectionService(lineService, stationService, new SectionMemoryRepositoryStub());

    @BeforeEach
    void init() {
        // 역 3개 생성 및 노선 1개 생성
        stationRepository.save(new Station("a"));
        stationRepository.save(new Station("b"));
        stationRepository.save(new Station("c"));
        lineService.saveLine(new LineRequest("신분당선", "abc-color", 1L, 2L, 1));
    }
    @Test
    void addSection() {
        // given
        // 요청 값 생성
        SectionRequest req = new SectionRequest(3L, 2L, 4);

        // when
        // sectionService.addSection 호출
        sectionService.addSection(1L, req);

        // then
        // lineService.getLine 메서드를 통해 검증
        Line line = lineService.getLine(1L);
        assertThat(line.getSectionList()).hasSize(2);
        assertThat(line.getDistance()).isEqualTo(5);
    }

    @Test
    void deleteSection() {
        //given section 이 생성 되었을 때
        SectionRequest req = new SectionRequest(3L, 2L, 4);
        sectionService.addSection(1L, req);

        // when
        // sectionService.deleteSection 호출
        sectionService.deleteSection(1L, 3L);

        // then
        // lineService.getLine 메서드를 통해 검증
        Line line = lineService.getLine(1L);
        assertThat(line.getSectionList()).hasSize(1);
        assertThat(line.getDistance()).isEqualTo(1);
    }
}
