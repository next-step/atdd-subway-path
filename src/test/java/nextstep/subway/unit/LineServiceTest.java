package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.NotExtensible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = 역_생성(new Station("강남역"));
        역삼역 = 역_생성(new Station("역삼역"));
        이호선 = 이호선_생성();
    }

    @Nested
    class saveTest {
        @Test
        void saveLine() {
            // when
            final LineResponse 노선_생성_응답 = lineService.saveLine(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10));

            // then
            final List<LineResponse> 노선_목록_응답 = lineService.showLines();
            assertThat(노선_목록_응답).hasSize(2);
        }
    }

    @Test
    void showLines() {
        // when
        final List<LineResponse> 노선_목록_응답 = lineService.showLines();

        // then
        assertThat(노선_목록_응답).hasSize(1);
    }

    @Test
    void findById() {
        // when
        final LineResponse 노선_정보_응답 = lineService.findById(이호선.getId());

        // then
        assertThat(노선_정보_응답.getId()).isEqualTo(이호선.getId());
    }

    @Test
    void updateLine() {
        // when
        lineService.updateLine(이호선.getId(), new LineRequest("8호선", null));

        // then
        assertThat(이호선.getName()).isEqualTo("8호선");
    }

    @Test
    void deleteLine() {
        // when
        lineService.deleteLine(이호선.getId());

        // then
        final List<LineResponse> 노선_목록_응답 = lineService.showLines();
        assertThat(노선_목록_응답).hasSize(0);
    }

    @Test
    void addSection() {
        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        final List<Section> 노선_구간들 = 이호선.getSections();
        assertThat(노선_구간들).hasSize(1);
    }

    @Test
    void deleteSection() {
        // given
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 10));

        // when
        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        // then
        final List<Section> 노선_구간들 = 이호선.getSections();
        assertThat(노선_구간들).hasSize(0);
    }

    private Line 이호선_생성() {
        return lineRepository.save(new Line(1L, "2호선", "green"));
    }

    private Station 역_생성(Station station) {
        return stationRepository.save(station);
    }
}
