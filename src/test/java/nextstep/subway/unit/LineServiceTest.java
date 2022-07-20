package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 영등포역;
    private Station 신도림역;
    private Station 구로역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    void setUp() {
        영등포역 = stationRepository.save(new Station("영등포역"));
        신도림역 = stationRepository.save(new Station("신도림역"));
        구로역 = stationRepository.save(new Station("구로역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
    }

    @Test
    void addSection() {
        // given
        Line 일호선 = lineRepository.save(createLine("1호선", "blue", 영등포역, 신도림역));

        // when
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 구로역.getId(), 20));

        // then
        LineResponse lineResponse = lineService.findById(일호선.getId());
        assertAll(
                () -> assertThat(일호선.getSections().size()).isEqualTo(2),
                () -> assertThat(lineResponse.getStations()).extracting("name")
                        .containsExactly(영등포역.getName(), 신도림역.getName(), 구로역.getName())
        );
    }

    @Test
    void showLines() {
        // given
        Line 일호선 = lineRepository.save(createLine("1호선", "blue", 영등포역, 신도림역));
        Line 이호선 = lineRepository.save(createLine("2호선", "green", 역삼역, 선릉역));

        // when
        List<LineResponse> allLines = lineService.showLines();

        // then
        assertAll(
                () -> assertThat(allLines)
                        .hasSize(2),
                () -> assertThat(allLines)
                        .extracting("name").containsOnlyOnce(일호선.getName(), 이호선.getName()),
                () -> assertThat(allLines)
                        .extracting("color").containsOnlyOnce(일호선.getColor(), 이호선.getColor())
        );
    }

    @Test
    void updateLine() {
        // given
        String newLineName = "1호선";
        String newLineColor = "blue";
        Line 이호선 = lineRepository.save(createLine("2호선", "green", 역삼역, 선릉역));

        LineRequest lineRequest = new LineRequest();
        lineRequest.setName(newLineName);
        lineRequest.setColor(newLineColor);

        // when
        lineService.updateLine(이호선.getId(), lineRequest);

        // then
        LineResponse newResponse = lineService.findById(이호선.getId());
        assertAll(
                () -> assertThat(newResponse.getColor()).isEqualTo(newLineColor),
                () -> assertThat(newResponse.getName()).isEqualTo(newLineName)
        );
    }

    @Test
    void deleteSection() {
        // given
        Line 일호선 = lineRepository.save(createLine("1호선", "blue", 영등포역, 신도림역));
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 구로역.getId(), 20));

        // when
        lineService.deleteSection(일호선.getId(), 구로역.getId());

        // then
        Line line = lineService.findLineById(일호선.getId());
        assertAll(
                () -> assertThat(line.getStations()).hasSize(2),
                () -> assertThat(line.getStations()).containsExactly(영등포역, 신도림역)
        );
    }

    private Line createLine(String name, String color, Station upStation, Station downStation) {
        Line line = new Line(name, color);
        line.addSection(upStation, downStation, 10);
        return line;
    }
}
