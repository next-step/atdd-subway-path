package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 노선 생성")
    @Test
    void saveLine() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        //when
        LineResponse response = lineService.saveLine(
                new LineRequest("신분당선", "yellow", 강남역.getId(), 역삼역.getId(), 10));

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("yellow");
        assertThat(response.getStations()).containsExactly(
                new StationResponse(강남역.getId(), "강남역"),
                new StationResponse(역삼역.getId(), "역삼역"));
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        //given
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));
        Line 분당선 = lineRepository.save(new Line("분당선", "red"));

        //when
        List<LineResponse> lineResponses = lineService.showLines();

        //then
        assertThat(lineResponses).hasSize(2);
        assertThat(lineResponses).containsExactly(
                new LineResponse(신분당선.getId(), 신분당선.getName(), 신분당선.getColor()),
                new LineResponse(분당선.getId(), 분당선.getName(), 분당선.getColor())
        );
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void findById() {
        //given
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));

        //when
        LineResponse response = lineService.findById(신분당선.getId());

        //then
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("yellow");
    }

    @DisplayName("지하철 노선 조회시 없으면 예외발생")
    @Test
    void findByIdException() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        //given
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));

        //when
        lineService.updateLine(신분당선.getId(), new LineRequest("분당선","yellow"));

        //then
        LineResponse response = lineService.findById(신분당선.getId());
        assertThat(response.getName()).isEqualTo("분당선");
        assertThat(response.getColor()).isEqualTo("yellow");
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        //given
        lineRepository.save(new Line("신분당선", "yellow"));
        Line 분당선 = lineRepository.save(new Line("분당선 ", "red"));

        //when
        lineService.deleteLine(분당선.getId());

        //then
        List<LineResponse> lineResponses = lineService.showLines();
        assertThat(lineResponses).hasSize(1);
    }

    @DisplayName("지하철 구간 생성")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        Section section = 신분당선.getSections().get(0);
        assertThat(section.getLine()).isEqualTo(신분당선);
        assertThat(section.getStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("지하철 구간 추가시 노선을 찾지 못하면 예외발생")
    @Test
    void addSectionException() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        // when //then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionRequest(강남역.getId(), 역삼역.getId(), 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteSection() {
        //given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 잠심역 = stationRepository.save(new Station("잠실역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
        lineService.addSection(신분당선.getId(), new SectionRequest(역삼역.getId(), 잠심역.getId(), 10));

        //when
        lineService.deleteSection(신분당선.getId(), 잠심역.getId());

        //then
        List<LineResponse> lineResponses = lineService.showLines();
        assertThat(lineResponses).hasSize(1);
    }

    @DisplayName("지하철 구간 삭제시 노선을 찾지 못하면 예외발생")
    @Test
    void deleteSectionException() {
        Station 강남역 = stationRepository.save(new Station("강남역"));

        // when // then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 강남역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 삭제시 하행종점역이 포함되지 않는 구간을 삭제시 예외발생")
    @Test
    void deleteSectionException2() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 잠실역 = stationRepository.save(new Station("잠실역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // when // then
        assertThatThrownBy(() -> lineService.deleteSection(신분당선.getId(), 잠실역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 삭제시 지하철역이 조회되지 않으면 예외발생")
    @Test
    void deleteSectionException3() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Long 잠실역 = 3L;
        Line 신분당선 = lineRepository.save(new Line("신분당선", "yellow"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // when // then
        assertThatThrownBy(() -> lineService.deleteSection(신분당선.getId(), 잠실역))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
