package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionService sectionService;

    private Station 당고개역, 이수역, 사당역;
    private Line line;
    private SectionDto sectionDto;

    @BeforeEach
    void setUpStation() {
        당고개역 = stationRepository.save(당고개역());
        이수역 = stationRepository.save(이수역());
        사당역 = stationRepository.save(사당역());
        line = lineRepository.save(line(당고개역, 이수역));
        sectionDto = sectionDto(이수역.getId(), 사당역.getId());
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        // when
        sectionService.addSection(line.getId(), sectionDto);

        // then
        assertThat(line.getSections()).hasSize(2)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "이수역"),
                        Tuple.tuple("이수역", "사당역")
                );
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given : 선행조건 기술
        sectionService.addSection(line.getId(), sectionDto);

        // when : 기능 수행
        sectionService.removeSection(line.getId(), 사당역.getId());

        // then : 결과 확인
        assertThat(line.getSections()).hasSize(1)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "이수역")
                );
    }

    private Line line(Station upStation, Station downStation) {
        return Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .section(section(upStation, downStation))
                .build();
    }

    private Station 당고개역() {
        return new Station("당고개역");
    }

    private Station 이수역() {
        return new Station("이수역");
    }

    private Station 사당역() {
        return new Station("사당역");
    }

    private Section section(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .build();
    }

    private SectionDto sectionDto(Long upStationId, Long downStationId) {
        return SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(10)
                .build();
    }
}
