package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    private static final int DEFAULT_DISTANCE = 5;
    private static final String DEFAULT_LINE_COLOR = "bg-green-600";
    private static final String FIRST_LINE_NAME = "1호선";
    private static final String SECOND_LINE_NAME = "2호선";

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @DisplayName("노선을 저장하다.")
    @Test
    void saveLine() {
        // given
        LineRequest request = createLineRequest(FIRST_LINE_NAME);

        // when
        LineResponse lineResponse = lineService.saveLine(request);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(request.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(request.getColor())
        );
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void showLines() {
        // given
        lineService.saveLine(createLineRequest(FIRST_LINE_NAME));
        lineService.saveLine(createLineRequest(SECOND_LINE_NAME));

        // when
        List<LineResponse> lines = lineService.showLines();

        // then
        assertThat(lines).hasSize(2);
    }

    @DisplayName("단일 노선을 조회한다")
    @Test
    void findById() {
        // given
        LineResponse lineResponse = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));

        // when
        LineResponse findLine = lineService.findById(lineResponse.getId());

        // then
        assertAll(
                () -> assertThat(findLine.getId()).isEqualTo(lineResponse.getId()),
                () -> assertThat(findLine.getName()).isEqualTo(lineResponse.getName()),
                () -> assertThat(findLine.getColor()).isEqualTo(lineResponse.getColor())
        );
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void updateLine() {
        // given
        String updateName = "분당선";

        LineResponse lineResponse = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));
        LineRequest updateLineRequest = createLineRequest(updateName);

        // when
        lineService.updateLine(lineResponse.getId(), updateLineRequest);
        LineResponse updateLineResponse = lineService.findById(lineResponse.getId());

        // then
        assertThat(updateLineResponse.getName()).isEqualTo(updateName);
    }

    @DisplayName("선택한 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineResponse lineResponse = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));

        // when
        lineService.deleteLine(lineResponse.getId());
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).hasSize(0);
    }

    @DisplayName("구간을 추가하다.")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = lineRepository.save(new Line("2호선", "bg-red-600"));
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));

        SectionRequest request = createSectionRequest(upStation, downStation);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(1);
    }

    private LineRequest createLineRequest(String name) {
        return createLineRequest(name, DEFAULT_LINE_COLOR);
    }

    private LineRequest createLineRequest(String name, String color) {
        LineRequest request = new LineRequest();
        request.setName(name);
        request.setColor(color);
        return request;
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation) {
        return createSectionRequest(upStation, downStation, DEFAULT_DISTANCE);
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation, int distance) {
        SectionRequest request = new SectionRequest();
        request.setUpStationId(upStation.getId());
        request.setDownStationId(downStation.getId());
        request.setDistance(distance);
        return request;
    }
}
