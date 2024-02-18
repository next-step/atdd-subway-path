package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.BusinessException;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // given

//        final LineRequest lineRequest = this.신분당선_생성_요청();
//
//        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
//        when(stationRepository.findById(2L)).thenReturn(Optional.of(역삼역));
//        when(lineRepository.save(any(Line.class))).thenReturn(신분당선);
//
//        // when
//        final LineResponse response = lineService.createSubwayLine(lineRequest);
//
//        // then
//        assertThat(response.getName()).isEqualTo(lineRequest.getName());
//        assertThat(response.getColor()).isEqualTo(lineRequest.getColor());
//        verify(lineRepository).save(any(Line.class));

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선을 생성 시 상행역 혹은 하행역을 찾을 수 없으면 에러가 발생한다.")
    @Test
    void 지하철_노선_생성_실패_존재하지_않는_역() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선을 조회 시 없는 노선인 경우 에러가 발생한다.")
    @Test
    void 지하철_노선_조회_실패_존재하지_않는_노선() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void 지하철_노선_수정() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선을 수정 시 없는 노선인 경우 에러가 발생한다.")
    @Test
    void 지하철_노선_수정_실패_존재하지_않는_노선() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선을 수정 시 노선 이름 혹은 색 필드가 누락된 경우 에러가 발생한다.")
    @Test
    void 지하철_노선_수정_실패_필드_누락() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void 지하철_구간_추가() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", Collections.emptyList())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);
        final var 세번째역 = FixtureUtil.getFixture(Station.class);

        final var 구간 = new Section(노선, 첫번째역, 두번째역, 10);
        노선.addSection(구간);

        when(lineRepository.findById(노선.getId())).thenReturn(Optional.of(노선));
        when(stationService.findById(두번째역.getId())).thenReturn(두번째역);
        when(stationService.findById(세번째역.getId())).thenReturn(세번째역);

        // when
        lineService.addSection(노선.getId(), 두번째역.getId(), 세번째역.getId(), 10);

        // then
        assertThat(
            노선.getSections().stream()
                .anyMatch(
                    section -> section.getLine().getId().equals(노선.getId())
                        && section.getUpStation().getId().equals(두번째역.getId())
                        && section.getDownStation().getId().equals(세번째역.getId())
                )
        ).isTrue();
    }

    @DisplayName("지하철 구간을 생성 시 노선에 이미 등록된 구간인 경우 에러가 발생한다.")
    @Test
    void 지하철_구간_생성_실패_이미_등록된_구간() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void 지하철_구간_삭제() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }

    @DisplayName("지하철 구간을 삭제 시 대상 구간이 노선의 유일한 구간인 경우 에러가 발생한다.")
    @Test
    void 지하철_구간_삭제_실패_노선의_유일한_구간() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }


}
