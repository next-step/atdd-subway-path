package nextstep.subway.unit;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.request.SectionRequest;
import nextstep.subway.domain.response.LineResponse;
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
        lineService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // then
        assertThat(line.getSections().getSections()).hasSize(2);
    }

    @Test
    void deleteSection() {
        // given
        lineService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // when
        sectionService.deleteSection(line.getId(), 선릉역.getId());

        // then
        assertThat(line.getSections().getSections()).hasSize(1);
    }

    @Test
    void getSection() {
        // given
        int distance = 10;
        LineResponse lineResponse = lineService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), distance));

        // when
        lineResponse.getStations();

        // then
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(line.getId()),
                () -> assertThat(lineResponse.getStations().stream().map(station -> station.getId())).contains(역삼역.getId(), 선릉역.getId())
        );
    }
}
