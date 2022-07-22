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
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
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

    Station 강남역;
    Station 역삼역;
    Station 선릉역;

    Line 이호선;
    Line 일호선;

    @BeforeEach
    void setup() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));

        일호선 = lineRepository.save(new Line("1호선", "bg-blue-600"));
        이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));

    }

    @Test
    @DisplayName("지하철 구간 등록합니다.")
    void addSection() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        Section section = Section.builder()
            .line(이호선)
            .upStation(강남역)
            .downStation(역삼역)
            .distance(6)
            .build();

        assertAll(() -> {
            assertThat(이호선.getSectionList()).contains(section);
            assertThat(이호선.getSectionList()).hasSize(1);
        });

    }

    @Test
    @DisplayName("지하철 구간목록 조회합니다.")
    void showSection() {
        List<LineResponse> 비교값 = List.of(
            LineResponse.builder().id(일호선.getId()).name(일호선.getName()).color(일호선.getColor()).stations(List.of()).build(),
            LineResponse.builder().id(이호선.getId()).name(이호선.getName()).color(이호선.getColor()).stations(List.of()).build()
        );

        List<LineResponse> 노선목록 = lineService.showLines();

        assertAll(() -> {
            assertThat(노선목록).hasSize(2);
            assertThat(노선목록).isEqualTo(비교값);
        });
    }

    @Test
    @DisplayName("지하철 노선 조회합니다.")
    void findSection() {
        LineResponse 일호선_반환값 = LineResponse.builder()
            .id(일호선.getId())
            .name(일호선.getName())
            .color(일호선.getColor())
            .stations(List.of())
            .build();

        LineResponse 일호선_조회한값 = lineService.findById(일호선.getId());

        assertThat(일호선_조회한값).isEqualTo(일호선_반환값);
    }

    @Test
    @DisplayName("지하철 노선 수정합니다")
    void updateSection() {
        lineService.updateLine(이호선.getId(), new LineRequest("1호선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 6));

        assertAll(() -> {
            assertThat(이호선.getName()).isEqualTo("1호선");
            assertThat(이호선.getColor()).isEqualTo("bg-red-600");
        });
    }

    @Test
    @DisplayName("지하철 노선 삭제합니다")
    void removeLine() {
        lineService.deleteLine(이호선.getId());

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.findById(이호선.getId());
        });
    }

    @Test
    @DisplayName("지하철 구간 삭제합니다.")
    void removeSection() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        assertThat(이호선.isEmptySections()).isTrue();
    }

    @Test
    @DisplayName("지하철 구간 삭제중 하행선이 아닌 것을 삭제할떄 예외를 발생합니다.")
    void removeSectionException() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 4));

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 강남역.getId());
        });
    }

    @Test
    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할시 예외를 반환한다.")
    void removeByEmptySectionException() {
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 6));

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 강남역.getId());
        });
    }
}
