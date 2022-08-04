package nextstep.subway.unit;

import nextstep.subway.applicaion.LineCommandService;
import nextstep.subway.applicaion.LineQueryService;
import nextstep.subway.applicaion.StationQueryService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRegistrationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nextstep.subway.utils.GivenUtils.FIRST_ID;
import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.GREEN;
import static nextstep.subway.utils.GivenUtils.SECOND_ID;
import static nextstep.subway.utils.GivenUtils.TEN;
import static nextstep.subway.utils.GivenUtils.YELLOW;
import static nextstep.subway.utils.GivenUtils.강남_역삼_구간_요청;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.강남역_이름;
import static nextstep.subway.utils.GivenUtils.분당선_이름;
import static nextstep.subway.utils.GivenUtils.분당선으로_수정_요청;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.역삼역_이름;
import static nextstep.subway.utils.GivenUtils.이호선;
import static nextstep.subway.utils.GivenUtils.이호선_객체_생성_요청;
import static nextstep.subway.utils.GivenUtils.이호선_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class LineCommandServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationQueryService stationQueryService;
    @Mock
    private LineQueryService lineQueryService;

    @InjectMocks
    private LineCommandService lineCommandService;

    @Test
    @DisplayName("지하철 노선 생성")
    void createLine() {
        // given
        doReturn(new Line(FIRST_ID, 이호선_이름, GREEN)).when(lineRepository)
                .save(new Line(이호선_이름, GREEN));
        doReturn(강남역()).when(stationQueryService)
                .findById(FIRST_ID);
        doReturn(역삼역()).when(stationQueryService)
                .findById(SECOND_ID);

        // when
        LineResponse lineResponse = lineCommandService.saveLine(이호선_객체_생성_요청());
        List<String> stationNames = lineResponse.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(lineResponse.getId()).isEqualTo(FIRST_ID);
        assertThat(lineResponse.getName()).isEqualTo(이호선_이름);
        assertThat(lineResponse.getColor()).isEqualTo(GREEN);
        assertThat(stationNames).containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("지하철 노선 생성 실패 - 존재하지 않는 역으로 노선 생성")
    void createLineByInvalidStationIds() {
        // given
        doThrow(EntityNotFoundException.class).when(stationQueryService)
                .findById(FIRST_ID);

        // when
        Executable executable = () -> lineCommandService.saveLine(이호선_객체_생성_요청());
        
        // then
        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선 수정")
    void modifyLine() {
        // given
        Line 이호선 = 이호선();
        doReturn(이호선).when(lineQueryService)
                .findById(FIRST_ID);

        // when
        lineCommandService.modifyLine(이호선.getId(), 분당선으로_수정_요청());

        // then
        assertThat(이호선.getName()).isEqualTo(분당선_이름);
        assertThat(이호선.getColor()).isEqualTo(YELLOW);
    }

    @Test
    @DisplayName("지하철 노선 수정 실패 - 존재하지 않는 지하철 노선 수정")
    void modifyNoneExistentLine() {
        // given
        doThrow(EntityNotFoundException.class).when(lineQueryService)
                .findById(FIRST_ID);

        // when
        Executable executable = () -> lineCommandService.modifyLine(FIRST_ID, 분당선으로_수정_요청());
        
        // then
        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선 제거 실패 - 존재하지 않는 지하철 노선 제거")
    void deleteNonExistentLine() {
        // given
        doThrow(EmptyResultDataAccessException.class).when(lineRepository)
                .deleteById(FIRST_ID);

        // when
        Executable executable = () -> lineCommandService.deleteLineById(FIRST_ID);

        // then
        assertThrows(EmptyResultDataAccessException.class, executable);
    }
    
    @Test
    @DisplayName("지하철 노선에 구간 추가")
    void addSection() {
        // given
        Line 이호선 = new Line(FIRST_ID, 이호선_이름, GREEN);
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        doReturn(이호선).when(lineQueryService)
                .findById(FIRST_ID);
        doReturn(강남역).when(stationQueryService)
                .findById(FIRST_ID);
        doReturn(역삼역).when(stationQueryService)
                .findById(SECOND_ID);

        // when
        lineCommandService.addSection(이호선.getId(), 강남_역삼_구간_요청());

        // then
        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @Test
    @DisplayName("지하철 노선에 구간 등록 실패 - 노선의 상행, 하행 종점역과 무관한 역 추가")
    void addSectionWithInvalidStation() {
        // given
        Line 이호선 = 이호선();
        Station 양재역 = 양재역();
        Station 강남역 = 강남역();
        Station 선릉역 = 선릉역();
        doReturn(양재역).when(stationQueryService)
                .findById(양재역.getId());
        doReturn(선릉역).when(stationQueryService)
                .findById(선릉역.getId());
        doReturn(이호선).when(lineQueryService)
                .findById(이호선.getId());
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                양재역.getId(),
                선릉역.getId(),
                TEN
        );

        // when
        Executable executable = () -> lineCommandService.addSection(이호선.getId(), sectionRequest);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선에 구간 제거")
    void removeSection() {
        // given
        int expectedSize = 2;
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        이호선.addSection(역삼역, 선릉역, FIVE);
        doReturn(선릉역).when(stationQueryService)
                .findById(선릉역.getId());
        doReturn(이호선).when(lineQueryService)
                .findById(이호선.getId());

        // when
        lineCommandService.removeSection(이호선.getId(), 선릉역.getId());

        // then
        assertThat(이호선.getStations()).hasSize(expectedSize).containsExactly(강남역, 역삼역);
    }

    @Test
    @DisplayName("지하철 노선에 구간 제거 실패 - 존재하지 않는 역 제거")
    void removeSectionWithInvalidStation() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        Station 양재역 = 양재역();
        이호선.addSection(역삼역, 선릉역, FIVE);
        doReturn(양재역).when(stationQueryService)
                .findById(양재역.getId());
        doReturn(이호선).when(lineQueryService)
                .findById(이호선.getId());

        // when
        Executable executable = () -> lineCommandService.removeSection(이호선.getId(), 양재역.getId());

        // then
        assertThrows(NoSuchElementException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선에 구간 제거 실패 - 구간이 1개인 경우")
    void removeSingleSection() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        doReturn(역삼역).when(stationQueryService)
                .findById(역삼역.getId());
        doReturn(이호선).when(lineQueryService)
                .findById(이호선.getId());

        // when
        Executable executable = () -> lineCommandService.removeSection(이호선.getId(), 역삼역().getId());

        // then
        assertThrows(SectionRemovalException.class, executable);
    }
    
}
