package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SubwayLineUpdateRequest;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.repository.SubwayLineRepository;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.SubwayLineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private SubwayLineRepository lineRepository;

    @Autowired
    private SectionService sectionService;

    @Test
    @DisplayName("지하철 노선 구간을 등록한다")
    void addSection() {
        // given
        var line = saveSubwayLine();

        // when
        var newDownStation = stationRepository.save(new Station(SEOUL_STATION));
        var sectionRequest = new SectionRequest.SectionRequestBuilder()
                .distance(10L)
                .upStationId(line.getDownStation().getId())
                .downStationId(newDownStation.getId())
                .build();
        sectionService.save(line.getId(), sectionRequest);

        // then
        assertThat(line.getSections().getStations().size()).isEqualTo(3);
    }

    private SubwayLine saveSubwayLine() {
        var upStation = stationRepository.save(new Station(GANGNAM_STATION));
        var downStation = stationRepository.save(new Station(PANGYO_STATION));
        var section = new SectionBuilder()
                .distance(10L)
                .upStation(upStation)
                .downStation(downStation)
                .build();
        var line = new SubwayLineBuilder()
                .name(LINE_SINBUNDANG)
                .color(COLOR_RED)
                .section(section)
                .build();
        return lineRepository.save(line);

    }
}
