package subway.db.h2.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.application.response.SubwayLineResponse;
import subway.common.annotation.UnitTest;
import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.entity.SubwaySectionJpa;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * 지하철 노선 응답 객체 변환을 테스트하기 위한 클래스 입니다.
 */
@UnitTest
@DisplayName("지하철 노선 응답 객체 매퍼 단위 테스트")
class SubwayLineResponseMapperTest {

    private final SubwayLineResponseMapper subwayLineResponseMapper = new SubwayLineResponseMapper();

    /**
     * @given 지하철 노선 영속성 객체가 존재한다면
     * @when 지하철 노선 응답 객체로 변환할 때
     * @then 값이 일치된 채로 지하철 노선 응답 객체가 반환된다.
     */
    @Test
    @DisplayName("영속성 객체를 응답 객체로 변환할 때 값이 일치한다.")
    void returnSubwayLineJpa() {
        //given
        SubwaySectionJpa subwaySectionJpa1 = SubwaySectionJpa.of(1L, 2L, "강남역", 3L, "교대역", BigDecimal.valueOf(1L));
        SubwaySectionJpa subwaySectionJpa2 = SubwaySectionJpa.of(2L, 1L, "신림역", 2L, "강남역", BigDecimal.valueOf(1L));

        SubwayLineJpa subwayLineJpa = new SubwayLineJpa(1L, "테스트", "red", 1L, List.of(subwaySectionJpa1, subwaySectionJpa2));
        //when
        SubwayLineResponse response = subwayLineResponseMapper.from(subwayLineJpa);

        //then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(1L),
                () -> assertThat(response.getName()).isEqualTo("테스트"),
                () -> assertThat(response.getColor()).isEqualTo("red"));
    }

    /**
     * @given 지하철 노선 영속성 객체가 존재한다면
     * @when 지하철 노선 응답 객체로 변환할 때
     * @then 역 목록이 종점 순으로 정렬되어 반환된다.
     */
    @Test
    @DisplayName("영속성 객체를 응답 객체로 변환할 때 역 목록이 종점 순으로 정렬되어 반환된다.")
    void orderedStations() {
        //given
        SubwaySectionJpa subwaySectionJpa1 = SubwaySectionJpa.of(1L, 1L, "강남역", 2L, "교대역", BigDecimal.valueOf(1L));
        SubwaySectionJpa subwaySectionJpa2 = SubwaySectionJpa.of(2L, 3L, "신림역", 1L, "강남역", BigDecimal.valueOf(1L));

        SubwayLineJpa subwayLineJpa = new SubwayLineJpa(1L, "테스트", "red", 3L, List.of(subwaySectionJpa1, subwaySectionJpa2));
        //when
        SubwayLineResponse response = subwayLineResponseMapper.from(subwayLineJpa);

        //then
        assertThat(response.getStations())
                .extracting(SubwayLineResponse.StationInfo::getId, SubwayLineResponse.StationInfo::getName)
                .containsExactly(
                        tuple(3L, "신림역"),
                        tuple(1L, "강남역"),
                        tuple(2L, "교대역"));
    }

}