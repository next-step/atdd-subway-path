package nextstep.subway.line.domain;

import nextstep.subway.exception.NotEqualsLastStationException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LineTest {

    private Station 강남역 = new Station("강남역");
    private Station 역삼역 = new Station("역삼역");
    private Station 삼성역 = new Station("삼성역");
    private Station 잠실새내역 = new Station("잠실새내역");
    private Station 잠실역 = new Station("잠실역");
    private Line 이호선;

    @BeforeEach
    void setUp() {
        //given
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        잠실새내역 = new Station("잠실새내역");
        잠실역 = new Station("잠실역");

        이호선 = new Line("2호선", "green-001", 강남역, 역삼역, 12);
    }

    @DisplayName("등록된 역 조회")
    @Test
    void getStations() {
        assertThat(이호선.getAllStations().get(0)).isEqualTo(강남역);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        //when
        이호선.addSection(역삼역, 삼성역, 7);

        //then
        assertThat(이호선.getAllStations()).containsOnlyOnce(삼성역);
    }

    @DisplayName("목록 중간에 추가 가능(신규요건)")
    @Test
    void addSectionInMiddle() {
        //given
        이호선.addSection(역삼역, 삼성역, 7);

        //when
        이호선.addSection(강남역, 잠실새내역, 7);

        // then
        assertEquals(이호선.getAllStations().size(), 4);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        //when-then
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->이호선.addSection(강남역, 역삼역, 2));
    }

    @DisplayName("하나의 구간 제거(중간역도 제거가능)")
    @Test
    void removeSection() {
        //given 강남역-역삼역-삼성역
        이호선.addSection(역삼역, 삼성역, 7);

        //when
        이호선.removeSection(역삼역);

        //then
        assertEquals(이호선.getAllStations().size(), 2);
    }

    @DisplayName("종점역(하행역=마지막구간) 제거")
    @Test
    void removeLastSection() {
        //given 강남역-역삼역-삼성역
        이호선.addSection(역삼역, 삼성역, 7);

        //when
        이호선.removeSection(삼성역);

        //then
        assertEquals(이호선.getAllStations().size(), 2);
    }

    @DisplayName("상행역(첫구간) 제거")
    @Test
    void removeFirstSection() {
        //given 강남역-역삼역-삼성역
        이호선.addSection(역삼역, 삼성역, 7);

        //when
        이호선.removeSection(강남역);

        //then
        assertEquals(이호선.getAllStations().size(), 2);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        //when - then
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->이호선.removeSection(역삼역));
    }

}
