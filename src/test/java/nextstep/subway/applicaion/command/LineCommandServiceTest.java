package nextstep.subway.applicaion.command;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.UpdateLineRequest;
import nextstep.subway.applicaion.query.LineQueryService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static nextstep.subway.acceptance.step_feature.LineStepFeature.신분당선_색;
import static nextstep.subway.acceptance.step_feature.LineStepFeature.신분당선_이름;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 서비스 로직 검증")
@SpringBootTest
@Transactional
class LineCommandServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineCommandService lineCommandService;
    @Autowired
    private LineQueryService lineQueryService;

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        stationRepository.save(강남역);
        stationRepository.save(정자역);
        신분당선 = lineRepository.save(Line.of(신분당선_이름, 신분당선_색, 강남역, 정자역, 100));
    }

    @DisplayName("노선을 추가할 수 있다")
    @Test
    void saveLine() {
        // given
        String lineName = "노선";
        LineRequest request = LineRequest.of(lineName, "blue", 강남역.getId(), 정자역.getId(), 100);

        // when
        LineResponse response = lineCommandService.saveLine(request);

        // then
        assertThat(response.getName()).isEqualTo(lineName);
    }

    @DisplayName("구간을 추가할 수 있다")
    @Test
    void addSection() {
        // given
        stationRepository.save(판교역);
        SectionRequest sectionRequest = SectionRequest.of(판교역.getId(), 정자역.getId(), 10);

        // when
        lineCommandService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line line = lineQueryService.findLineById(신분당선.getId());
        assertThat(line.getId()).isEqualTo(신분당선.getId());
        assertThat(line.getStations()).containsExactly(강남역, 판교역, 정자역);
    }

    @DisplayName("구간을 삭제할 수 있다")
    @Test
    void deleteSection() {
        // given
        stationRepository.save(판교역);
        SectionRequest sectionRequest = SectionRequest.of(판교역.getId(), 정자역.getId(), 10);
        lineCommandService.addSection(신분당선.getId(), sectionRequest);

        // when
        lineCommandService.deleteSection(신분당선.getId(), 정자역.getId());

        // then
        Line line = lineRepository.getById(신분당선.getId());
        assertThat(line.getStations()).contains(강남역, 판교역);
    }

    @DisplayName("노선의 이름과 색을 변경할 수 있다")
    @Test
    void updateLine() {
        // given
        String name = "구분당선";
        String color = "blue";
        UpdateLineRequest request = UpdateLineRequest.of(color, name);

        // when
        lineCommandService.updateLine(신분당선.getId(), request);

        // then
        Line line = lineRepository.getById(신분당선.getId());
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @DisplayName("노선을 삭제할 수 있다")
    @Test
    void deleteLine() {
        // when
        lineCommandService.deleteLine(신분당선.getId());

        // then
        Optional<Line> line = lineRepository.findById(신분당선.getId());
        assertThat(line.isPresent()).isFalse();
    }

}