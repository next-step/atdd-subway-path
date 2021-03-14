package nextstep.subway.line.domain;

import nextstep.subway.line.exception.HaveOnlyOneSectionException;
import nextstep.subway.line.exception.IsDownStationExistedException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);

        // when
        분당선.addSection(가천대역, 복정역, 3);

        // then
        assertThat(분당선.getStations()).contains(태평역, 가천대역, 복정역);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 3);

        // when - then
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> 분당선.addSection(가천대역, 수서역, 3));

    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);

        // when - then
        assertThatExceptionOfType(IsDownStationExistedException.class)
            .isThrownBy(() -> 분당선.addSection(가천대역, 태평역, 3));
    }

    @DisplayName("노선에서 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);
        분당선.addSection(가천대역, 복정역, 3);

        // when
        분당선.removeSection(복정역.getId());

        // then
        assertThat(분당선.getStations()).doesNotContain(복정역);

    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // given
        Line 분당선 = new Line("분당선", "yellow", 태평역, 가천대역, 3);

        // when - then
        assertThatExceptionOfType(HaveOnlyOneSectionException.class)
            .isThrownBy(() -> 분당선.removeSection(가천대역.getId()));
    }
}
