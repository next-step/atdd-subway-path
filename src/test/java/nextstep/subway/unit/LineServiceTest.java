package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
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

    @Test
    void addSection() {
        // given
        Line line = new Line();
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Station 잠실역 = stationRepository.save(new Station("잠실역"));

        line.addSection(new Section(강남역, 선릉역, 7));
        lineRepository.save(line);

        // when
        lineService.addSection(line.getId(), new SectionRequest(선릉역.getId(), 잠실역.getId(), 3));

        // then
        assertThat(line.getSectionList()).hasSize(2);
    }

    @Test
    void deleteSection() {
        // given
        Line line = new Line();
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Station 잠실역 = stationRepository.save(new Station("잠실역"));

        line.addSection(new Section(강남역, 선릉역, 7));
        line.addSection(new Section(선릉역, 잠실역, 3));
        lineRepository.save(line);

        // when
        lineService.deleteSection(line.getId(), 잠실역.getId());

        // then
        assertThat(line.getSectionList()).hasSize(1);
    }
}
