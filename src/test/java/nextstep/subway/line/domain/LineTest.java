package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DoseNotExistStationOfSectionException;
import nextstep.subway.line.exception.HaveOnlyOneSectionException;
import nextstep.subway.line.exception.IsExistedSectionException;
import nextstep.subway.line.exception.IsMoreThanDistanceException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("Line Entity에 대한 단위 테스트")
public class LineTest {

    private Station 태평역 = new Station("태평역");
    private Station 가천대역 = new Station("가천대역");
    private Station 복정역 = new  Station("복정역");
    private Station 수서역 = new  Station("수서역");

    @BeforeEach()
    void setup() {
        ReflectionTestUtils.setField(태평역, "id", 1L);
        ReflectionTestUtils.setField(가천대역, "id", 2L);
        ReflectionTestUtils.setField(복정역, "id", 3L);
        ReflectionTestUtils.setField(수서역, "id", 4L);
    }

    @DisplayName("노선의 역 목록을 가져온다.")
    @Test
    void getStations() {
        // when
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);

        // then
        assertThat(분당선.getStations()).contains(태평역, 가천대역);
    }

    @DisplayName("노선에 구간을 등록한다: 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_case1() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);

        // when
        분당선.addSection(가천대역, 복정역, 3);

        // then
        assertThat(분당선.getStations()).isEqualTo(Arrays.asList(태평역, 가천대역, 복정역));
    }

    @DisplayName("노선에 구간을 등록한다: 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_case2() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 가천대역, 복정역, 3);

        // when
        분당선.addSection(태평역, 가천대역, 3);

        // then
        assertThat(분당선.getStations()).isEqualTo(Arrays.asList(태평역, 가천대역, 복정역));
    }

    @DisplayName("노선에 구간을 등록한다: 역 사이에 새로운 역을 등록할 경우 (1)")
    @Test
    void addSection_case3() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 복정역, 6);

        // when
        분당선.addSection(태평역, 가천대역, 3);

        // then
        assertThat(분당선.getStations()).isEqualTo(Arrays.asList(태평역, 가천대역, 복정역));
    }

    @DisplayName("노선에 구간을 등록한다: 역 사이에 새로운 역을 등록할 경우 (2)")
    @Test
    void addSection_case4() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 복정역, 6);

        // when
        분당선.addSection(가천대역, 복정역, 3);

        // then
        assertThat(분당선.getStations()).isEqualTo(Arrays.asList(태평역, 가천대역, 복정역));
    }

    @DisplayName("구간 등록 예외: 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionException_case1() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 복정역, 3);

        // when - then
        assertThatExceptionOfType(IsMoreThanDistanceException.class)
            .isThrownBy(() -> 분당선.addSection(가천대역, 복정역, 6));

        // when - then
        assertThatExceptionOfType(IsMoreThanDistanceException.class)
            .isThrownBy(() -> 분당선.addSection(태평역, 가천대역, 6));

    }

    @DisplayName("구간 등록 예외: 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionException_case2() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 6);

        // when - then
        assertThatExceptionOfType(IsExistedSectionException.class)
            .isThrownBy(() -> 분당선.addSection(태평역, 가천대역, 3));
    }

    @DisplayName("구간 등록 예외: 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionException_case3() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 수서역, 10);

        // when - then
        assertThatExceptionOfType(DoseNotExistStationOfSectionException.class)
            .isThrownBy(() -> 분당선.addSection(가천대역, 복정역, 3));
    }

    @DisplayName("구간 삭제: 기점 제거")
    @Test
    void removeSectionThatFirst() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 3);

        // when
        분당선.removeSection(태평역.getId());

        // then
        assertThat(분당선.getStations()).doesNotContain(태평역);
        assertThat(분당선.getDistance()).isEqualTo(3);

    }

    @DisplayName("구간 삭제: 종점 제거")
    @Test
    void removeSectionThatLast() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 3);

        // when
        분당선.removeSection(복정역.getId());

        // then
        assertThat(분당선.getStations()).doesNotContain(복정역);
        assertThat(분당선.getDistance()).isEqualTo(3);

    }

    @DisplayName("구간 삭제: 사이역 제거")
    @Test
    void removeSectionThatMiddle() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 3);

        // when
        분당선.removeSection(가천대역.getId());

        // then
        List<Station> stations = 분당선.getStations();
        assertThat(분당선.getStations()).doesNotContain(가천대역);
        assertThat(분당선.getDistance()).isEqualTo(6);

    }

    @DisplayName("구간 삭제 예외: 구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionExceptionThatIsOnlyOne() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);

        // when - then
        assertThatExceptionOfType(HaveOnlyOneSectionException.class)
            .isThrownBy(() -> 분당선.removeSection(가천대역.getId()));
    }

    @DisplayName("구간 삭제 예외: 노선에 없는 역을 삭제 시 에러 발생")
    @Test
    void removeSectionExceptionThatDoesNotExist() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 3);

        // when - then
        assertThatExceptionOfType(DoseNotExistStationOfSectionException.class)
            .isThrownBy(() -> 분당선.removeSection(수서역.getId()));
    }
}
