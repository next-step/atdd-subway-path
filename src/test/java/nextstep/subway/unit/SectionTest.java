package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {
    private Line 이호선;

    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;

    @BeforeEach
    void SetUp() {
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
    }

    @DisplayName("중간역이 제거될 경우 구간 재배치")
    @Test
    void merge_section() {
        Section prev = new Section(이호선, 강남역, 삼성역, 10);
        Section next = new Section(이호선, 삼성역, 잠실역, 5);
        Section merged = prev.merge(next);

        assertAll(
                () -> assertThat(merged.getDistance()).isEqualTo(15),
                () -> assertThat(merged.getUpStation()).isEqualTo(강남역),
                () -> assertThat(merged.getDownStation()).isEqualTo(잠실역)
        );
    }
}
