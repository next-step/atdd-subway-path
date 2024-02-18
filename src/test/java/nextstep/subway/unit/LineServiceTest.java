package nextstep.subway.unit;

import static com.navercorp.fixturemonkey.api.experimental.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.ui.BusinessException;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // given
        final var 강남역 = 역_생성("강남역");
        final var 양재역 = 역_생성("양재역");
        final var request = FixtureUtil.getBuilder(LineRequest.class)
            .set("name", "2호선")
            .set("color", "빨강")
            .set(javaGetter(LineRequest::getUpStationId), 강남역.getId())  // type safety expression
            .set(javaGetter(LineRequest::getDownStationId), 양재역.getId())
            .set("distance", 10)
            .sample();

        // when
        final var result = lineService.saveLine(request);

        // then
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getColor()).isEqualTo(request.getColor());

        assertTrue(lineRepository.findById(result.getId()).isPresent());
    }

    @DisplayName("지하철 노선을 생성 시 상행역 혹은 하행역을 찾을 수 없으면 에러가 발생한다.")
    @Test
    void 지하철_노선_생성_실패_존재하지_않는_역() {
        // given
        final var 강남역 = 역_생성("강남역");
        final var 존재하지_않는_역_ID = 9999L;
        final var request = FixtureUtil.getBuilder(LineRequest.class)
            .set("name", "2호선")
            .set("color", "빨강")
            .set(javaGetter(LineRequest::getUpStationId), 강남역.getId())
            .set(javaGetter(LineRequest::getDownStationId), 존재하지_않는_역_ID)
            .set("distance", 10)
            .sample();

        // when
        Throwable throwable = catchThrowable(() -> lineService.saveLine(request));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class)
            .hasMessageContaining("역 정보를 찾을 수 없습니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // given
        final var 존재하는_노선_ID_목록 = List.of(
            노선_생성("신분당선", "빨강"),
            노선_생성("2호선", "초록")
        ).stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        // when
        final var lines = lineService.showLines();

        // then
        assertThat(lines.size()).isEqualTo(존재하는_노선_ID_목록.size());
        assertThat(
            lines.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList())
        ).containsAll(존재하는_노선_ID_목록);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // given
        final var 신분당선 = 노선_생성("신분당선", "빨강");

        // when
        final var line = lineService.findById(신분당선.getId());

        // then
        assertThat(line.getName()).isEqualTo(신분당선.getName());
        assertThat(line.getColor()).isEqualTo(신분당선.getColor());
    }

    @DisplayName("지하철 노선을 조회 시 없는 노선인 경우 에러가 발생한다.")
    @Test
    void 지하철_노선_조회_실패_존재하지_않는_노선() {
        // given
        final var 존재하지_않는_노선_ID = 9999L;

        // when
        Throwable throwable = catchThrowable(() -> lineService.findById(존재하지_않는_노선_ID));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class)
            .hasMessageContaining("노선 정보를 찾을 수 없습니다.");
    }

    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void 지하철_노선_수정() {
        // given
        final var 신분당선 = 노선_생성("신분당선", "빨강");
        final var request = FixtureUtil.getBuilder(LineRequest.class)
            .set("name", "분당선")
            .set("color", "노랑")
            .sample();

        // when
        lineService.updateLine(신분당선.getId(), request);

        // then
        final var line = lineService.findById(신분당선.getId());
        assertThat(line.getName()).isEqualTo(request.getName());
        assertThat(line.getColor()).isEqualTo(request.getColor());
    }

    @DisplayName("지하철 노선을 수정 시 없는 노선인 경우 에러가 발생한다.")
    @Test
    void 지하철_노선_수정_실패_존재하지_않는_노선() {
        // given
        final var 존재하지_않는_노선_ID = 9999L;
        final var request = FixtureUtil.getBuilder(LineRequest.class)
            .set("name", "분당선")
            .set("color", "노랑")
            .sample();

        // when
        Throwable throwable = catchThrowable(() -> lineService.updateLine(존재하지_않는_노선_ID, request));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class)
            .hasMessageContaining("노선 정보를 찾을 수 없습니다.");
    }

    @DisplayName("지하철 노선을 수정 시 노선 이름 혹은 색 필드가 누락된 경우 에러가 발생한다.")
    @Test
    void 지하철_노선_수정_실패_필드_누락() {
        // given
        final var 신분당선 = 노선_생성("신분당선", "빨강");
        final var request = FixtureUtil.getBuilder(LineRequest.class)
            .set("name", null)
            .set("color", "노랑")
            .sample();

        // when
        Throwable throwable = catchThrowable(() -> lineService.updateLine(신분당선.getId(), request));

        // then
//        assertThat(throwable).isInstanceOf(AssertionError.class);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.getSections 메서드를 통해 검증
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void 지하철_구간_추가() {
        // given
        final var 강남역 = 역_생성("강남역");
        final var 양재역 = 역_생성("양재역");
        final var 신분당선 = 노선_생성("신분당선", "빨강");
        final var 첫구간 = new Section(신분당선, 강남역, 양재역, 10);
        신분당선.addSection(첫구간);

        final var 청계산입구역 = 역_생성("청계산입구역");

        // when
        lineService.addSection(신분당선.getId(), 양재역.getId(), 청계산입구역.getId(), 5);

        // then
        assertThat(
            신분당선.getSections().stream()
                .anyMatch(
                    section -> section.getLine().getId().equals(신분당선.getId())
                        && section.getUpStation().getId().equals(양재역.getId())
                        && section.getDownStation().getId().equals(청계산입구역.getId())
                )
        ).isTrue();
    }

    @DisplayName("지하철 구간을 생성 시 노선에 이미 등록된 구간인 경우 에러가 발생한다.")
    @Test
    void 지하철_구간_생성_실패_이미_등록된_구간() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.getSections 메서드를 통해 검증
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void 지하철_구간_삭제() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.getSections 메서드를 통해 검증
    }

    @DisplayName("지하철 구간을 삭제 시 대상 구간이 노선의 유일한 구간인 경우 에러가 발생한다.")
    @Test
    void 지하철_구간_삭제_실패_노선의_유일한_구간() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        // when
        // lineService.addSection 호출

        // then
        // line.getSections 메서드를 통해 검증
    }

    private Line 노선_생성(final String name, final String color) {
        return lineRepository.save(new Line(name, color));
    }

    private Station 역_생성(final String name) {
        return stationRepository.save(new Station(name));
    }
}
