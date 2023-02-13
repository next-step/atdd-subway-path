package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @BeforeEach
    void setup() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
        삼성역 = stationRepository.save(new Station("삼성역"));

        이호선 = lineRepository.save(new Line("이호선", "green"));
    }

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 이호선;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Line result = lineRepository.findById(이호선.getId()).get();
        assertAll(() -> {
            assertThat(result.getSections()).hasSize(1);
            assertThat(result.getStations()).containsExactlyElementsOf(List.of(강남역, 역삼역));
        });
    }

    /**
     * 역 사이에 등록
     *
     * 강남역 -------------- 선릉역
     *              |
     * (강남역) --- 역삼역
     */
    @Test
    void addSection_When_역사이에등록하면_Then_등록된다() {
        // given
        SectionRequest 강남역_선릉역 = new SectionRequest(강남역.getId(), 선릉역.getId(), 10);
        lineService.addSection(이호선.getId(), 강남역_선릉역);

        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 5);

        // when
        lineService.addSection(이호선.getId(), 강남역_역삼역);

        // then
        // line.getSections 메서드를 통해 검증
        Line result = lineRepository.findById(이호선.getId()).get();
        assertAll(() -> {
            assertThat(result.getSections()).hasSize(2);
            assertThat(result.getSections().stream()
                                            .map(Section::getDistance)
                                            .collect(Collectors.toList()))
                                            .containsExactlyElementsOf(List.of(5, 5));
            assertThat(result.getStations()).containsExactlyElementsOf(List.of(강남역, 역삼역, 선릉역));
        });
    }

    /**
     * 둘 다 등록되어 있음
     */
    @Test
    void addSection_When_새로운_구간의_역이_모두_등록되어있으면_Then_ThrowCustomException() {
        // given
        SectionRequest 강남역_선릉역 = new SectionRequest(강남역.getId(), 선릉역.getId(), 10);
        lineService.addSection(이호선.getId(), 강남역_선릉역);

        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 5);

        // then
        assertThatThrownBy(() -> lineService.addSection(이호선.getId(), 강남역_역삼역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.DUPLICATE_STATION_MSG);
    }

    /**
     * 역 사이에 등록 불가 (거리)
     */
    @Test
    void addSection_When_새로운_구간의_길이가_기존_길이보다_길면_Then_ThrowCustomException() {
        // given
        SectionRequest 강남역_선릉역 = new SectionRequest(강남역.getId(), 선릉역.getId(), 5);
        lineService.addSection(이호선.getId(), 강남역_선릉역);

        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);

        // then
        assertThatThrownBy(() -> lineService.addSection(이호선.getId(), 강남역_역삼역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.CAN_NOT_ADD_SECTION_CAUSE_DISTANCE);
    }

    /**
     * 둘 다 등록되어 있지 않음
     */
    @Test
    void addSection_When_새로운_구간의_역이_기존_노선에_존재하지_않는다면_Then_ThrowCustomException() {
        // given
        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 5);
        lineService.addSection(이호선.getId(), 강남역_역삼역);

        SectionRequest 선릉역_선릉역 = new SectionRequest(선릉역.getId(), 삼성역.getId(), 10);

        // then
        assertThatThrownBy(() -> lineService.addSection(이호선.getId(), 선릉역_선릉역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.ADD_STATION_MUST_INCLUDE_IN_LINE);
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

    @Test
    void deleteLine() {
        //given
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));

        //when
        lineService.deleteLine(이호선.getId());

        //then
        assertThat(lineRepository.findById(이호선.getId())).isEmpty();
    }

    @Test
    void deleteSection_When_마지막_역을_제거하면_Then_마지막_구간제거() {
        // given
        // 강남역 --- 역삼역 --- 선릉역
        Station 강남역 = stationRepository.save(new Station( "강남역"));
        Station 역삼역 = stationRepository.save(new Station( "역삼역"));
        Station 선릉역 = stationRepository.save(new Station( "선릉역"));
        LineRequest lineRequest = new LineRequest("이호선", "green", 강남역.getId(), 역삼역.getId(), 10);
        LineResponse 이호선 = lineService.saveLine(lineRequest);
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 선릉역.getId(), 15);
        lineService.addSection(이호선.getId(), sectionRequest);

        //when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        //then
        LineResponse response = lineService.findById(이호선.getId());
        assertThat(response.getStations().stream()
                            .map(StationResponse::getId)
                            .collect(Collectors.toList())).containsExactlyElementsOf(List.of(강남역.getId(), 역삼역.getId()));
    }

    @Test
    void deleteSection_When_중간_역을_제거하면_Then_중간_구간제거() {
        // given
        // 강남역 --- 역삼역 --- 선릉역
        Station 강남역 = stationRepository.save(new Station( "강남역"));
        Station 역삼역 = stationRepository.save(new Station( "역삼역"));
        Station 선릉역 = stationRepository.save(new Station( "선릉역"));
        LineRequest lineRequest = new LineRequest("이호선", "green", 강남역.getId(), 역삼역.getId(), 10);
        LineResponse 이호선 = lineService.saveLine(lineRequest);
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 선릉역.getId(), 15);
        lineService.addSection(이호선.getId(), sectionRequest);

        //when
        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        //then
        LineResponse response = lineService.findById(이호선.getId());
        assertThat(response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList())).containsExactlyElementsOf(List.of(강남역.getId(), 선릉역.getId()));
    }

    @Test
    void deleteSection_Given_노선에_구간이_하나뿐일때_When_구간을_제거하면_Then_ThrowException() {
        //given
        Station 강남역 = stationRepository.save(new Station( "강남역"));
        Station 역삼역 = stationRepository.save(new Station( "역삼역"));
        LineRequest lineRequest = new LineRequest("이호선", "green", 강남역.getId(), 역삼역.getId(), 10);
        LineResponse 이호선 = lineService.saveLine(lineRequest);

        //then
        assertThatThrownBy(() -> lineService.deleteSection(이호선.getId(), 역삼역.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.LINE_HAS_SECTION_AT_LEAST_ONE);
    }
}
