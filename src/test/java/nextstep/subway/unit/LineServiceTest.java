package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private Station 군자역;
    private Station 아차산역;
    private Station 광나루역;
    private Line 오호선;

    @BeforeEach
    void setup() {
        군자역 = stationRepository.save(new Station("군자역"));
        아차산역 = stationRepository.save(new Station("아차산역"));
        광나루역 = stationRepository.save(new Station("광나루역"));
        오호선 = new Line("5호선", "보라색");
        오호선.getSections().add(Section.of(오호선, 군자역, 아차산역, 10));
        lineRepository.save(오호선);
    }

    @DisplayName("마지막 구간에 구간 추가")
    @Test
    void addSection() {
        // given
        int distance = 10;
        SectionRequest request = new SectionRequest(아차산역.getId(), 광나루역.getId(), distance);

        // when
        lineService.addSection(오호선.getId(), request);

        // then
        List<Section> sections = 오호선.getSections();
        Section lastSection = sections.get(sections.size() - 1);
        assertThat(lastSection.getUpStation()).isEqualTo(아차산역);
        assertThat(lastSection.getDownStation()).isEqualTo(광나루역);
        assertThat(lastSection.getDistance()).isEqualTo(distance);
    }

    @DisplayName("중간 역을 제거")
    @Test
    void removeSection() {
        // given
        int distance = 5;
        SectionRequest request = new SectionRequest(군자역.getId(), 광나루역.getId(), distance);
        lineService.addSection(오호선.getId(), request);

        // when
        lineService.deleteSection(오호선.getId(), 광나루역.getId());

        // then
        LineResponse line = lineService.findLine(오호선.getId());
        List<StationResponse> stations = line.getStations();

        assertThat(stations).hasSize(2);
        assertThat(stations.get(0).getName()).isEqualTo(군자역.getName());
        assertThat(stations.get(1).getName()).isEqualTo(아차산역.getName());
    }
}
