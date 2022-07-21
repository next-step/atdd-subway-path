package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
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

import static nextstep.subway.utils.LineTestSources.*;
import static nextstep.subway.utils.StationTestSources.*;
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
                () -> target.deleteSection(lineId, downStationId));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSectionFail_NotLastDownStation() {
        // given
        final Station savedUpStation = stationRepository.save(upStation());
        final Station savedDownStation = stationRepository.save(downStation());
        final LineResponse lineResponse = target.saveLine(lineRequest(savedUpStation.getId(), savedDownStation.getId()));

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
        final Station savedUpStation = stationRepository.save(upStation());
        final Station savedDownStation = stationRepository.save(downStation());
        final Station savedNewDownStation = stationRepository.save(downStation());
        final LineResponse lineResponse = target.saveLine(lineRequest(savedUpStation.getId(), savedDownStation.getId()));
        target.addSection(lineResponse.getId(), new SectionRequest(savedDownStation.getId(), savedNewDownStation.getId(), 10));

        // when
        target.deleteSection(lineResponse.getId(), savedNewDownStation.getId());

        // then
        assertThat(target.findById(lineResponse.getId()).getStations()).hasSize(2);
    }

    @Test
    void deleteSectionFail_LastSection() {
        // given
        final Station savedUpStation = stationRepository.save(upStation());
        final Station savedDownStation = stationRepository.save(downStation());
        final LineResponse lineResponse = target.saveLine(lineRequest(savedUpStation.getId(), savedDownStation.getId()));

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.deleteSection(lineResponse.getId(), savedDownStation.getId()));

        // then
        assertThat(result).hasMessageContaining("Last section cannot be removed");
    }

    @Test
    void saveLineSuccess_NotSection() {
        // given

        // when
        final LineResponse result = target.saveLine(lineRequest());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isEmpty();
    }

    @Test
    void saveLineSuccess_WithSection() {
        // given
        final Station savedUpStation = stationRepository.save(upStation());
        final Station savedDownStation = stationRepository.save(downStation());

        // when
        final LineResponse result = target.saveLine(lineRequest(savedUpStation.getId(), savedDownStation.getId()));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isNotEmpty();
    }

    @Test
    void addSectionSuccess() {
        // given
        final Line savedLine = lineRepository.save(line());

        final Station savedUpStation = stationRepository.save(upStation());
        final Station savedDownStation = stationRepository.save(downStation());

        // when
        target.addSection(savedLine.getId(), sectionRequest(savedUpStation.getId(), savedDownStation.getId()));

        // then
        assertThat(savedLine.getStations()).isNotEmpty();
    }

    @Test
    void addSectionFail_LineNotFound() {
        // given

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.addSection(lineId, new SectionRequest()));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateLineSuccess() {
        // given
        final Line line = line();
        final Line savedLine = lineRepository.save(line);

        // when
        target.updateLine(savedLine.getId(), lineRequest());

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
                () -> target.updateLine(lineId, lineRequest()));

        // then
        assertThat(result).isNotNull();
    }

}
