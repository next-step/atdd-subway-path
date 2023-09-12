package subway.db.h2.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;
import subway.db.h2.entity.StationJpa;
import subway.domain.PathStation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 경로 내 지하철역 도메인 모델 변환 유닛 테스트를 합니다.
 */
@UnitTest
@DisplayName("경로 내 지하철역 도메인 모델 변환 유닛 테스트")
class PathStationMapperTest {

    private final PathStationMapper mapper = new PathStationMapper();

    /**
     * @given 지하철역 영속성 모델이 아직 DB에 저장되지 않았다면
     * @when 경로 내 지하철역 도메인 모델로 변환할 때
     * @then 예외를 반환한다.
     */
    @Test
    @DisplayName("지하철역 영속성 모델이 DB에 저장되어있지 않다면 경로 내 지하철역 도메인 모델로 변환할 수 없다.")
    void cantMapWhenStationJpaDontSave() {
        //given
        StationJpa 강남역 = StationJpa.of("강남역");

        //when
        Throwable throwable = catchThrowable(() -> mapper.from(강남역));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("강남역은 아직 저장되지 않았습니다.");

    }

    /**
     * @given 지하철역 영속성 모델이 주어졌다면
     * @when 경로 내 지하철역 도메인 모델로 변환할 때
     * @then 경로 내 지하철역 도메인 모델을 반환한다.
     */
    @Test
    @DisplayName("지하철역 영속성 모델을 경로 내 지하철역 도메인 모델로 변환 가능하다.")
    void mapToPathStation() {
        //given
        StationJpa stationJpa = StationJpa.of(1L, "강남역");
        //when
        PathStation pathStation = mapper.from(stationJpa);

        //then
        assertThat(pathStation)
                .extracting("id", "name")
                .containsExactly(PathStation.Id.of(1L), "강남역");
    }
}