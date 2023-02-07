package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.exception.NoExistLineException;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.common.error.SubwayError.NO_FIND_LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선의 대한 테스트")
@SpringBootTest
@Transactional
class LineServiceTest {

    private static final String 강남역 = "강남역";
    private static final String 잠실역 = "잠실역";
    private static final String 녹색 = "bg-green-600";
    private static final String 빨간색 = "bg-red-600";
    private static final String 이호선 = "2호선";
    private static final String 신분당선 = "신분당선";

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("노선의 구간을 생성한다.")
    @Test
    void addSection() {
        final Station 상행종점_강남역 = createStation(강남역);
        final Station 하행종점_잠실역 = createStation(잠실역);
        final Line 노선_이호선 = createLine(이호선, 녹색);

        final SectionRequest sectionRequest = new SectionRequest(상행종점_강남역.getId(), 하행종점_잠실역.getId(), 10);
        lineService.addSection(노선_이호선.getId(), sectionRequest);

        final List<Station> stations = 노선_이호선.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(상행종점_강남역, 하행종점_잠실역)
        );
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void showLines() {
        createLine(이호선, 녹색);
        createLine(신분당선, 빨간색);

        final List<LineResponse> 응답_노선_목록 = lineService.showLines();

        assertAll(
                () -> assertThat(응답_노선_목록).hasSize(2),
                () -> assertThat(응답_노선_목록.get(0).getName()).isEqualTo(이호선),
                () -> assertThat(응답_노선_목록.get(0).getColor()).isEqualTo(녹색),
                () -> assertThat(응답_노선_목록.get(1).getName()).isEqualTo(신분당선),
                () -> assertThat(응답_노선_목록.get(1).getColor()).isEqualTo(빨간색)

        );
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void findLine() {
        final Station 상행종점_강남역 = new Station(1L, 강남역);
        final Station 하행종점_잠실역 = new Station(2L, 잠실역);
        final Line 노선_이호선 = createLine(이호선, 녹색);
        노선_이호선.addSection(상행종점_강남역, 하행종점_잠실역, 10);

        final LineResponse 응답_노선 = lineService.findById(노선_이호선.getId());

        assertAll(
                () -> assertThat(응답_노선.getId()).isEqualTo(노선_이호선.getId()),
                () -> assertThat(응답_노선.getName()).isEqualTo(이호선),
                () -> assertThat(응답_노선.getColor()).isEqualTo(녹색),
                () -> assertThat(응답_노선.getStationResponses()).hasSize(2)
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        final LineRequest 요청_수정_노선 = LineRequest.from(신분당선, 빨간색);
        final Line 노선 = createLine(이호선, 녹색);

        lineService.updateLine(노선.getId(), 요청_수정_노선);

        assertAll(
                () -> assertThat(노선.getName()).isEqualTo(신분당선),
                () -> assertThat(노선.getColor()).isEqualTo(빨간색)
        );
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        final Line 노선 = createLine(이호선, 녹색);

        lineService.deleteLine(노선.getId());

        assertThatThrownBy(() -> lineService.findLine(노선.getId()))
                .isInstanceOf(NoExistLineException.class)
                .hasMessage(NO_FIND_LINE.getMessage());

    }

    private Station createStation(final String station) {
        return stationRepository.save(new Station(station));
    }

    private Line createLine(final String name, final String color) {
        return lineRepository.save(new Line(name, color));
    }
}
