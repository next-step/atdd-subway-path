package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.exception.CannotFindPathException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.unit.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    @DisplayName("도착역이 없는 경우 경로 조회 실패")
    @Test
    void notExistStartStationException() {
        //given
        List<Section> sections = getSections();

        //when
        ThrowableAssert.ThrowingCallable actual = () -> new Path(sections, 교대역, null);

        //then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotFindPathException.class);

    }

    private List<Section> getSections() {
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
        return lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList());
    }
}