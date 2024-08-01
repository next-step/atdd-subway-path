package nextstep.subway.section.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.section.exception.SectionException;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.repository.StationRepository;
import nextstep.subway.station.service.StationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private SectionRepository sectionRepository;
    private StationService stationService;
    private LineService lineService;
    private SectionService sectionService;

    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Station 언주역;
    private Station 논현역;
    private Sections 신분당선_구간 = new Sections();
    private Section 강남역_선릉역_구간;
    private Line 신분당선;

    @BeforeEach
    public void setup() {
        stationService = new StationService(stationRepository, lineService);
        lineService = new LineService(lineRepository, stationService);
        sectionService = new SectionService(sectionRepository, stationService, lineService);
        강남역 = Station.of(1L, "강남역");
        선릉역 = Station.of(2L, "선릉역");
        삼성역 = Station.of(3L, "삼성역");
        언주역 = Station.of(4L, "언주역");
        논현역 = Station.of(5L, "논현역");

        강남역_선릉역_구간 = Section.of(강남역, 선릉역, 1L);
        신분당선_구간.addSection(강남역_선릉역_구간);
        신분당선 = Line.of(1L, "신분당선", "Red", 10L, 신분당선_구간);
    }

    @Test
    @DisplayName("새로운 구간을 첫번째 구간에 생성한다.")
    public void createSection_first() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(1L)).thenReturn(Optional.ofNullable(강남역));
        when(stationRepository.findById(3L)).thenReturn(Optional.ofNullable(삼성역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        // when
        var 삼성역_강남역_구간_생성_요청 = SectionRequest.of(삼성역.getId(), 강남역.getId(), 5L);
        var 삼성역_강남역_구간_생성_응답 = sectionService.createSection(신분당선.getId(), 삼성역_강남역_구간_생성_요청);

        // then
        verify(lineRepository, times(1)).findById(1L);
        verify(stationRepository, times(1)).findById(1L);
        verify(stationRepository, times(1)).findById(3L);
        verify(lineRepository, times(1)).save(신분당선);

        Assertions.assertAll(
                () -> assertEquals(삼성역_강남역_구간_생성_응답.getLineId(), 신분당선.getId()),
                () -> assertEquals(삼성역_강남역_구간_생성_응답.getUpStationResponse().getId(), 삼성역.getId()),
                () -> assertEquals(삼성역_강남역_구간_생성_응답.getUpStationResponse().getName(), 삼성역.getName()),
                () -> assertEquals(삼성역_강남역_구간_생성_응답.getDownStationResponse().getId(), 강남역.getId()),
                () ->assertEquals(삼성역_강남역_구간_생성_응답.getDownStationResponse().getName(), 강남역.getName())
        );

    }

    @Test
    @DisplayName("새로운 구간을 중간 구간에 생성한다.")
    public void createSection_middle() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(3L)).thenReturn(Optional.ofNullable(삼성역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        sectionService.createSection(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 5L));

        // when
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        var 선릉역_언주역_구간_생성_응답 = sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // then
        verify(lineRepository, times(2)).findById(1L);
        verify(stationRepository, times(2)).findById(2L);
        verify(stationRepository, times(1)).findById(3L);
        verify(stationRepository, times(1)).findById(4L);
        verify(lineRepository, times(2)).save(신분당선);

        Assertions.assertAll(
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getLineId(), 신분당선.getId()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getId(), 선릉역.getId()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getName(), 선릉역.getName()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getId(), 언주역.getId()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getName(), 언주역.getName()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getDistance(), 1L)
        );

    }

    @Test
    @DisplayName("새로운 구간을 마지막 구간에 생성한다.")
    public void createSection_last() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when
        var 선릉역_언주역_구간_생성_응답 = sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // then
        verify(lineRepository, times(1)).findById(1L);
        verify(stationRepository, times(1)).findById(2L);
        verify(stationRepository, times(1)).findById(4L);
        verify(lineRepository, times(1)).save(신분당선);

        Assertions.assertAll(
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getLineId(), 신분당선.getId()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getId(), 선릉역.getId()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getName(), 선릉역.getName()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getId(), 언주역.getId()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getName(), 언주역.getName()),
                () -> assertEquals(선릉역_언주역_구간_생성_응답.getDistance(), 1L)
        );

    }

    @Test
    @DisplayName("새로운 구간의 lineId를 찾을 수 없다.")
    public void createSection_fail_lineId_cannot_found() {
        // given
        when(lineRepository.findById(2L)).thenReturn(Optional.ofNullable(null));

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(LineNotFoundException.class, () -> sectionService.createSection(2L, 선릉역_언주역_구간_생성_요청))
                .getMessage().equals("노선을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 upStation을 찾을 수 없다.")
    public void createSection_fail_upStation_cannot_found() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        var 존재하지_않는역과_언주역_구간_생성_요청 = SectionRequest.of(10L, 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 존재하지_않는역과_언주역_구간_생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 downStation을 찾을 수 없다.")
    public void createSection_fail_downStation_cannot_found() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(stationRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        var 언주역과_존재하지_않는_역_구간_생성_요청 = SectionRequest.of(언주역.getId(), 10L, 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 언주역과_존재하지_않는_역_구간_생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("상행 종점역을 가진 첫 번째 구간을 삭제한다.")
    public void delete_section_first_section() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        sectionService.deleteSection(신분당선.getId(), 강남역.getId());

        verify(lineRepository, times(2)).findById(1L);
        verify(stationRepository, times(1)).findById(2L);
        verify(stationRepository, times(1)).findById(4L);
        verify(lineRepository, times(2)).save(신분당선);
    }

    @Test
    @DisplayName("중간 구간을 삭제한다.")
    public void delete_section_middle_section() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(stationRepository.findById(5L)).thenReturn(Optional.ofNullable(논현역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);
        var 언주역_논현역_구간_생성_요청 = SectionRequest.of(언주역.getId(), 논현역.getId(), 2L);
        sectionService.createSection(신분당선.getId(), 언주역_논현역_구간_생성_요청);

        // when & then
        sectionService.deleteSection(신분당선.getId(), 선릉역.getId());

        verify(lineRepository, times(3)).findById(1L);
        verify(stationRepository, times(1)).findById(2L);
        verify(stationRepository, times(2)).findById(4L);
        verify(stationRepository, times(1)).findById(5L);
        verify(lineRepository, times(3)).save(신분당선);
    }

    @Test
    @DisplayName("하행 종점역을 가진 마지막 구간을 삭제한다.")
    public void delete_section_last_section() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        sectionService.deleteSection(신분당선.getId(), 언주역.getId());

        verify(lineRepository, times(2)).findById(1L);
        verify(stationRepository, times(1)).findById(2L);
        verify(stationRepository, times(1)).findById(4L);
        verify(lineRepository, times(2)).save(신분당선);
    }

    @Test
    @DisplayName("구간에 존재하지 않는 역의 삭제 요청은 실패한다.")
    public void delete_section_fail1() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> sectionService.deleteSection(신분당선.getId(), 논현역.getId()))
                .getMessage().equals("구간을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("삭제 구간의 대상이 되는 노선의 lineId를 찾을 수 없다.")
    public void delete_section_fail2() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(lineRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));

        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        Assertions.assertThrows(LineNotFoundException.class, () -> sectionService.deleteSection(2L, 언주역.getId()))
                .getMessage().equals("노선을 찾을 수 없습니다.");
    }

}