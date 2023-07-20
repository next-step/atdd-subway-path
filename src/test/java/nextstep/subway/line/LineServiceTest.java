package nextstep.subway.line;

import static common.Constants.강남역;
import static common.Constants.분당선;
import static common.Constants.빨강색600;
import static common.Constants.신논현역;
import static common.Constants.신분당선;
import static common.Constants.파랑색600;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private final StationRepository stationRepository;
    @Autowired
    private final LineRepository lineRepository;
    @Autowired
    private final LineService lineService;

    private Station gangnamStation;
    private Station sinnonhyeonStation;

    @Autowired
    public LineServiceTest(final StationRepository stationRepository,
        final LineRepository lineRepository, final LineService lineService) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.lineService = lineService;
    }

    @BeforeEach
    void init() {
        gangnamStation = stationRepository.save(new Station(강남역));
        sinnonhyeonStation = stationRepository.save(new Station(신논현역));
    }

    @Test
    void saveLine() {
        LineRequest lineRequest = new LineRequest(신분당선, 빨강색600, gangnamStation.getId(),
            sinnonhyeonStation.getId(), 10);

        LineResponse lineResponse = lineService.saveLine(lineRequest);

        assertAll(
            () -> assertThat(lineResponse.getId()).isNotNull(),
            () -> assertThat(lineResponse.getName()).isEqualTo(신분당선),
            () -> assertThat(lineResponse.getColor()).isEqualTo(빨강색600),
            () -> assertThat(lineResponse.getStations()).containsExactly(
                new StationResponse(1L, 강남역),
                new StationResponse(2L, 신논현역))
        );
    }

    @Test
    void findLine() {
        Section section = new Section(gangnamStation, sinnonhyeonStation, 10);
        Line line = lineRepository.save(new Line(신분당선, 빨강색600, section));

        LineResponse lineResponse = lineService.findLine(line.getId());

        assertThat(lineResponse).isEqualTo(new LineResponse(line));
    }

    @Test
    void update() {
        Section section = new Section(gangnamStation, sinnonhyeonStation, 10);
        Line line = lineRepository.save(new Line(신분당선, 빨강색600, section));
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(분당선, 파랑색600);

        lineService.update(line.getId(), lineUpdateRequest);

        assertThat(line.getName()).isEqualTo(분당선);
        assertThat(line.getColor()).isEqualTo(파랑색600);
    }

    @Test
    void deleteLineById() {
        Section section = new Section(gangnamStation, sinnonhyeonStation, 10);
        Line line = lineRepository.save(new Line(신분당선, 빨강색600, section));

        lineService.deleteLineById(line.getId());

        Optional<Line> stu = lineRepository.findById(line.getId());
        assertThat(stu).isEmpty();
    }
}