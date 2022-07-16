package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;


    private LineResponse lineResponse;

    @BeforeEach
    void setUp() {
        this.lineResponse = createLine("4호선", "blue", "중앙역", "한대앞역");
    }

    @Test
    @DisplayName("Line 목록 전체가 조회된다.")
    void showLinesTest() {
        // given
        createLine("2호선", "green", "사당역", "방배역");

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).hasSize(2);
        assertThat(lineResponses.get(0).getName()).isEqualTo("4호선");
        assertThat(lineResponses.get(1).getName()).isEqualTo("2호선");

    }

    @Test
    @DisplayName("Line 상세 정보가 조회된다.")
    void findByIdTest() {
        // when
        LineResponse findLineResponse = lineService.findById(lineResponse.getId());

        // then
        assertThat(lineResponse.getId()).isEqualTo(findLineResponse.getId());
        assertThat(lineResponse.getName()).isEqualTo(findLineResponse.getName());
        assertThat(lineResponse.getColor()).isEqualTo(findLineResponse.getColor());

    }

    @Test
    @DisplayName("Line 정보가 업데이트 된다.")
    void updateLineTest() {
        // when
        lineService.updateLine(lineResponse.getId(), new LineRequest("2호선", "green"));

        // then
        LineResponse updateLineResponse = lineService.findById(lineResponse.getId());
        assertThat(updateLineResponse.getName()).isEqualTo("2호선");
        assertThat(updateLineResponse.getColor()).isEqualTo("green");

    }

    @Test
    @DisplayName("Line이 삭제되며, 재조회시 예외가 발생한다.")
    void deleteLineTest() {
        // when
        lineService.deleteLine(lineResponse.getId());

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> lineService.findById(lineResponse.getId()));
    }

    @Test
    @DisplayName("구간이 추가된다.")
    void addSectionTest() {
        // when, then
        createLineAndAddSection("상록수역");
    }

    @Test
    @DisplayName("구간이 제거된다.")
    void deleteSectionTest() {
        // given
        LineResponse lineResponse = createLineAndAddSection("상록수역");

        // when
        lineService.deleteSection(lineResponse.getId(), getLastStationId(lineResponse.getStations()));

        // then
        LineResponse deleteSectionLineResponse = lineService.findById(lineResponse.getId());

        assertThat(deleteSectionLineResponse.getStations()).hasSize(2);
        assertThat(deleteSectionLineResponse.getStations()).doesNotContain(new StationResponse(3L, "상록수역"));
    }

    private LineResponse createLine(String lineName, String lineColor, String upStationName, String downStationName) {
        // given
        Station upStation = stationRepository.save(new Station(upStationName));
        Station downStation = stationRepository.save(new Station(downStationName));


        // when
        LineResponse lineResponse = lineService.saveLine(new LineRequest(lineName, lineColor, upStation.getId(), downStation.getId(), 10));

        // then
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(upStationName);
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(downStationName);

        return lineResponse;
    }

    private LineResponse createLineAndAddSection(String newStationName) {
        // given
        Station newDownStation = stationRepository.save(new Station(newStationName));
        LineResponse beforeResponse = lineResponse;

        // when
        lineService.addSection(lineResponse.getId(), new SectionRequest(getLastStationId(lineResponse.getStations()), newDownStation.getId(), 10));

        // then
        LineResponse addSectionLineResponse = lineService.findById(lineResponse.getId());
        List<StationResponse> stations = addSectionLineResponse.getStations();
        assertThat(stations).hasSize(beforeResponse.getStations().size() + 1);
        assertThat(stations.get(lastIndexOf(stations)).getName()).isEqualTo(newStationName);

        return addSectionLineResponse;
    }

    public long getLastStationId(List<StationResponse> stationResponses) {
        return stationResponses.get(lastIndexOf(stationResponses)).getId();
    }

    private int lastIndexOf(List<?> list) {
        return list.size() - 1;
    }

}