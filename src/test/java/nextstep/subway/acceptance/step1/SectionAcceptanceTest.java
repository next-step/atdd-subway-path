package nextstep.subway.acceptance.step1;

import org.junit.jupiter.api.DisplayName;

@DisplayName("지하철 구간 추가 기능 개선 - 테스트")
public class SectionAcceptanceTest {

    /**
     * given 노선의 기존 구간(강남역-선릉역)이 존재할 때,
     * when 신규 구간(강남역-역삼역)을 추가하면
     * then 총 2개의 구간(강남역-역삼역, 역삼역-선릉역)이 생긴다.
     */
    @DisplayName("기존 구간 사이에 새로운 구간 추가")
    void add_Between_Exist_Section() {

    }

    /**
     * given 노선의 기존 구간(강남역-역삼역)이 존재할 때,
     * when 신규 구간(교대역-강남역)을 추가하면
     * then 총 2개의 구간(교대역-강남역, 강남역-역삼역)이 생긴다.
     */
    @DisplayName("기존 구간에 추가 : 상행역 기준")
    void add_Before_UpStation() {

    }

    /**
     * given 노선의 기존 구간(강남역-역삼역)이 존재할 때,
     * when 신규 구간(역삼역-선릉역)을 추가하면
     * then 총 2개의 구간(강남역-역삼역, 역삼역-선릉역)이 생긴다.
     */
    @DisplayName("기존 구간에 추가 : 하행역 기준")
    void add_After_DownStation() {

    }

    /**
     * given 노선의 기존 구간(강남역-역삼역)이 존재할 때,
     * when 신규 구간(교대역-강남역)을 추가 후 노선을 조회하면
     * then 교대역-강남역-역삼역 순서로 조회된다.
     */
    @DisplayName("노선의 역을 구간 순서대로 조회")
    void get_Stations_In_Sequence() {

    }

    /**
     * given 노선의 기존 구간이 존재할 때,
     * when 기존 구간 사이에 신규 구간 추가시, 기존 구간과 신규 구간의 길이가 같다면
     * then 총 2개의 구간(강남역-역삼역, 역삼역-선릉역)이 생긴다.
     */
    @DisplayName("구간 추가 실패 : 구간 길이 동일할 때")
    void throw_Exception() {

    }
}


