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

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void saveLine() {
        // given
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 강남역.getId(), 역삼역.getId(), 10);

        // when
        lineService.saveLine(lineRequest);

        // then
        final List<Line> lines = lineRepository.findAll();
        final Line line = lines.stream()
                .filter(it -> it.getName().equals("2호선"))
                .findAny()
                .orElse(null);
        assertThat(line).isNotNull();

        final Section section = line.getSections().stream()
                .filter(it -> it.getUpStation().equals(강남역))
                .filter(it -> it.getDownStation().equals(역삼역))
                .findAny()
                .orElse(null);
        assertThat(section).isNotNull();
    }

    @Test
    void showLines() {
        // given
        createLine("2호선", "bg-green-600");

        // when
        final List<LineResponse> lineResponses = lineService.showLines();

        // then
        final List<String> lineNames = lineResponses.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        assertThat(lineNames).containsAnyOf("2호선");
    }

    @Test
    void findById() {
        // given
        final Line line = createLine("2호선", "bg-green-600");

        // when
        final LineResponse lineResponse = lineService.findById(line.getId());

        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @Test
    void updateLine() {
        // given
        final Line line = createLine("2호선", "bg-green-600");
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", null, null, 10);

        // when
        lineService.updateLine(line.getId(), lineRequest);

        // then
        final LineResponse lineResponse = lineService.findById(line.getId());
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    @Test
    void deleteLine() {
        // given
        final Line line = createLine("2호선", "bg-green-600");

        // when
        lineService.deleteLine(line.getId());

        // then
        assertThatThrownBy(() -> {
            lineService.findById(line.getId());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addSection() {
        // given
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final Line 이호선 = createLine("2호선", "bg-green-600");

        // when
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        final List<Section> sections = 이호선.getSections();
        assertThat(sections).containsAnyOf(new Section(이호선, 강남역, 역삼역, 10));
    }

    @Test
    void deleteSection() {
        // given
        final Station 강남역 = createStation("강남역");
        final Station 역삼역 = createStation("역삼역");
        final Station 선릉역 = createStation("선릉역");
        final Line 이호선 = createLine("2호선", "bg-green-600");
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        // then
        final List<Section> sections = 이호선.getSections();
        assertThat(sections).doesNotContain(new Section(이호선, 역삼역, 선릉역, 10));
    }

    private Station createStation(final String name) {
        return stationRepository.save(new Station(name));
    }

    private Line createLine(final String name, final String color) {
        return lineRepository.save(new Line(name, color));
    }
}
