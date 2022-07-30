package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import java.util.List;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineSaveRequest;
import nextstep.subway.applicaion.dto.LineUpdateRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("실제 노선 저장")
    void saveLine() {
        final long 강남역_id = 역_생성("강남역");
        final long 역삼역_id = 역_생성("역삼역");

        final LineResponse lineResponse = 노선_생성("2호선", "green", 강남역_id, 역삼역_id, 10);

        assertAll(
            () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
            () -> assertThat(lineResponse.getColor()).isEqualTo("green"),
            () -> assertThat(lineResponse.getStations()).containsExactly(
                new StationResponse(1L, "강남역"), new StationResponse(2L, "역삼역"))
        );
    }

    @Test
    @DisplayName("실제 구간 추가")
    void addSection() {
        // given
        final long 강남역_id = 역_생성("강남역");
        final long 역삼역_id = 역_생성("역삼역");
        final LineResponse lineResponse = 노선_생성("2호선", "green", 강남역_id, 역삼역_id, 10);
        final long 선릉역_id = 역_생성("선릉역");

        // when
        lineService.addSection(lineResponse.getId(),
            new SectionRequest(역삼역_id, 선릉역_id, 10));

        // then
        final LineResponse 구간_추가_후_노선_응답 = lineService.findById(lineResponse.getId());
        assertThat(구간_추가_후_노선_응답.getStations()).hasSize(3);
    }

    @Test
    @DisplayName("실제 노선 조회")
    void showLines() {
        //given
        final long 강남역_id = 역_생성("강남역");
        final long 역삼역_id = 역_생성("역삼역");
        final LineResponse createLineResponse = 노선_생성("2호선", "green", 강남역_id, 역삼역_id, 10);

        //when
        final List<LineResponse> responseList = lineService.showLines();

        //then
        final LineResponse lineResponse = responseList.get(0);

        assertAll(
            () -> assertThat(lineResponse.getId()).isEqualTo(createLineResponse.getId()),
            () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
            () -> assertThat(lineResponse.getColor()).isEqualTo("green"),
            () -> assertThat(lineResponse.getStations()).hasSize(2)
        );
    }

    @Test
    @DisplayName("실제 노선 갱신")
    void updateLine() {
        //given
        final long 강남역_id = 역_생성("강남역");
        final long 역삼역_id = 역_생성("역삼역");
        final LineResponse 노선_생성_응답 = 노선_생성("2호선", "green", 강남역_id, 역삼역_id, 10);

        //when
        lineService.updateLine(노선_생성_응답.getId(), new LineUpdateRequest("신분당선", "red"));

        //then
        final List<LineResponse> responseList = lineService.showLines();
        final LineResponse lineResponse = responseList.get(0);

        assertAll(
            () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
            () -> assertThat(lineResponse.getColor()).isEqualTo("red")
        );
    }

    @Test
    @DisplayName("실제 노선 삭제")
    void deleteLine() {
        //given
        final long 강남역_id = 역_생성("강남역");
        final long 역삼역_id = 역_생성("역삼역");
        final LineResponse lineResponse = 노선_생성("2호선", "green", 강남역_id, 역삼역_id, 10);

        lineService.deleteLine(lineResponse.getId());

        assertThatIllegalArgumentException()
            .isThrownBy(() -> lineService.findById(lineResponse.getId()));

    }

    @Test
    @DisplayName("실제 구간 삭제")
    void deleteSection() {
        //given
        final long 강남역_id = 역_생성("강남역");
        final long 역삼역_id = 역_생성("역삼역");
        final LineResponse lineResponse = 노선_생성("2호선", "green", 강남역_id, 역삼역_id, 10);
        final long 선릉역_id = 역_생성("선릉역");
        lineService.addSection(lineResponse.getId(),
            new SectionRequest(역삼역_id, 선릉역_id, 10));

        //when
        lineService.deleteSection(lineResponse.getId(), 선릉역_id);

        //then
        final LineResponse 삭제후_응답 = lineService.findById(lineResponse.getId());
        assertThat(삭제후_응답.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("실제 출발역 또는 도착역이 하나라도 포함되어 있는지")
    void containingStation() {
        final long 강남역 = 역_생성("강남역");
        final long 역삼역 = 역_생성("역삼역");
        final LineResponse 노선_생성 = 노선_생성("2호선", "green", 강남역, 역삼역, 10);

        final List<Line> lines = lineService.lineContainingStation(List.of(강남역, 역삼역));

        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getId()).isEqualTo(노선_생성.getId());
    }

    private LineResponse 노선_생성(final String name, final String color, final long upStationId, final long downStationId
        , final int distance) {
        LineSaveRequest request = new LineSaveRequest(name, color, upStationId, downStationId, distance);
        return lineService.saveLine(request);
    }

    private long 역_생성(final String stationName) {
        final Station station = stationRepository.save(new Station(stationName));
        return station.getId();
    }

}
