package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        // given
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        교대역 = new Station("교대역");
        ReflectionTestUtils.setField(교대역, "id", 3L);
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
    }

    @Test
    void getStations() {
        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(Arrays.array(강남역, 역삼역));
    }

    @Test
    void addSection() {
        // when
        이호선.addSection(역삼역, 교대역, 20);

        // then
        assertThat(이호선.getSections()).hasSize(2);
        assertThat(이호선.getSections().get(1).getDownStation()).isEqualTo(교대역);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        // when/then
        assertThatThrownBy(()->{
            이호선.addSection(교대역, 역삼역, 5);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        // when/then
        assertThatThrownBy(()->{
            이호선.addSection(강남역, 역삼역, 5);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void removeSection() {
        // given
        이호선.addSection(역삼역, 교대역, 20);

        // when
        이호선.removeSection(교대역.getId());

        // then
        assertThat(이호선.getSections()).hasSize(1);
        assertThat(이호선.getSections().get(0).getDownStation()).isEqualTo(역삼역);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // when/then
        assertThatThrownBy(()->{
            이호선.removeSection(역삼역.getId());
        }).isInstanceOf(RuntimeException.class);
    }
}
