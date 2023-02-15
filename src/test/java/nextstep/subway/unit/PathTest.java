package nextstep.subway.unit;

import nextstep.subway.common.exception.NoRegisterStationException;
import nextstep.subway.common.exception.SameStationException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static nextstep.subway.common.error.SubwayError.NO_FIND_SAME_SOURCE_TARGET_STATION;
import static nextstep.subway.common.error.SubwayError.NO_REGISTER_LINE_STATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 부평역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {

        교대역 = 역_생성(1L, "교대역");
        강남역 = 역_생성(2L, "강남역");
        양재역 = 역_생성(3L, "양재역");
        남부터미널역 = 역_생성(4L, "남부터미널역");
        부평역 = 역_생성(5L, "부평역");

        이호선 = 노선_생성(1L, "2호선", "green", 교대역, 강남역, 10);
        신분당선 = 노선_생성(2L, "신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선_생성(3L, "3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("출발역과 도착역이 같아서 조회가 불가능합니다.")
    @Test
    void error_sameSourceNTarget() {

        final List<Line> 전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);

        assertThatThrownBy(() -> Path.of(전체_노선_목록, 교대역, 교대역))
                .isInstanceOf(SameStationException.class)
                .hasMessage(NO_FIND_SAME_SOURCE_TARGET_STATION.getMessage());
    }

    @DisplayName("요청한 역이 노선의 등록되어 있지 않습니다.")
    @Test
    void error_noRegisterStation() {

        final List<Line> 전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);

        assertThatThrownBy(() -> Path.of(전체_노선_목록, 부평역, 교대역))
                .isInstanceOf(NoRegisterStationException.class)
                .hasMessage(NO_REGISTER_LINE_STATION.getMessage());
    }

    private Line 노선_생성(final Long id, final String name, final String color, final Station upStation, final Station downStation, final Integer distance) {
        final Line line = new Line(name, color, upStation, downStation, distance);
        ReflectionTestUtils.setField(line, "id", id);
        return line;
    }

    private Station 역_생성(final Long id, final String name) {
        final Station 역 = new Station(name);
        ReflectionTestUtils.setField(역, "id", id);
        return 역;
    }
}