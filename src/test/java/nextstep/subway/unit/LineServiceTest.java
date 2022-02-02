package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.IllegalSectionArgumentException;
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

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));

        // when
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        assertThat(line.getStations()).containsExactly(수원역, 수원중앙역);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addFistSection() {
        // given
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest secondSectionRequest = new SectionRequest(강남역.getId(), 수원역.getId(), 10);
        lineService.addSection(line.getId(), secondSectionRequest);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 수원역, 수원중앙역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간을 상행으로 추가할 경우")
    @Test
    void addSecondSectionByUpStation() {
        // given
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest secondSectionRequest = new SectionRequest(수원역.getId(), 강남역.getId(), 9);
        lineService.addSection(line.getId(), secondSectionRequest);

        // then
        assertThat(line.getStations()).containsExactly(수원역, 강남역, 수원중앙역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간을 하행으로 추가할 경우")
    @Test
    void addSecondSectionByDownStation() {
        // given
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest secondSectionRequest = new SectionRequest(강남역.getId(), 수원중앙역.getId(), 9);
        lineService.addSection(line.getId(), secondSectionRequest);

        // then
        assertThat(line.getStations()).containsExactly(수원역, 강남역, 수원중앙역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간 추가시 distance가 기존의 구간보다 긴 경우 등록 실패")
    @Test
    void addSecondSectionFailedByLongDistance() {
        // given
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        SectionRequest secondSectionRequest = new SectionRequest(강남역.getId(), 수원중앙역.getId(), 10);
        Assertions.assertThrows(
                IllegalSectionArgumentException.class,
                () -> lineService.addSection(line.getId(), secondSectionRequest));
    }
}
