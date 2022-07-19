package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void 구간_추가() {
        // given
        Station upStation = stationRepository.save(new Station("양재역"));
        Station downStation = stationRepository.save(new Station("교대역"));
        Line line = lineRepository.save(new Line("신분당선", "red"));

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 6);

        // when
        lineService.addSection(line.getId(), sectionRequest);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);

        Section addedSection = sections.get(0);
        assertThat(addedSection.getUpStationId()).isEqualTo(upStation.getId());
        assertThat(addedSection.getDownStationId()).isEqualTo(downStation.getId());
    }

    @Test
    void 구간_삭제() {
        // given
        Station upStation = stationRepository.save(new Station("양재역"));
        Station downStation = stationRepository.save(new Station("교대역"));
        Line line = lineRepository.save(new Line("신분당선", "red"));

        line.addSection(upStation.getId(), downStation.getId(), 6);

        // when
        lineService.deleteSection(line.getId(), downStation.getId());

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void 구간_삭제_종점이_아니면_예외() {
        // given
        Station upStation = stationRepository.save(new Station("양재역"));
        Station downStation = stationRepository.save(new Station("교대역"));
        Line line = lineRepository.save(new Line("신분당선", "red"));

        line.addSection(upStation.getId(), downStation.getId(), 6);

        // when + then
        assertThatThrownBy(() -> lineService.deleteSection(line.getId(), upStation.getId()))
                .isInstanceOf(CannotDeleteSectionException.class);
    }
}
