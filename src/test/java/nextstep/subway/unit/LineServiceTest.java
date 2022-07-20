package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
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
    @DisplayName("노선 등록")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        // then
        // line.getSectionList 메서드를 통해 검증
        assertThat(이호선.getSectionList()).hasSize(1);
    }

    @Test
    @DisplayName("노선목록 조회")
    void showSection() {
        lineRepository.save(new Line("1호선", "bg-blue-600"));
        lineRepository.save(new Line("2호선", "bg-green-600"));

        List<LineResponse> 노선목록 = lineService.showLines();

        assertThat(노선목록).hasSize(2);
    }

    @Test
    @DisplayName("노선 조회")
    void findSection() {
        Line line = lineRepository.save(new Line("1호선", "bg-blue-600"));

        LineResponse 일호선 = lineService.findById(line.getId());

        assertAll(() -> {
            assertThat(일호선.getName()).isEqualTo("1호선");
            assertThat(일호선.getColor()).isEqualTo("bg-blue-600");
        });
    }

    @Test
    @DisplayName("노선 수정")
    void updateSection() {
        Line 이호선 = lineRepository.save(new Line("1호선", "bg-blue-600"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        lineService.updateLine(이호선.getId(), new LineRequest("2호선", "bg-green-600", 강남역.getId(), 역삼역.getId(), 6));

        assertAll(() -> {
            assertThat(이호선.getName()).isEqualTo("2호선");
            assertThat(이호선.getColor()).isEqualTo("bg-green-600");
        });
    }

    @Test
    @DisplayName("노선 삭제")
    void removeLine() {
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        lineService.deleteLine(이호선.getId());

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.findById(이호선.getId());
        });
    }

    @Test
    @DisplayName("구간 삭제")
    void removeSection() {
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        assertThat(이호선.isEmptySections()).isTrue();
    }

    @Test
    @DisplayName("구간 삭제 - [예외] 상행선을 삭제할려고 할떄")
    void removeSectionException() {
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 강남역.getId());
        });
    }
}
