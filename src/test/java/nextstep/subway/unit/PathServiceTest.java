package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.dto.PathDto;
import nextstep.subway.path.service.PathService;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private SectionService sectionService;

    @Autowired
    private PathService pathService;

    private Station 강남역, 역삼역, 잠실역;
    private Line line;

    @BeforeEach
    void setUpStation() {
        강남역 = stationRepository.save(createStation("강남역"));
        역삼역 = stationRepository.save(createStation("역삼역"));
        잠실역 = stationRepository.save(createStation("잠실역"));
        line = lineRepository.save(line(강남역, 역삼역, 2));
        sectionService.addSection(line.getId(), sectionDto(역삼역.getId(), 잠실역.getId(), 3));
    }

    @Test
    void findPath() {
        // given : 선행조건 기술

        // when : 기능 수행
        PathDto pathDto = pathService.findPath(강남역.getId(), 잠실역.getId());

        // then : 결과 확인
        assertThat(pathDto.getStationDtos()).hasSize(3)
                .extracting("name")
                .containsExactly("강남역", "역삼역", "잠실역");
        assertThat(pathDto.getDistance()).isEqualTo(5);
    }

    private Station createStation(String name) {
        return new Station(name);
    }

    private Line line(Station upStation, Station downStation, int distance) {
        return Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .section(section(upStation, downStation, distance))
                .build();
    }

    private Section section(Station upStation, Station downStation, int distance) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    private SectionDto sectionDto(Long upStationId, Long downStationId, int distance) {
        return SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }
}