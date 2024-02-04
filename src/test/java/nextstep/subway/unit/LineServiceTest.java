package nextstep.subway.unit;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.request.SectionRequest;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.SectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    SectionService sectionService;

    @Test
    void addSection() {

        // given
        // 초기 세팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));

        Line line = new Line("이호선", "초록색");
        Section section = new Section(line, 강남역, 역삼역, 10);
        line.addSection(section);
        lineRepository.save(line);

        // when
        // line.addSection() 호출
        sectionService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // then
        // 검증
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void deleteSection() {

        // given
        // 초기 세팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));

        Line line = new Line("이호선", "초록색");
        Section section = new Section(line, 강남역, 역삼역, 10);
        line.addSection(section);
        lineRepository.save(line);
        sectionService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // when
        sectionService.deleteSection(line.getId(), 선릉역.getId());

        // then
        // 검증
        assertThat(line.getSections()).hasSize(1);
    }
}
