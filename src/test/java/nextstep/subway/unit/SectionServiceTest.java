package nextstep.subway.unit;

import nextstep.subway.applicaion.SectionService;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class SectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private SectionService sectionService;

    @Test
    void addSection() {
        // given
        final Station 기흥역 = new Station("기흥역");
        final Station 신갈역 = new Station("신갈역");
        stationRepository.save(기흥역);
        stationRepository.save(신갈역);

        final Line line = new Line("분당선", "yellow");
        lineRepository.save(line);

        // when
        sectionService.addSection(line.getId(), new SectionRequest(기흥역.getId(), 신갈역.getId(), 10));

        // then
        assertAll(
            () -> assertThat(line.getSections().size()).isEqualTo(1),
            () -> assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역")
                 );
    }

    @Test
    void deleteSection() {
        // given
        final Station 기흥역 = new Station("기흥역");
        final Station 신갈역 = new Station("신갈역");
        final Station 정자역 = new Station("정자역");
        stationRepository.save(기흥역);
        stationRepository.save(신갈역);
        stationRepository.save(정자역);

        final Line line = new Line("분당선", "yellow");
        lineRepository.save(line);

        sectionService.addSection(line.getId(), new SectionRequest(기흥역.getId(), 신갈역.getId(), 10));
        sectionService.addSection(line.getId(), new SectionRequest(신갈역.getId(), 정자역.getId(), 10));

        // when
        sectionService.deleteSection(line.getId(), 정자역.getId());

        // then
        assertAll(
            () -> assertThat(line.getSections().size()).isEqualTo(1),
            () -> assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역")
                 );
    }
}
