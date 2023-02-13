package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("구간 추가")
    void addSection() {
        // given
        Line line = lineRepository.save(new Line("7호선", "green darken-2"));
        Station upStation = stationRepository.save(new Station("상도역"));
        Station downStation = stationRepository.save(new Station("장승배기역"));

        // when
        lineService.addSection(line.getId(), createSectionRequest(upStation.getId(), downStation.getId()));

        // then
        Sections sections = line.getSections();
        Assertions.assertAll(
                () -> assertThat(sections).isNotNull(),
                () -> assertThat(sections.getStations()).contains(downStation, upStation)
        );
    }

    @Test
    @DisplayName("구간 삭제")
    void removeSection() {
        // given
        Line line = lineRepository.save(new Line("7호선", "green darken-2"));
        Station 장승배기역 = stationRepository.save(new Station("장승배기역"));
        Station 상도역 = stationRepository.save(new Station("상도역"));
        Station 숭실대입구역 = stationRepository.save(new Station("숭실대입구역"));

        lineService.addSection(line.getId(), createSectionRequest(장승배기역.getId(), 상도역.getId()));
        lineService.addSection(line.getId(), createSectionRequest(상도역.getId(), 숭실대입구역.getId()));

        // when
        lineService.deleteSection(line.getId(), 숭실대입구역.getId());

        // then
        Sections sections = line.getSections();
        
        assertThat(sections.getStations()).contains(장승배기역, 상도역);
    }

    private SectionRequest createSectionRequest(Long upStationId, Long downStationId) {
        return new SectionRequest(upStationId, downStationId, 10);
    }
}
