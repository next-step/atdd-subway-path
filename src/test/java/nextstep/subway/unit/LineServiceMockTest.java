package nextstep.subway.unit;

import nextstep.config.fixtures.LineFixture;
import nextstep.config.fixtures.SectionFixture;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.application.LineService;
import nextstep.subway.application.PathFinder;
import nextstep.subway.application.dto.PathResult;
import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.repository.LineRepository;
import nextstep.subway.entity.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    LineService lineService;

    @Mock
    StationRepository stationRepository;

    @Mock
    LineRepository lineRepository;

    @Mock
    PathFinder pathFinder;


    @BeforeEach
    void 서비스_객체_생성() {
        lineService = new LineService(stationRepository, lineRepository, pathFinder);
    }

    @Nested
    class 지하철_구간 {

        Long 이호선_아이디;
        Long 선릉역_번호;
        Long 삼성역_번호;
        Long 신천역_번호;

        Station 선릉역;
        Station 삼성역;
        Station 신천역;


        @BeforeEach
        void 사전_스텁_설정() {
            이호선_아이디 = 1L;
            선릉역_번호 = 1L;
            삼성역_번호 = 2L;
            신천역_번호 = 3L;

            선릉역 = StationFixture.선릉;
            ReflectionTestUtils.setField(선릉역, "id", 선릉역_번호);

            삼성역 = StationFixture.삼성;
            ReflectionTestUtils.setField(삼성역, "id", 삼성역_번호);

            신천역 = StationFixture.신천;
            ReflectionTestUtils.setField(신천역, "id", 신천역_번호);

            when(lineRepository.findById(이호선_아이디)).thenReturn(Optional.of(LineFixture.이호선_생성()));
            when(stationRepository.findById(선릉역_번호)).thenReturn(Optional.of(선릉역));
            when(stationRepository.findById(삼성역_번호)).thenReturn(Optional.of(삼성역));
        }

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 추가하면
         * Then  지하철 노선에 구간이 추가된다.
         */
        @Test
        void 지하철_구간_추가() {
            // given
            SectionRequest 선릉_삼성_구간 = SectionRequest.mergeForCreateLine(
                    이호선_아이디, SectionFixture.지하철_구간(선릉역_번호, 삼성역_번호, 10));

            // when
            lineService.addSection(선릉_삼성_구간);

            // then
            assertThat(lineService.findLineById(이호선_아이디).getAllStations())
                    .containsAnyOf(선릉역, 삼성역);
        }

        /**
         * Given 지하철 노선이 생성되고 구간을 추가한다
         * When  지하철 구간을 삭제하면
         * Then  지하철 노선에 구간이 삭제된다
         */
        @Test
        void 지하철_구간_삭제() {
            // given
            SectionRequest 선릉_삼성_구간 = SectionRequest.mergeForCreateLine(
                    이호선_아이디, SectionFixture.지하철_구간(선릉역_번호, 삼성역_번호, 10));
            SectionRequest 삼성_신천_구간 = SectionRequest.mergeForCreateLine(
                    이호선_아이디, SectionFixture.지하철_구간(삼성역_번호, 신천역_번호, 10));

            when(stationRepository.findById(신천역_번호)).thenReturn(Optional.of(신천역));

            lineService.addSection(선릉_삼성_구간);
            lineService.addSection(삼성_신천_구간);

            // when
            lineService.deleteSection(이호선_아이디, 신천역_번호);

            // then
            assertThat(lineService.findLineById(이호선_아이디).getAllStations())
                    .containsAnyOf(선릉역, 삼성역);
        }
    }

    @Nested
    class 지하철_경로 {

        Line 이호선;
        Line 신분당선;
        Line 삼호선;
        Line 사호선;

        List<Line> 모든_노선_목록;

        Long 이호선_아이디;
        Long 신분당선_아이디;
        Long 삼호선_아이디;
        Long 사호선_아이디;

        Station 교대;
        Station 강남;
        Station 양재;
        Station 남부터미널;
        Station 정왕;
        Station 오이도;

        Long 교대역_아이디;
        Long 강남역_아이디;
        Long 양재역_아이디;
        Long 남부터미널역_아이디;
        Long 정왕역_아이디;
        Long 오이도역_아이디;


        @BeforeEach
        void 사전_노선_설정() {
            이호선_아이디 = 1L;
            신분당선_아이디 = 2L;
            삼호선_아이디 = 3L;
            사호선_아이디 = 3L;

            이호선 = new Line("이호선", "green");
            ReflectionTestUtils.setField(이호선, "id", 이호선_아이디);

            신분당선 = new Line("신분당선", "red");
            ReflectionTestUtils.setField(신분당선, "id", 신분당선_아이디);

            삼호선 = new Line("삼호선", "orange");
            ReflectionTestUtils.setField(삼호선, "id", 삼호선_아이디);

            사호선 = new Line("사호선", "blue");
            ReflectionTestUtils.setField(사호선, "id", 사호선_아이디);

            모든_노선_목록 = List.of(이호선, 신분당선, 삼호선, 사호선);

            교대역_아이디 = 1L;
            강남역_아이디 = 2L;
            양재역_아이디 = 3L;
            남부터미널역_아이디 = 4L;
            정왕역_아이디 = 5L;
            오이도역_아이디 = 6L;

            교대 = StationFixture.교대;
            ReflectionTestUtils.setField(교대, "id", 교대역_아이디);

            강남 = StationFixture.강남;
            ReflectionTestUtils.setField(강남, "id", 강남역_아이디);

            양재 = StationFixture.양재;
            ReflectionTestUtils.setField(양재, "id", 양재역_아이디);

            남부터미널 = StationFixture.남부터미널;
            ReflectionTestUtils.setField(남부터미널, "id", 남부터미널역_아이디);

            정왕 = StationFixture.정왕;
            ReflectionTestUtils.setField(정왕, "id", 정왕역_아이디);

            오이도 = StationFixture.오이도;
            ReflectionTestUtils.setField(오이도, "id", 오이도역_아이디);

            이호선.addSection(new Section(교대, 강남, 10, 이호선));
            신분당선.addSection(new Section(강남, 양재, 10, 신분당선));
            삼호선.addSection(new Section(교대, 남부터미널, 2, 삼호선));
            삼호선.addSection(new Section(남부터미널, 양재, 3, 삼호선));
            사호선.addSection(new Section(정왕, 오이도, 10, 사호선));
        }

        /**
         * Given 지하철 노선을 생성하고, 구간을 추가한다.
         * When  출발역과 도착역을 통해 경로를 조회할 경우
         * Then  최단거리의 존재하는 역 목록과 최단 거리 값을 확인할 수 있다.
         */
        @Test
        void 강남역에서_남부터미널역까지_경로_조회() {
            // given
            when(stationRepository.existsById(강남역_아이디)).thenReturn(true);
            when(stationRepository.existsById(남부터미널역_아이디)).thenReturn(true);
            when(stationRepository.findById(강남역_아이디)).thenReturn(Optional.of(강남));
            when(stationRepository.findById(남부터미널역_아이디)).thenReturn(Optional.of(남부터미널));
            when(lineRepository.findAll()).thenReturn(모든_노선_목록);
            when(pathFinder.calculateShortestPath(모든_노선_목록, 강남, 남부터미널)).thenReturn(new PathResult(List.of(강남, 교대, 남부터미널), 12));

            // when
            PathResponse 경로_조회_응답 = lineService.findShortestPath(new PathRequest(강남역_아이디, 남부터미널역_아이디));

            // then
            assertThat(경로_조회_응답).usingRecursiveComparison()
                    .isEqualTo(new PathResponse(List.of(강남, 교대, 남부터미널), 12));
        }

        /**
         * Given 지하철 노선을 생성하고, 구간을 추가한다.
         * When  출발역과 도착역을 통해 경로를 조회할 경우
         * Then  최단거리의 존재하는 역 목록과 최단 거리 값을 확인할 수 있다.
         */
        @Test
        void 교대역에서_양재역까지_경로_조회() {
            // given
            when(stationRepository.existsById(교대역_아이디)).thenReturn(true);
            when(stationRepository.existsById(양재역_아이디)).thenReturn(true);
            when(stationRepository.findById(교대역_아이디)).thenReturn(Optional.of(교대));
            when(stationRepository.findById(양재역_아이디)).thenReturn(Optional.of(양재));
            when(lineRepository.findAll()).thenReturn(모든_노선_목록);
            when(pathFinder.calculateShortestPath(모든_노선_목록, 교대, 양재)).thenReturn(new PathResult(List.of(교대, 남부터미널, 양재), 5));

            // when
            PathResponse 경로_조회_응답 = lineService.findShortestPath(new PathRequest(교대역_아이디, 양재역_아이디));

            // then
            assertThat(경로_조회_응답).usingRecursiveComparison()
                    .isEqualTo(new PathResponse(List.of(교대, 남부터미널, 양재), 5));
        }

    }
}
