package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.unit.fixture.StationFixture.*;
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

    private Line 신분당선;
    private Station 광교역;
    private Station 광교중앙역;
    private Station 상현역;


    @BeforeEach
    void initSection() {
        신분당선 = lineRepository.save(new Line("신분당선", "RED"));
        광교역 = stationRepository.save(광교);
        광교중앙역 = stationRepository.save(광교중앙);
        상현역 = stationRepository.save(상현);
        lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 5));
    }

    @Test
    void addSection() {
        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), 6));

        // then
        assertThat(신분당선.getStations()).containsExactly(광교역, 광교중앙역, 상현역);
    }

    @Test
    void deleteSection() {
        // given
        lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), 6));

        // when
        lineService.deleteSection(신분당선.getId(), 광교중앙역.getId());

        // then
        assertThat(신분당선.getStations()).contains(광교역, 상현역);
    }
}
