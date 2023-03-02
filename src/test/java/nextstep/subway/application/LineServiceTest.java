package nextstep.subway.application;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.UpdateLineRequest;
import nextstep.subway.domain.*;
import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;
    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    void setUp() {
        이호선 = createLine("2호선", "bg-green-600");
        강남역 = createStation("강남역");
        역삼역 = createStation("역삼역");
        선릉역 = createStation("선릉역");
    }

    @DisplayName("노선 저장")
    @Test
    void saveLine() {
        // given
        final CreateLineRequest createLineRequest = new CreateLineRequest("3호선", "bg-green-600", 강남역.getId(), 역삼역.getId(), 10);

        // when
        lineService.saveLine(createLineRequest);

        // then
        final Line 삼호선 = lineRepository.findByName("3호선").get();
        assertThat(삼호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("노선 목록 조회")
    @Test
    void showLines() {
        // when
        final List<LineResponse> lineResponses = lineService.showLines();

        // then
        final List<String> lineNames = lineResponses.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        assertThat(lineNames).containsAnyOf("2호선");
    }

    @DisplayName("ID 로 노선 상세 조회")
    @Test
    void findById() {
        // when
        final LineResponse lineResponse = lineService.findById(이호선.getId());

        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @DisplayName("노선 정보 수정")
    @Test
    void updateLine() {
        // given
        final UpdateLineRequest updateLineRequest = new UpdateLineRequest("신분당선", "bg-red-600");

        // when
        lineService.updateLine(이호선.getId(), updateLineRequest);

        // then
        final LineResponse lineResponse = lineService.findById(이호선.getId());
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("노선 삭제")
    @Test
    void deleteLine() {
        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> {
            lineService.findById(이호선.getId());
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // when
        lineService.addSection(이호선.getId(), new AddSectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        final List<Section> sections = 이호선.getSections();
        assertThat(sections).containsAnyOf(new Section(이호선, 강남역, 역삼역, 10));
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection() {
        // given
        lineService.addSection(이호선.getId(), new AddSectionRequest(강남역.getId(), 역삼역.getId(), 10));
        lineService.addSection(이호선.getId(), new AddSectionRequest(역삼역.getId(), 선릉역.getId(), 10));

        // when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        // then
        final List<Section> sections = 이호선.getSections();
        assertThat(sections).doesNotContain(new Section(이호선, 역삼역, 선릉역, 10));
    }

    private Station createStation(final String name) {
        return stationRepository.save(new Station(name));
    }

    private Line createLine(final String name, final String color) {
        return lineRepository.save(new Line(name, color));
    }
}
