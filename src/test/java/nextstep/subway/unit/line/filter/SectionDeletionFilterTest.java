package nextstep.subway.unit.line.filter;

import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.deletion.filter.SingularSectionExistFilter;
import nextstep.subway.line.domain.entity.deletion.filter.StationNotExistFilter;
import nextstep.subway.station.entity.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 삭제 필터 관련 기능")
public class SectionDeletionFilterTest {

    final int SECTION_DEFAULT_DISTANCE = 10;

    Station 강남역;
    Station 역삼역;
    Station 익명역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        익명역 = new Station(4L, "익명역");
        이호선 = new Line("이호선", "Green", SECTION_DEFAULT_DISTANCE, 강남역, 역삼역);
    }

    @DisplayName("존재하지 않는 역 제거")
    @Test
    void deletionFailedByNonExistingStation() {
        Assertions.assertThatThrownBy(() -> new StationNotExistFilter().doFilter(이호선.getSections(), 익명역))
                .isInstanceOf(DeletionValidationException.class);
    }

    @DisplayName("구간이 하나인 역 제거")
    @Test
    void deletionFailedByOnlyOneSectionExists() {
        Assertions.assertThatThrownBy(() -> new SingularSectionExistFilter().doFilter(이호선.getSections(), 역삼역))
                .isInstanceOf(DeletionValidationException.class);
    }
}
