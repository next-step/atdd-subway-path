package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotFindPathException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.unit.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("최단 경로 조횜")
class PathTest {

    @DisplayName("도착역 또는 출발역이 없는 경우 경로 조회 실패")
    @ParameterizedTest(name = "없는 역의 경로 조회 [{index}] [{arguments}]")
    @MethodSource
    void notExistStationException(Station 출발역, Station 도착역, String expectedMessage) {
        //given
        List<Section> sections = getSections();

        //when
        ThrowableAssert.ThrowingCallable actual = () -> new Path(sections, 출발역, 도착역);

        //then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> notExistStationException() {
        return Stream.of(
                Arguments.of(교대역, null, "도착역이 없는 경우 경로를 조회할 수 없습니다."),
                Arguments.of(null, 교대역, "출발역이 없는 경우 경로를 조회할 수 없습니다.")
        );
    }

    private List<Section> getSections() {
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
        return lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList());
    }
}