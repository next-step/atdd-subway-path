package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @ParameterizedTest
    @CsvSource(value = {"에버라인:red:에버라인:red", "에버라인::에버라인:yellow", ":red:분당선:red"}, delimiter = ':')
    void updateLine(String lineName, String color, String expectLineName, String expectColor) {
        // given
        final Line line = new Line(lineName, color);
        lineRepository.save(line);

        // when
        lineService.updateLine(line.getId(), LineRequest.builder().color(expectColor).name(expectLineName).build());

        // then
        assertAll(
            () -> assertThat(line.getName()).isEqualTo(expectLineName),
            () -> assertThat(line.getColor()).isEqualTo(expectColor)
                 );
    }
}
