package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.fake.FakeLineFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    private Line 분당선;

    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
    }

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = 지하철역_생성("강남역");
        Station 교대역 = 지하철역_생성("교대역");
        Line 분당선 = 구간_생성(new Line("분당선", "green"));
        Section 강남역_교대역_구간 = new Section(분당선, 강남역, 교대역, 10);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 교대역.getId(), 10);
        lineService.addSection(분당선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineRepository.findById(분당선.getId()).orElseThrow(IllegalArgumentException::new);
        Sections sections = line.getSections();
        assertThat(sections.getValues()).containsAnyOf(강남역_교대역_구간);
    }

    @Test
    void 라인_등록_검증() {
        //given
        LineRequest lineRequest = new LineRequest("2호선", "green");

        //when
        lineService.saveLine(lineRequest);

        //then
        List<Line> findAllLines = lineRepository.findAll();
        assertThat(findAllLines).hasSize(1);
    }

    @Test
    void 라인_수정_검증() {
        //given
        LineRequest saveRequest = new LineRequest(분당선.getName(), 분당선.getColor());
        LineResponse lineResponse = lineService.saveLine(saveRequest);

        //when
        LineRequest updateRequest = new LineRequest("신분당선", "red");
        lineService.updateLine(lineResponse.getId(), updateRequest);

        //then
        LineResponse findLineResponse = lineService.findById(lineResponse.getId());
        assertThat(findLineResponse.getName()).isEqualTo("신분당선");
        assertThat(findLineResponse.getColor()).isEqualTo("red");
    }

    @Test
    void 라인_삭제_검증() {
        //given
        LineRequest saveRequest = new LineRequest(분당선.getName(), 분당선.getColor());
        LineResponse saveResponse = lineService.saveLine(saveRequest);

        //when
        lineService.deleteLine(saveResponse.getId());

        //then
        List<LineResponse> lineResponses = lineService.showLines();
        assertThat(lineResponses).hasSize(0);
    }

    private Station 지하철역_생성(String name) {
        return stationRepository.save(new Station(name));
    }

    private Line 구간_생성(Line line) {
        return lineRepository.save(line);
    }

}
