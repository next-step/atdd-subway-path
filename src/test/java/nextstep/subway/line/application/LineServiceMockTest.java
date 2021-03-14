package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.line.exception.LineAlreadyExistException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.CannotRemoveStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

@DisplayName("노선 비즈니스 로직 Mock 테스트")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Station savedStationGangnam;
    private Station savedStationYeoksam;
    private Station savedStationSamseong;
    private Station savedStationYangJae;

    private LineRequest line2Request;
    private Line line2;
    private Line lineNewBunDang;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);

        savedStationGangnam = new Station("강남역");
        ReflectionTestUtils.setField(savedStationGangnam, "id", 1L);

        savedStationYeoksam = new Station("역삼역");
        ReflectionTestUtils.setField(savedStationYeoksam, "id", 2L);

        savedStationSamseong = new Station("삼성역");
        ReflectionTestUtils.setField(savedStationSamseong, "id", 3L);

        savedStationYangJae = new Station("양재역");
        ReflectionTestUtils.setField(savedStationYangJae, "id", 4L);

        line2 = new Line("2호선", "bg-green-600");
        ReflectionTestUtils.setField(line2, "id", 1L);

        lineNewBunDang = new Line("신분당선", "bg-red-600");
        ReflectionTestUtils.setField(lineNewBunDang, "id", 2L);

        line2Request = new LineRequest("2호선", "bg-green-600", savedStationGangnam.getId(), savedStationYeoksam.getId(), 10);
    }

    @Test
    @DisplayName("노선 저장")
    void saveLine() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.save(any(Line.class))).willReturn(line2);

        // when
        LineResponse lineResponse = lineService.saveLine(line2Request);

        // then
        assertThat(lineResponse).isNotNull();
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("노선 저장 시 존재하는 이름이 있으면 에러 발생")
    void validateNameToSaveLine() {
        // given
        given(lineRepository.existsByName("2호선")).willThrow(LineAlreadyExistException.class);

        // when & then
        assertThatExceptionOfType(LineAlreadyExistException.class)
                .isThrownBy(() -> lineService.saveLine(line2Request));
    }

    @Test
    @DisplayName("노선 수정")
    void updateLine() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.save(any(Line.class))).willReturn(line2);
        LineResponse lineResponse = lineService.saveLine(line2Request);

        given(lineRepository.findById(lineResponse.getId())).willReturn(Optional.ofNullable(line2));

        // when
        LineResponse updatedLine2Response = lineService.updateLine(lineResponse.getId(), new LineRequest("2호선", "bg-green-100"));

        // then
        assertThat(updatedLine2Response.getColor()).isEqualTo("bg-green-100");
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.save(any(Line.class))).willReturn(line2);
        LineResponse lineResponse = lineService.saveLine(line2Request);

        given(lineRepository.findById(lineResponse.getId())).willReturn(Optional.ofNullable(line2));

        // when
        lineService.deleteLineById(lineResponse.getId());

        // then
        assertThat(lineService.findAllLines()).hasSize(0);
    }

    @Test
    @DisplayName("모든 노선 조회")
    void findAllLines() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.save(any(Line.class))).willReturn(line2);
        lineService.saveLine(line2Request);

        given(stationService.findStationById(4L)).willReturn(savedStationYangJae);
        given(lineRepository.save(any(Line.class))).willReturn(lineNewBunDang);
        LineRequest lineNewBunDangRequest = new LineRequest("신분당선", "bg-red-600", savedStationGangnam.getId(), savedStationYangJae.getId(), 4);
        lineService.saveLine(lineNewBunDangRequest);

        given(lineRepository.findAll()).willReturn(Arrays.asList(line2, lineNewBunDang));

        // when
        List<LineResponse> savedLineAllResponses = lineService.findAllLines();

        // then
        assertThat(savedLineAllResponses).hasSize(2);
    }

        @Test
    @DisplayName("노선에 구간 추가")
    void addSection() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.findById(1L)).willReturn(Optional.ofNullable(line2));

        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationGangnam, savedStationYeoksam, 10));

        given(stationService.findStationById(3L)).willReturn(savedStationSamseong);

        // when
        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // then
        Line line = lineService.findLineById(line2.getId());
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 구간 삭제")
    void deleteSection() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.findById(1L)).willReturn(Optional.ofNullable(line2));

        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationGangnam, savedStationYeoksam, 10));

        given(stationService.findStationById(3L)).willReturn(savedStationSamseong);
        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // when
        lineService.deleteSectionToLine(line2.getId(), savedStationSamseong.getId());

        // then
        assertThat(line2.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("노선에 구간 삭제 시 하행 종점역이 아니면 에러 발생")
    void validateDownStationToDeleteSection() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.findById(1L)).willReturn(Optional.ofNullable(line2));

        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationGangnam, savedStationYeoksam, 10));

        given(stationService.findStationById(3L)).willReturn(savedStationSamseong);
        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // when & then
        assertThatExceptionOfType(CannotRemoveStationException.class)
                .isThrownBy(() -> lineService.deleteSectionToLine(line2.getId(), savedStationYeoksam.getId()));
    }

    @Test
    @DisplayName("노선에 구간 삭제 시 구간이 1개만 있을 경우 에러 발생")
    void validateSectionSizeToDeleteSection() {
        // given
        given(stationService.findStationById(1L)).willReturn(savedStationGangnam);
        given(stationService.findStationById(2L)).willReturn(savedStationYeoksam);
        given(lineRepository.findById(1L)).willReturn(Optional.ofNullable(line2));

        lineService.addSectionToLine(line2.getId(), createSectionRequest(savedStationGangnam, savedStationYeoksam, 10));

        // when & then
        assertThatExceptionOfType(CannotRemoveSectionException.class)
                .isThrownBy(() -> lineService.deleteSectionToLine(line2.getId(), savedStationYeoksam.getId()));
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation, int distance) {
        return new SectionRequest(upStation.getId(), downStation.getId(), distance);
    }
}
