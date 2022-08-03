package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.LineTestFixtures;
import nextstep.subway.utils.StationTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static nextstep.subway.utils.LineTestFixtures.노선_생성_WITH_ID;
import static nextstep.subway.utils.StationTestFixtures.지하철역_생성_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    public Station 지하철역_생성(final String 지하철역이름, final Long 지하철역_아이디) {
        final Station 지하철역 = StationTestFixtures.지하철역_생성("강남역");
        ReflectionTestUtils.setField(지하철역, "id", 지하철역_아이디);
        return 지하철역;
    }

    public Line 노선_생성(final String 노선명, final String 노선색, final Station 상행역, final Station 하행역,
                      final int 거리, final Long 노선_아이디) {
        Line 노선 = LineTestFixtures.노선_생성(노선명, 노선색, 상행역, 하행역, 거리);
        ReflectionTestUtils.setField(노선, "id", 노선_아이디);
        return 노선;
    }

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새로운 구간 등록")
    @Test
    void addSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);

        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        when(stationService.findById(시청역.getId())).thenReturn(시청역);

        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);
        when(stationService.findById(구로디지털단지역.getId())).thenReturn(구로디지털단지역);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "red", 강남역, 시청역, 10, 1L);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        SectionRequest 두번째_구간_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 구로디지털단지역.getId(), 10);

        // when
        lineService.addSection(신분당선.getId(), 두번째_구간_요청);

        // then
        Line 반환된_신분당선 = lineService.findLineById(신분당선.getId());
        assertThat(반환된_신분당선.sectionSize()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록할 구간의 거리가 클 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionGreatorThanSectionDistance() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "red", 강남역, 시청역, 10, 1L);
        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 구로디지털단지역.getId(), 11);

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(3L)).thenReturn(구로디지털단지역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 동일할 경우")
    @Test
    void throwsExceptionIfAddSectionExistSectionEqualsThanSectionDistance() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "red", 강남역, 시청역, 10, 1L);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 구로디지털단지역.getId(), 10);

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(3L)).thenReturn(구로디지털단지역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청));
    }

    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 0 혹은 음수일경우")
    @Test
    void throwsExceptionIfAddSectionDistanceZeroOrNegative() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 구로디지털단지역.getId(), 0);

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(3L)).thenReturn(구로디지털단지역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("지하철 노선에 새롭게 등록하려는 구간이 존재할 경우")
    @Test
    void throwsExceptionIfEqualsAddSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(강남역.getId(), 시청역.getId(), 6);

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(시청역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청));
    }

    @DisplayName("지하철 노선의 Down Section에 구간을 등록")
    @Test
    void addSectionToDownSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 선릉역 = 지하철역_생성_WITH_ID("선릉역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);
        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 선릉역.getId(), 5);

        when(stationService.findById(2L)).thenReturn(시청역);
        when(stationService.findById(3L)).thenReturn(선릉역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // when
        boolean isAdded = lineService.addSection(신분당선.getId(), 구간_생성_요청);

        // then
        assertThat(isAdded).isTrue();
        assertThat(신분당선.stations()).containsExactly(강남역, 시청역, 선릉역);
    }

    @DisplayName("지하철 노선의 Top Section에 구간을 등록")
    @Test
    void addSectionToTopSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);
        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(구로디지털단지역.getId(), 강남역.getId(), 5);

        when(stationService.findById(3L)).thenReturn(구로디지털단지역);
        when(stationService.findById(1L)).thenReturn(강남역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // when
        boolean isAdded = lineService.addSection(신분당선.getId(), 구간_생성_요청);

        // then
        assertThat(isAdded).isTrue();
        assertThat(신분당선.stations()).containsExactly(구로디지털단지역, 강남역, 시청역);
    }

    @DisplayName("지하철 노선 구간에 일치하지 않은 상행, 하행을 가진 구간을 등록")
    @Test
    void throwsExceptionIfNotHasStations() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);
        final Station 선릉역 = 지하철역_생성_WITH_ID("선릉역", 4L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);

        final SectionRequest 구간_생성_요청 = LineTestFixtures.구간요청_생성(구로디지털단지역.getId(), 선릉역.getId(), 5);

        when(stationService.findById(3L)).thenReturn(구로디지털단지역);
        when(stationService.findById(4L)).thenReturn(선릉역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), 구간_생성_요청))
                .isInstanceOf(IllegalStateException.class);
    }

//    @DisplayName("노선의 모든 지하철역 조회")
//    @Test
//    void getStations() {
//
//        // given
//        final Station 강남역 = 지하철역_생성_WITH_ID()("강남역", 1L);
//        final Station 시청역 = 지하철역_생성_WITH_ID()("시청역", 2L);
//        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID()("구로디지털단지역", 3L);
//        final Station 선릉역 = 지하철역_생성_WITH_ID()("선릉역", 4L);
//        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "red", 강남역, 시청역, 10, 1L);
//        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));
//
//        when(stationService.findById(1L)).thenReturn(강남역);
//        when(stationService.findById(2L)).thenReturn(시청역);
//        when(stationService.findById(3L)).thenReturn(구로디지털단지역);
//        when(stationService.findById(4L)).thenReturn(선릉역);
//
//        SectionRequest 첫번째_구간_생성_요청 = LineTestFixtures.구간요청_생성(구로디지털단지역.getId(), 강남역.getId(), 10);
//        SectionRequest 두번째_구간_생성_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 선릉역.getId(), 5);
//
//        lineService.addSection(신분당선.getId(), 첫번째_구간_생성_요청);
//        lineService.addSection(신분당선.getId(), 두번째_구간_생성_요청);
//
//        List<Section> sections = 신분당선.getSections();
//        ReflectionTestUtils.setField(sections.get(0), "id", 1L);
//        ReflectionTestUtils.setField(sections.get(1), "id", 2L);
//        ReflectionTestUtils.setField(sections.get(2), "id", 3L);
//
//        // when
//        LineResponse 조회_응답 = lineService.findById(신분당선.getId());
//
//        // then
//        assertThat(조회_응답.getStations().stream()
//                .map(StationResponse::getName)
//                .collect(toList())).containsExactly(구로디지털단지역.getName(), 강남역.getName(), 시청역.getName(), 선릉역.getName());
//    }

    @DisplayName("지하철 노선의 상행 구간 제거")
    @Test
    void removeSectionToTopSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);
        신분당선.addSection(구로디지털단지역, 강남역, 5);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));
        when(stationService.findById(구로디지털단지역.getId())).thenReturn(구로디지털단지역);

        // when
        boolean isRemoved = lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        // then
        assertThat(isRemoved).isTrue();
        assertThat(신분당선.stations()).containsExactly(강남역, 시청역);
    }

    @DisplayName("지하철 노선의 하행 구간 제거")
    @Test
    void removeSectionToDownSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);

        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        when(stationService.findById(시청역.getId())).thenReturn(시청역);

        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);
        when(stationService.findById(구로디지털단지역.getId())).thenReturn(구로디지털단지역);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "red", 강남역, 시청역, 10, 1L);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        final SectionRequest 두번째_구간_요청 = LineTestFixtures.구간요청_생성(시청역.getId(), 구로디지털단지역.getId(), 5);

        lineService.addSection(신분당선.getId(), 두번째_구간_요청);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));
        when(stationService.findById(구로디지털단지역.getId())).thenReturn(구로디지털단지역);

        //when
        boolean isRemoved = lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        //then
        assertThat(isRemoved).isTrue();
        assertThat(신분당선.stations()).containsExactly(강남역, 시청역);
        assertThat(신분당선.sectionSize()).isEqualTo(1);
    }

    @DisplayName("지하철 노선의 중간 구간 제거")
    @Test
    void removeSectionToMiddleSection() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 3L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);
        신분당선.addSection(강남역, 구로디지털단지역, 5);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));
        when(stationService.findById(구로디지털단지역.getId())).thenReturn(구로디지털단지역);

        // when
        boolean isRemoved = lineService.removeSection(신분당선.getId(), 구로디지털단지역.getId());

        // then
        assertThat(isRemoved).isTrue();
        assertThat(신분당선.stations()).containsExactly(강남역, 시청역);
    }

    @DisplayName("지하철 노선의 구간이 1개일 때 제거 오류")
    @Test
    void throwsExceptionRemoveSectionIfSectionCountOne() {

        // given
        final Station 강남역 = 지하철역_생성_WITH_ID("강남역", 1L);
        final Station 시청역 = 지하철역_생성_WITH_ID("시청역", 2L);

        final Line 신분당선 = 노선_생성_WITH_ID("신분당선", "green", 강남역, 시청역, 10, 1L);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));
        when(stationService.findById(강남역.getId())).thenReturn(강남역);

        // then
        assertThatThrownBy(() -> lineService.removeSection(신분당선.getId(), 강남역.getId()))
                .isInstanceOf(IllegalStateException.class);
    }
}