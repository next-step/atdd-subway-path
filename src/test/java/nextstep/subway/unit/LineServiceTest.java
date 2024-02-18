package nextstep.subway.unit;

import nextstep.subway.application.LineService;
import nextstep.subway.application.dto.SectionCreateRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));

        Line line = new Line("이호선", "green");
        line.addSection(강남역, 역삼역, 10);
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(역삼역.getId(), 선릉역.getId(), 10);
        lineService.addSection(line.getId(), sectionCreateRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void deleteSection() {
        // given
        // stationRepository 와 LineRepository 를 사용하여 역과 노선을 초기 세팅한다.
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));

        Line line = new Line("이호선", "green");
        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 10);
        lineRepository.save(line);

        // when
        // LineService 의 deleteSection 을 호출했을 때
        lineService.deleteSection(line.getId(), 선릉역.getId());

        // then
        // line.getSections 메서드를 통해 검증한다.
        assertThat(line.getSections()).hasSize(1);
    }
}
