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
        ReflectionTestUtils.setField(강남역, "id", 1L);
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        삼성역 = new Station("삼성역");
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        잠실새내역 = new Station("잠실새내역");
        ReflectionTestUtils.setField(잠실새내역, "id", 4L);
        잠실역 = new Station("잠실역");
        ReflectionTestUtils.setField(잠실역, "id", 5L);

        이호선 = new Line("2호선", "green-001", 강남역, 역삼역, 12);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @Test
    void getStations() {
        assertThat(이호선.getAllStations().get(0).getName()).isEqualTo(강남역.getName());
    }

    @Test
    void addSection() {
        //when
        이호선.addSection(역삼역, 삼성역, 7);

        //then
        assertThat(이호선.getLastStation().getId()).isEqualTo(삼성역.getId());
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        //given
        이호선.addSection(역삼역, 삼성역, 7);

        //when-then
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->이호선.addSection(강남역, 잠실새내역, 7));
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        //when-then
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->이호선.addSection(강남역, 역삼역, 2));
    }

    @Test
    void removeSection() {
        //given
        이호선.addSection(역삼역, 삼성역, 7);

        //when
        이호선.removeSection(삼성역.getId());

        //then
        assertThat(이호선.getLastStation().getId()).isEqualTo(역삼역.getId());
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        //when - then
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(()->이호선.removeSection(역삼역.getId()));
    }
}
