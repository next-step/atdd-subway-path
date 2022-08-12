package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineUpdateRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = 역_생성(new Station("강남역"));
        역삼역 = 역_생성(new Station("역삼역"));
        삼성역 = 역_생성(new Station("삼성역"));
        이호선 = 이호선_생성();
    }

    @DisplayName("지하철 노선 목록 가져오기")
    @Test
    void showLines() {
        // when
        final List<LineResponse> 노선_목록_응답 = lineService.showLines();

        // then
        assertThat(노선_목록_응답).hasSize(1);
    }

    @DisplayName("ID 를 통해 지하철 노선 가져오기")
    @Test
    void findById() {
        // when
        final LineResponse 노선_정보_응답 = lineService.findById(이호선.getId());

        // then
        assertThat(노선_정보_응답.getId()).isEqualTo(이호선.getId());
    }

    @DisplayName("지하철 노선 정보 수정")
    @Test
    void updateLine() {
        // when
        lineService.updateLine(이호선.getId(), new LineUpdateRequest("8호선", null));

        // then
        assertThat(이호선.getName()).isEqualTo("8호선");
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // when
        lineService.deleteLine(이호선.getId());

        // then
        final List<LineResponse> 노선_목록_응답 = lineService.showLines();
        assertThat(노선_목록_응답).hasSize(0);
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        final Sections 노선_구간들 = 이호선.getSections();
        assertThat(노선_구간들.size()).isEqualTo(1);
    }

    @DisplayName("지하철 노선에서 구간 제거")
    @Test
    void deleteSection() {
        // given
        이호선.getSections().add(new Section(이호선, 강남역, 역삼역, 10));
        이호선.getSections().add(new Section(이호선, 역삼역, 삼성역, 10));

        // when
        lineService.deleteSection(이호선.getId(), 삼성역.getId());

        // then
        final Sections 노선_구간들 = 이호선.getSections();
        assertThat(노선_구간들.size()).isEqualTo(1);
    }

    @DisplayName("지하철 노선에서 하행종점역이 아닌 역을 제거하려고 할 때 에러 발생")
    @Test
    void deleteSectionWithNonLastStation() {
        // given
        이호선.getSections().add(new Section(이호선, 강남역, 역삼역, 10));
        이호선.getSections().add(new Section(이호선, 역삼역, 삼성역, 10));

        // when
        assertThatThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 역삼역.getId());
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("노선의 마지막 구간을 삭제하려고 할 때 에러 발생")
    @Test
    void deleteSectionWithLastSection() {
        // given
        이호선.getSections().add(new Section(이호선, 강남역, 역삼역, 10));

        // when
        assertThatThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 역삼역.getId());
        }).isInstanceOf(BusinessException.class);
    }

    private Line 이호선_생성() {
        return lineRepository.save(new Line(1L, "2호선", "green"));
    }

    private Station 역_생성(Station station) {
        return stationRepository.save(station);
    }

    @Nested
    class saveTest {
        @DisplayName("노선 생성")
        @Test
        void saveLine() {
            // when
            lineService.saveLine(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10));

            // then
            final List<LineResponse> 노선_목록_응답 = lineService.showLines();
            assertThat(노선_목록_응답).hasSize(2);
        }
    }
}
