package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.unit.fixture.StationFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.unit.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.*;


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
        Line 신분당선 = lineRepository.save(new Line("신분당선", "RED"));
        Station 광교역 = stationRepository.save(광교);
        Station 광교중앙역 = stationRepository.save(광교중앙);

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 5));

        // then
        assertThat(신분당선.getStations()).containsExactly(광교역, 광교중앙역);
    }

    @Test
    void deleteSection() {
        // given
        Line 신분당선 = lineRepository.save(new Line("신분당선", "RED"));
        Station 광교역 = stationRepository.save(광교);
        Station 광교중앙역 = stationRepository.save(광교중앙);
        Station 상현 = stationRepository.save(StationFixture.상현);
        lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 5));
        lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현.getId(), 5));

        // when
        lineService.deleteSection(신분당선.getId(), 광교중앙역.getId());

        // then
        assertThat(신분당선.getStations()).containsExactly(광교역, 상현);
    }
}
