package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("노선 추가 정상동작")
    void saveLine() {
        // given
        Line line = new Line(1L,"9호선","YELLOW");
        given(lineRepository.save(any(Line.class))).willReturn(line);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        Station upStation = new Station("염창역");
        Station downStation = new Station("당산역");
        given(stationService.findById(1L)).willReturn(upStation);
        given(stationService.findById(2L)).willReturn(downStation);

        // when
        lineService.saveLine(new LineRequest("9호선","YELLOW",1L,2L,10));

        // then
        Line actualLine = lineRepository.findById(1L).orElseThrow(RuntimeException::new);
        assertThat(line.getSections()).isEqualTo(actualLine.getSections());
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("염창역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("노선 전체 조회 정상동작")
    void showLines() {
        // given
        Line line = new Line(1L,"9호선","YELLOW");
        Station upStation = new Station("염창역");
        Station downStation = new Station("당산역");
        line.getSections().add(new Section(line,upStation,downStation,10));
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        given(lineRepository.findAll()).willReturn(lines);
        given(stationService.createStationResponse(upStation))
            .willReturn(new StationResponse(upStation.getId(), upStation.getName()));
        given(stationService.createStationResponse(downStation))
            .willReturn(new StationResponse(downStation.getId(), downStation.getName()));


        // when
        List<LineResponse> responses = lineService.showLines();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("9호선");
        assertThat(responses.get(0).getColor()).isEqualTo("YELLOW");
        assertThat(responses.get(0).getStations().get(0).getName()).isEqualTo("염창역");
        assertThat(responses.get(0).getStations().get(1).getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("노선 단일 조회 정상동작")
    void findLineById() {
        // given
        Line line = new Line(1L,"9호선","YELLOW");
        Station upStation = new Station("염창역");
        Station downStation = new Station("당산역");
        line.getSections().add(new Section(line,upStation,downStation,10));

        given(lineRepository.findById(1L)).willReturn(Optional.of(line));
        given(stationService.createStationResponse(upStation))
            .willReturn(new StationResponse(upStation.getId(), upStation.getName()));
        given(stationService.createStationResponse(downStation))
            .willReturn(new StationResponse(downStation.getId(), downStation.getName()));


        // when
        LineResponse response = lineService.findById(1L);

        // then
        assertThat(response.getName()).isEqualTo("9호선");
        assertThat(response.getColor()).isEqualTo("YELLOW");
        assertThat(response.getStations().get(0).getName()).isEqualTo("염창역");
        assertThat(response.getStations().get(1).getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("노선 수정 정상동작")
    void updateLine() {
        // given
        Line line = new Line(1L,"9호선","YELLOW");
        Station upStation = new Station(1L,"염창역");
        Station downStation = new Station(2L,"당산역");
        line.getSections().add(new Section(line,upStation,downStation,10));

        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        lineService.updateLine(1L,new LineRequest("7호선","RED",1L,2L,10));

        // then
        Line actual = lineRepository.findById(1L).orElseThrow(IllegalArgumentException::new);
        assertThat(actual.getName()).isEqualTo("7호선");
        assertThat(actual.getColor()).isEqualTo("RED");
    }

    @Test
    @DisplayName("노선 삭제 정상동작")
    void deleteLine() {
        // given
        Line line = new Line(1L,"9호선","YELLOW");
        Station upStation = new Station(1L,"염창역");
        Station downStation = new Station(2L,"당산역");
        line.getSections().add(new Section(line,upStation,downStation,10));

        // when
        lineService.deleteLine(1L);

        // then
        assertThatThrownBy(()->lineService.findById(1L)).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("구간 추가 정상동작")
    void addSection() {
        // given
        Station upStation = new Station("염창역");
        Station downStation = new Station("당산역");
        given(stationService.findById(1L)).willReturn(upStation);
        given(stationService.findById(2L)).willReturn(downStation);

        Line line = new Line("9호선","YELLOW");
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        lineService.addSection(1L,new SectionRequest(1L,2L,10));

        // then
        Line actualLine = lineRepository.findById(1L).orElseThrow(RuntimeException::new);
        assertThat(line.getSections()).isEqualTo(actualLine.getSections());
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("염창역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("구간 삭제 정상동작")
    void deleteSection() {
        // given
        Station upStation = new Station("염창역");
        Station downStation = new Station("당산역");
        Station endStation = new Station("여의도역");
        given(stationService.findById(3L)).willReturn(endStation);

        Line line = new Line("9호선","YELLOW");
        line.getSections().add(new Section(line,upStation,downStation,10));
        line.getSections().add(new Section(line,downStation,endStation,10));
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        lineService.deleteSection(1L,3L);

        // then
        Line actualLine = lineRepository.findById(1L).orElseThrow(RuntimeException::new);
        assertThat(line.getSections()).isEqualTo(actualLine.getSections());
        assertThat(line.getSections()).hasSize(1);
    }
}
