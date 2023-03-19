package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;

import nextstep.subway.exception.SectionBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
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

    private Line 강남_2호선;

    private Station 강남역;

    private Station 역삼역;

    private Station 삼성역;

    @BeforeEach
    void setUp() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        강남_2호선 = lineRepository.save(new Line("강남 2호선", "green"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
    }

    @Test
    void 노선에_구간을_추가한다() {

        // when
        // lineService.addSection 호출
        lineService.addSection(강남_2호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> 검증하려는_노선의_구간_목록 = lineRepository.findById(강남_2호선.getId()).get().getSections();
        List<Section> 입력했던_노선의_구간_목록 = 강남_2호선.getSections();

        assertAll(
                () -> assertThat(검증하려는_노선의_구간_목록).isEqualTo(입력했던_노선의_구간_목록),
                () -> assertThat(검증하려는_노선의_구간_목록.get(0).getUpStation().getId()).isEqualTo(입력했던_노선의_구간_목록.get(0).getUpStation().getId()),
                () -> assertThat(검증하려는_노선의_구간_목록.get(0).getDownStation().getId()).isEqualTo(입력했던_노선의_구간_목록.get(0).getDownStation().getId()),
                () -> assertThat(검증하려는_노선의_구간_목록.get(0).getDistance()).isEqualTo(10)
        );
    }

    @Test
    void 노선에_등록되어있는_구간_목록을_조회한다() {
        // given
        lineService.addSection(강남_2호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
        lineService.addSection(강남_2호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 12));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        List<StationResponse> stations = lineResponses.get(0).getStations();
        assertThat(stations.get(0).getName()).isEqualTo(강남역.getName());
        assertThat(stations.get(1).getName()).isEqualTo(역삼역.getName());
        assertThat(stations.get(2).getName()).isEqualTo(삼성역.getName());
    }

    @Test
    void 구간이_비어있는_경우_노선을_삭제_할_수_없다() {
        // given

        // when

        // then
    }

    @Test
    void 노선의_이름과_색을_변경한다() {
        // given
        LineRequest lineRequest = new LineRequest("강남강남_2호선", "super green");

        // when
        lineService.updateLine(강남_2호선.getId(), lineRequest);

        // then
        Line 강남강남_2호선 = lineRepository.findById(강남_2호선.getId()).get();
        assertAll(
                () -> assertThat(강남강남_2호선.getName()).isEqualTo(강남_2호선.getName()),
                () -> assertThat(강남강남_2호선.getColor()).isEqualTo(강남_2호선.getColor())
        );
    }

    /*
    * 구간이 비어있는 경우 노선을 삭제 할 수 없다
    * 노선의_구간이_1개일_경우_구간을_삭제_할_수_없다
    * 노선에_등록된_하행_종점역만_제거_할_수_있다
    * 노선의 구간을 삭제한다
    * */
}
