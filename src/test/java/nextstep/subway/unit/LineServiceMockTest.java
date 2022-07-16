package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
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

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Long lineId = 3L;
        Long upStationId = 1L;
        Long downStationId = 2L;

        given(stationService.findById(upStationId)).willReturn(new Station("강남역"));
        given(stationService.findById(downStationId)).willReturn(new Station("건대입구역"));
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(new Line("2호선", "green")));

        SectionRequest request = new SectionRequest(upStationId, downStationId, 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, request);

        // then
        // line.findLineById 메서드를 통해 검증
        Line findLine = lineRepository.findById(lineId).get();
        then(findLine.getSections()).hasSize(1);
    }

    @DisplayName("구간이 하나 있는 line에 구간삭제 요청시 성공한다")
    @Test
    public void delete_section_test() {
        // given
        // 역과 라인을 생성하고, 라인에 구간을 등록시킨다
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "건대입구역");
        Line line = new Line(3L, "2호선", "green");
        line.addSection(new Section(line, upStation, downStation, 10));

        given(stationService.findById(anyLong())).willReturn(downStation);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        // 라인에서 구간을 삭제하면
        lineService.deleteSection(line.getId(), downStation.getId());

        // then
        // 라인의 구간은 비어있게 된다
        Line findLine = lineRepository.findById(line.getId()).get();
        then(findLine.getSections()).isEmpty();
    }

    @DisplayName("구간이 없는 line에 구간삭제 요청시 예외가 발생한다")
    @Test
    public void delete_section_when_no_section() {
        // given
        Station downStation = new Station(2L, "건대입구역");
        Line line = new Line(3L, "2호선", "green");

        given(stationService.findById(anyLong())).willReturn(downStation);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> lineService.deleteSection(line.getId(), downStation.getId()));

        // then
        then(exception).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @DisplayName("존재하지 않는 line에 구간삭제 요청시 예외가 발생한다")
    @Test
    public void delete_section_when_no_line() {
        // given
        Station downStation = new Station(2L, "건대입구역");

        given(lineRepository.findById(anyLong())).willThrow(new IllegalArgumentException());

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> lineService.deleteSection(1L, downStation.getId()));

        // then
        then(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 라인의 색과 이름을 변경할 수 있다")
    @Test
    public void line_update_success_test() {
        // given
        Line line = new Line(3L, "2호선", "green");
        LineRequest request = new LineRequest("변경된 호선", "blue");

        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        lineService.updateLine(line.getId(), request);

        // then
        then(line.getName()).isEqualTo("변경된 호선");
        then(line.getColor()).isEqualTo("blue");
    }

    @DisplayName("존재하지 않는 라인을 수정하려 하는 경우 예외가 발생한다")
    @Test
    public void line_update_fail_test() {
        // given
        LineRequest request = new LineRequest("변경된 호선", "blue");

        given(lineRepository.findById(anyLong())).willThrow(new IllegalArgumentException());

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> lineService.updateLine(1L, request));

        // then
        then(exception).isInstanceOf(IllegalArgumentException.class);

    }
}
