package subway.db.h2.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.domain.Kilometer;
import subway.domain.PathSection;
import subway.domain.PathStation;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 경로 구간 도메인 모델 변환 유닛 테스트를 합니다.
 */
@UnitTest
@DisplayName("경로 구간 도메인 모델 변환 유닛 테스트")
class PathSectionMapperTest {

    private final PathSectionMapper mapper = new PathSectionMapper();

    /**
     * @given 지하철 구간 영속성 모델이 아직 DB에 저장되지 않았다면
     * @when 경로 구간 도메인 모델로 변환할 때
     * @then 예외를 반환한다.
     */
    @Test
    @DisplayName("지하철 구간 영속성 모델이 DB에 저장되어있지 않다면 경로 구간 도메인 모델로 변환할 수 없다.")
    void cantMapToPathSectionWhenDontSaveYet() {
        //given
        SubwaySectionJpa subwaySectionJpa = SubwaySectionJpa.register(
                1L,
                "강남역",
                2L,
                "역삼역",
                new BigDecimal(10));

        //when
        Throwable throwable = catchThrowable(() -> mapper.from(subwaySectionJpa));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 구간 영속성 모델이 DB에 저장되어있지 않습니다.");
    }

    /**
     * @given 지하철 구간 영속성 모델이 주어진다면
     * @when 경로 구간 도메인 모델로 변환할 때
     * @then 경로 구간 도메인 모델을 반환한다.
     */
    @Test
    @DisplayName("지하철 구간 영속성 모델을 경로 구간 도메인 모델로 변환 가능하다.")
    void mapToPathSection() {
        //given
        SubwaySectionJpa subwaySectionJpa = SubwaySectionJpa.of(
                1L,
                1L,
                "강남역",
                2L,
                "역삼역",
                BigDecimal.valueOf(10));

        //when
        PathSection pathSection = mapper.from(subwaySectionJpa);

        //then
        assertThat(pathSection).isNotNull()
                        .extracting("id", "upStation.id", "upStation.name", "downStation.id", "downStation.name", "distance")
                        .containsExactly(PathSection.Id.of(1L), PathStation.Id.of(1L), "강남역", PathStation.Id.of(2L), "역삼역", Kilometer.of(10));

    }
}