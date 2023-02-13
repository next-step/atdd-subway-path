package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private EntityManager em;

    private Line 신분당선;

    private Station 강남역;
    private Station 양재역;

    private Section 강남_양재_구간;

    private int distance = 10;
    private int halfDistance = (int) (distance / 2);

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "bg-red-900");
        distance = 10;

        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        강남역 = stationRepository.save(강남역);
        양재역 = stationRepository.save(양재역);

        강남_양재_구간 = new Section(신분당선, 강남역, 양재역, distance);
    }

    @Test
    @DisplayName("노선종점역에 새구간 추가")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = lineRepository.save(신분당선);
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), distance);

        // when
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        신분당선 = lineRepository.findById(line.getId()).orElseThrow();
        assertThat(line.getSections()).containsExactlyElementsOf(신분당선.getSections());
    }

    @Test
    @DisplayName("노선시작역에 앞에 새구간 추가")
    void addSectionFirst() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 신논현역 = stationRepository.save(new Station("신논현역"));
        Line line = lineRepository.save(신분당선);
        SectionRequest sectionRequest1 = new SectionRequest(강남역.getId(), 양재역.getId(), distance);
        SectionRequest sectionRequest2 = new SectionRequest(신논현역.getId(), 강남역.getId(), distance);
        lineService.addSection(line.getId(), sectionRequest1);

        // when
        lineService.addSection(line.getId(), sectionRequest2);
        flushAndClearEntityManger();

        // then
        신분당선 = lineRepository.findById(line.getId()).orElseThrow();
        노선의_지하철역_순서_검증(신분당선, List.of(신논현역, 강남역, 양재역));
    }

    @Test
    @DisplayName("노선의 기존구간의 중간에 새구간이 삽입되는 경우 테스트 ( 삽입하는 구간의 상행역과 기존구간의 상행역이 일치하는 경우 )")
    void addSectionMiddle() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 신논현역 = stationRepository.save(new Station("신논현역"));
        Line line = lineRepository.save(신분당선);

        SectionRequest 기존구간 = new SectionRequest(신논현역.getId(), 양재역.getId(), distance);
        SectionRequest 새구간 = new SectionRequest(신논현역.getId(), 강남역.getId(), halfDistance);
        lineService.addSection(line.getId(), 기존구간);

        // when
        lineService.addSection(line.getId(), 새구간);
        flushAndClearEntityManger();

        // then
        신분당선 = lineRepository.findById(line.getId()).orElseThrow();
        노선의_지하철역_순서_검증(신분당선, List.of(신논현역, 강남역, 양재역));
    }


    @Test
    @DisplayName("노선의 기존구간의 중간에 새구간이 삽입되는 경우 테스트 ( 삽입하는 구간의 하행역과 기존구간의 하행역이 일치하는 경우 )")
    void addSectionMiddle2() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 신논현역 = stationRepository.save(new Station("신논현역"));
        Line line = lineRepository.save(신분당선);
        SectionRequest 기존구간 = new SectionRequest(신논현역.getId(), 양재역.getId(), distance);
        SectionRequest 새구간 = new SectionRequest(강남역.getId(), 양재역.getId(), halfDistance);
        lineService.addSection(line.getId(), 기존구간);

        // when
        lineService.addSection(line.getId(), 새구간);
        flushAndClearEntityManger();

        // then
        신분당선 = lineRepository.findById(line.getId()).orElseThrow();
        노선의_지하철역_순서_검증(신분당선, List.of(신논현역, 강남역, 양재역));
    }

    @Test
    void updateLine() {
        // given
        Line line = lineRepository.save(신분당선);

        // when
        lineService.updateLine(line.getId(), new LineRequest("새로운이름", "새로운컬러"));

        // then
        노선_수정_정보_검증(line);
    }

    @Test
    void saveLine() {
        // given
        LineRequest lineRequest = new LineRequest(신분당선.getName(), 신분당선.getColor(), 강남역.getId(), 양재역.getId(), distance);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        노선_저장_검증(lineResponse);
    }

    @Test
    void getSections() {
        // given
        Station 청계산역 = stationRepository.save(new Station("청계산역"));
        LineRequest lineRequest = new LineRequest(신분당선.getName(), 신분당선.getColor(), 강남역.getId(), 양재역.getId(), distance);


        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        lineService.addSection(lineResponse.getId(), new SectionRequest(양재역.getId(), 청계산역.getId(), 10));
        flushAndClearEntityManger();

        Line line = lineRepository.findById(lineResponse.getId()).orElseThrow();
        // then
        SoftAssertions.assertSoftly((sa) -> {
            sa.assertThat(line.getName()).isEqualTo(신분당선.getName());
            sa.assertThat(line.getColor()).isEqualTo(신분당선.getColor());
        });
        노선의_지하철역_순서_검증(line, List.of(강남역, 양재역, 청계산역) );
    }

    @Test
    void deleteSection() {
        // given
        Station 양재시민의숲역 = stationRepository.save(new Station("양재시민의숲역"));
        LineRequest lineRequest = new LineRequest(신분당선.getName(), 신분당선.getColor(), 강남역.getId(), 양재역.getId(), distance);
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 양재시민의숲역.getId(), distance);
        lineService.addSection(lineResponse.getId(), sectionRequest);

        // when
        lineService.deleteSection(lineResponse.getId(), 양재시민의숲역.getId());

        // then
        Line 업데이트된노선 = lineRepository.findById(lineResponse.getId()).orElseThrow();
        노선의_지하철역_순서_검증(업데이트된노선, List.of(강남역, 양재역));
    }

    private void 노선_수정_정보_검증(Line line) {
        Line 업데이트된노선 = lineRepository.findById(line.getId()).orElseThrow();
        SoftAssertions.assertSoftly((sa) -> {
            sa.assertThat(line.getName()).isEqualTo(업데이트된노선.getName());
            sa.assertThat(line.getColor()).isEqualTo(업데이트된노선.getColor());
        });
    }

    private void 노선_저장_검증(LineResponse lineResponse) {
        Assertions.assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(신분당선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(신분당선.getColor()),
                () -> assertThat(lineResponse.getStations()).usingRecursiveComparison().isEqualTo(List.of(StationResponse.from(강남역), StationResponse.from(양재역)))
        );
    }

    private void 노선의_지하철역_순서_검증(Line line, List<Station> 검증역들) {
        assertThat(line.getStations()).usingRecursiveComparison()
                .isEqualTo(검증역들);
    }

    private void flushAndClearEntityManger() {
        em.flush();
        em.clear();
    }
}
