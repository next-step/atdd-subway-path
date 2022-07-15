package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private LineService target;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Test
    void deleteSectionFail_LineNotExists() {
        // given

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.deleteSection(1L, 2L));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSectionFail_NotLastDownStation() {
        // given
        final Station savedUpStation = stationRepository.save(new Station("station"));
        final Station savedDownStation = stationRepository.save(new Station("station"));
        final LineResponse lineResponse = target.saveLine(new LineRequest("name", "color", savedUpStation.getId(), savedDownStation.getId(), 3));

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.deleteSection(lineResponse.getId(), savedUpStation.getId()));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSectionSuccess() {
        // given
        final Station savedUpStation = stationRepository.save(new Station("station"));
        final Station savedDownStation = stationRepository.save(new Station("station"));
        final LineResponse lineResponse = target.saveLine(new LineRequest("name", "color", savedUpStation.getId(), savedDownStation.getId(), 3));

        // when
        target.deleteSection(lineResponse.getId(), savedDownStation.getId());

        // then
        assertThat(target.findById(lineResponse.getId()).getStations()).isEmpty();
    }

    @Test
    void saveLineSuccess_NotSection() {
        // given

        // when
        final LineResponse result = target.saveLine(new LineRequest("name", "color"));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isEmpty();
    }

    @Test
    void saveLineSuccess_WithSection() {
        // given
        final Station savedUpStation = stationRepository.save(new Station("station"));
        final Station savedDownStation = stationRepository.save(new Station("station"));

        // when
        final LineResponse result = target.saveLine(new LineRequest("name", "color", savedUpStation.getId(), savedDownStation.getId(), 3));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isNotEmpty();
    }

    @Test
    void addSectionSuccess() {
        // given
        final Line line = new Line("name", "color");
        final Line savedLine = lineRepository.save(line);

        final Station savedUpStation = stationRepository.save(new Station("station"));
        final Station savedDownStation = stationRepository.save(new Station("station"));

        // when
        target.addSection(savedLine.getId(), new SectionRequest(savedUpStation.getId(), savedDownStation.getId(), 10));

        // then
        assertThat(savedLine.getSections()).isNotEmpty();
    }

    @Test
    void addSectionFail_LineNotFound() {
        // given
        final Line line = new Line("name", "color");

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.addSection(1L, new SectionRequest()));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateLineSuccess() {
        // given
        final Line line = new Line("name", "color");
        final Line savedLine = lineRepository.save(line);

        // when
        target.updateLine(savedLine.getId(), new LineRequest(null, null));

        // then
        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    void updateLineFail_LineNotFound() {
        // given

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.updateLine(1L, new LineRequest(null, null)));

        // then
        assertThat(result).isNotNull();
    }

}
