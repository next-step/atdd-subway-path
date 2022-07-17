package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 관련 without Mock")
@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Stub stub;

    public LineServiceTest() {
        this.stub = new Stub();
    }

    @DisplayName("지하철 구간 등록")
    @Test
    void addSection() {
        // given
        Station 구로디지털단지역 = stub.구로디지털단지역_생성.get();
        Station 신대방역 = stub.신대방역_생성.get();
        Station 신림역 = stub.신림역_생성.get();
        Line 이호선 = stub.이호선_생성.apply(구로디지털단지역, 신대방역);

        // when
        lineService.addSection(이호선.getId(), new SectionRequest(신대방역.getId(), 신림역.getId(), 8));

        // then
        assertThat(이호선.getStations()).contains(구로디지털단지역, 신대방역, 신림역);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteSection() {
        // given
        Station 구로디지털단지역 = stub.구로디지털단지역_생성.get();
        Station 신대방역 = stub.신대방역_생성.get();
        Line 이호선 = stub.이호선_생성.apply(구로디지털단지역, 신대방역);
        이호선.addSection(이호선.getLastDownStation(), stub.신림역_생성.get(), 8);

        // when
        lineService.deleteSection(이호선.getId(), 이호선.getLastDownStation().getId());

        // then
        assertThat(이호선.getStations()).contains(구로디지털단지역, 신대방역);
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void saveLine() {
        // given
        Station 구로디지털단지역 = stub.구로디지털단지역_생성.get();
        Station 신대방역 = stub.신대방역_생성.get();

        // when
        LineResponse response = lineService.saveLine(new LineRequest("2호선", "green", 구로디지털단지역.getId(), 신대방역.getId(), 10));

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("2호선");
        assertThat(response.getColor()).isEqualTo("green");
        assertThat(response.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Line 이호선 = stub.이호선_생성.apply(stub.구로디지털단지역_생성.get(), stub.신대방역_생성.get());

        // when
        lineService.updateLine(이호선.getId(), new LineRequest("신분당선", "red"));

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        Line 이호선 = stub.이호선_생성.apply(stub.구로디지털단지역_생성.get(), stub.신대방역_생성.get());

        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> lineService.findLineById(이호선.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private class Stub {
        private final Supplier<Station> 구로디지털단지역_생성 = () -> saveStation("구로디지털단지역");
        private final Supplier<Station> 신대방역_생성 = () -> saveStation("신대방역");
        private final Supplier<Station> 신림역_생성 = () -> saveStation("신림역");

        private final BiFunction<Station, Station, Line> 이호선_생성 =
                (Station upStation, Station downStation) ->
                        lineRepository.save(new Line("2호선", "green", upStation, downStation, 10));

        private Station saveStation(String name) {
            return stationRepository.save(new Station(name));
        }
    }

}
