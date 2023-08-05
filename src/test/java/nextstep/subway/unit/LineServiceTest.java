package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));

        Station addStation = stationRepository.save(new Station("판교역"));
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "노랑", upStation.getId(), downStation.getId(), 10));

        // when
        lineService.addSection(lineResponse.getId(), new SectionRequest(upStation.getId(), addStation.getId(), 2));
        Line line = lineRepository.findById(lineResponse.getId()).get();
        // then

        assertThat(line.getSections().isExistedUpAndDownStation(upStation,addStation))
                .isEqualTo(true);
    }

    @Test
    void deleteSection() {
        // given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));
        Station addStation = stationRepository.save(new Station("판교역"));
        Station addStation2 = stationRepository.save(new Station("청계산입구역"));

        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "노랑", upStation.getId(), downStation.getId(), 10));
        lineService.addSection(lineResponse.getId(), new SectionRequest(upStation.getId(), addStation.getId(), 2));
        lineService.addSection(lineResponse.getId(), new SectionRequest(downStation.getId(), addStation2.getId(), 5));

        // when
        lineService.deleteSection(lineResponse.getId(), downStation.getId());
        Line line = lineRepository.findById(lineResponse.getId()).get();

        // then
        assertThat(line.getSections().size()).isEqualTo(2);

        Section findSection = line.getSections().findSectionByUpAndDownStation(addStation, addStation2);
        assertThat(findSection.getDistance()).isEqualTo(13);

    }
}
