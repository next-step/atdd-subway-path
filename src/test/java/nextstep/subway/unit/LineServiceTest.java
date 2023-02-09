package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(() -> {
            assertThat(이호선.getSections()).hasSize(1);
            assertThat(이호선.getStations()).containsExactlyElementsOf(List.of(강남역, 역삼역));
        });
    }

    @Test
    void saveLine() {
        //given
        Station 강남역 = stationRepository.save(new Station( "강남역"));
        Station 역삼역 = stationRepository.save(new Station( "역삼역"));
        LineRequest lineRequest = new LineRequest("이호선", "green", 강남역.getId(), 역삼역.getId(), 10);

        //when
        LineResponse response = lineService.saveLine(lineRequest);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(lineRequest.getName());
    }

    @Test
    void findById() {
        //given
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));

        //when
        LineResponse response = lineService.findById(이호선.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(이호선.getId());
    }

    @Test
    void showLine() {
        //given
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));
        Line 삼호선 = lineRepository.save(new Line("삼호선", "blue"));

        //when
        List<LineResponse> lines = lineService.showLines();

        //then
        assertThat(lines).hasSize(2);
    }

    //updateLine
    @Test
    void updateLine() {
        //given
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));
        LineRequest lineRequest = new LineRequest("칠호선", "brown");

        //when
        lineService.updateLine(이호선.getId(), lineRequest);

        //then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertThat(line.getName()).isEqualTo(lineRequest.getName());
        assertThat(line.getColor()).isEqualTo(lineRequest.getColor());
    }

    //deleteLine
    @Test
    void deleteLine() {
        //given
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));

        //when
        lineService.deleteLine(이호선.getId());

        //then
        assertThat(lineRepository.findById(이호선.getId())).isEmpty();
    }

    //deleteSection
    @Test
    void deleteSection() {
        //given
        Station 강남역 = stationRepository.save(new Station( "강남역"));
        Station 역삼역 = stationRepository.save(new Station( "역삼역"));
        LineRequest lineRequest = new LineRequest("이호선", "green", 강남역.getId(), 역삼역.getId(), 10);
        LineResponse 이호선 = lineService.saveLine(lineRequest);

        //when
        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        //then
        LineResponse response = lineService.findById(이호선.getId());
        assertThat(response.getStations()).isEmpty();
    }
}
