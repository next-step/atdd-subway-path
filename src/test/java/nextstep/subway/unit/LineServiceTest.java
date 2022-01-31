package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.IllegalSectionArgumentException;
import org.junit.jupiter.api.Assertions;
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
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));


        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(수원역, 수원중앙역);
    }

    @Test
    void addFistSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));


        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(강남역.getId(), 수원역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest2);
        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(강남역, 수원역, 수원중앙역);
    }

    @Test
    void addSecondSectionByUpStation() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(수원역.getId(), 강남역.getId(), 9);
        lineService.addSection(line.getId(), sectionRequest2);
        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(수원역, 강남역, 수원중앙역);
    }

    @Test
    void addSecondSectionByDownStation() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(강남역.getId(), 수원중앙역.getId(), 9);
        lineService.addSection(line.getId(), sectionRequest2);
        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(수원역, 강남역, 수원중앙역);
    }

    @Test
    void addSecondSectionFailedByLongerDistance() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = new Line("간선", "blue");
        lineRepository.save(line);

        Station 수원역 = stationRepository.save(new Station("수원역"));
        Station 수원중앙역 = stationRepository.save(new Station("수원중앙역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(강남역.getId(), 수원중앙역.getId(), 10);
        // then
        // line.getSections 메서드를 통해 검증
        Assertions.assertThrows(
                IllegalSectionArgumentException.class,
                () -> lineService.addSection(line.getId(), sectionRequest2));
    }
}
