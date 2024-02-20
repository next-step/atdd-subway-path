package nextstep.subway.unit;

import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.dto.request.CreateSectionRequest;
import nextstep.subway.domain.line.repository.LineRepository;
import nextstep.subway.domain.line.service.SectionService;
import nextstep.subway.domain.station.domain.Station;
import nextstep.subway.domain.station.repository.StationRepository;
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
    private SectionService sectionService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("선릉역"));

        Line line = lineRepository.save(
                Line.builder()
                        .name("2호선")
                        .color("red")
                        .build()
        );

        // when
        // sectionService.createSection 호출 addSection 같은 역할
        CreateSectionRequest request = CreateSectionRequest.builder()
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(10)
                .build();

        sectionService.createSection(line.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(1);
    }
}
