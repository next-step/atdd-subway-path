package nextstep.subway.section;

import static common.Constants.신논현역;
import static common.Constants.지하철역;
import static nextstep.subway.section.SectionBuilder.aSection;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("구간 관련 기능 단위테스트")
class SectionTest {

    @DisplayName("update() : 상행역이 겹치면 거리를 수정하고 상행역을 입력된 상행역으로 수정한다")
    @Test
    void update_sameUpStation() {
        Section oleSection = aSection().withDownStation(new Station(3L, 지하철역)).build();
        Section newSection = aSection().withDownStation(new Station(2L, 신논현역))
            .withDistance(3).build();

        oleSection.update(newSection);

        assertAll(
            () -> assertThat(oleSection.getDistance()).isEqualTo(7),
            () -> assertThat(oleSection.getUpStation().getId()).isEqualTo(2L),
            () -> assertThat(oleSection.getDownStation().getId()).isEqualTo(3L),

            () -> assertThat(newSection.getDistance()).isEqualTo(3),
            () -> assertThat(newSection.getUpStation().getId()).isEqualTo(1L),
            () -> assertThat(newSection.getDownStation().getId()).isEqualTo(2L)
        );
    }

    @DisplayName("update() : 거리를 수정하고 하행역이 겹치면 하행역을 입력된 상행역으로 수정한다")
    @Test
    void update_sameDownStation() {
        Section oleSection = aSection().withDownStation(new Station(3L, 지하철역)).build();
        Section newSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역))
            .withDistance(3).build();

        oleSection.update(newSection);

        assertAll(
            () -> assertThat(oleSection.getDistance()).isEqualTo(7),
            () -> assertThat(oleSection.getUpStation().getId()).isEqualTo(1L),
            () -> assertThat(oleSection.getDownStation().getId()).isEqualTo(2L),

            () -> assertThat(newSection.getDistance()).isEqualTo(3),
            () -> assertThat(newSection.getUpStation().getId()).isEqualTo(2L),
            () -> assertThat(newSection.getDownStation().getId()).isEqualTo(3L)
        );
    }

    @DisplayName("update() : 입력된 구간의 거리가 더 크면 실패한다")
    @ParameterizedTest
    @ValueSource(ints = {10, 12, 13})
    void update_fail_distanceOver(int distance) {
        Section oleSection = aSection().withDownStation(new Station(3L, 지하철역)).build();
        Section newSection = aSection().withStations(new Station(2L, 신논현역), new Station(3L, 지하철역))
            .withDistance(distance).build();

        assertThatThrownBy(() -> oleSection.update(newSection))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 합니다.");
    }

    @DisplayName("merge() : 파라미터로 받은 역이 포함된 구간을 삭제하면 해당 역의 후행역을 삭제된 역의 구간의 후행역으로 변경한다")
    @Test
    void merge() {
        Section prevSection = aSection().build();
        Section deleteTargetSection = aSection()
            .withUpStation(new Station(2L, 신논현역))
            .withDownStation(new Station(3L, 지하철역))
            .build();

        prevSection.delete(deleteTargetSection.getDownStation(), deleteTargetSection.getDistance());

        assertThat(prevSection.getDownStation().getId()).isEqualTo(3L);
        assertThat(prevSection.getDistance()).isEqualTo(20);
    }
}