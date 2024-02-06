package nextstep.subway.unit;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.request.SectionRequest;
import nextstep.subway.domain.response.SectionResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    SectionService sectionService;
    @Autowired
    LineService lineService;

    private Line line;
    Station 강남역, 역삼역, 선릉역;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));

        line = new Line("이호선", "초록색");
        Section section = new Section(line, 강남역, 역삼역, 10);
        line.addSection(section);
        line = lineRepository.save(line);
    }

    @Test
    void addSection() {
        // given

        // when
        // line.addSection() 호출
        sectionService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // then
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void deleteSection() {
        // given
        sectionService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // when
        sectionService.deleteSection(line.getId(), 선릉역.getId());

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void getSection() {
        // given
        int distance = 10;
        SectionResponse sectionResponse = sectionService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), distance));

        // when
        SectionResponse response = lineService.findSection(line.getId(), sectionResponse.getId());

        // then
        assertAll(
                () -> assertThat(response.getLine()).isEqualTo(line),
                () -> assertThat(response.getUpStation()).isEqualTo(역삼역),
                () -> assertThat(response.getDownStation()).isEqualTo(선릉역),
                () -> assertThat(response.getDistance()).isEqualTo(distance)
        );
    }
}
