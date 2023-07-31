package nextstep.subway.unit;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;
import nextstep.subway.domain.delete.DeleteFirst;
import nextstep.subway.domain.delete.DeleteLast;
import nextstep.subway.domain.delete.DeleteMiddle;
import nextstep.subway.domain.delete.SectionDeleteType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionDeleteTypeTest {

    Stations stations = new Stations(List.of(신사역, 양재역, 양재시민의숲역));

    @ParameterizedTest
    @DisplayName("구간 삭제 방식 선택")
    @MethodSource("provideSections")
    void findAddType(Station station, Class strategy) {
        assertThat(SectionDeleteType.find(stations, station)).isInstanceOf(strategy);
    }

    public static Stream<Arguments> provideSections() {
        return Stream.of(
                Arguments.of(신사역, DeleteFirst.class),
                Arguments.of(양재역, DeleteMiddle.class),
                Arguments.of(양재시민의숲역, DeleteLast.class)
        );
    }
}
