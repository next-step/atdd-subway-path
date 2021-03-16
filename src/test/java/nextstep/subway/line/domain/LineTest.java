package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private Station 잠실역;
    private Station 강변역;
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
        잠실역 = new Station("잠실역");
        ReflectionTestUtils.setField(잠실역, "id", 4L);
        강변역 = new Station("강변역");
        ReflectionTestUtils.setField(강변역, "id", 5L);
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
    }

    @Test
    void getStations() {
        // when
        List<Station> stations = 이호선.getSections().getStations();

        // then
        assertThat(stations).containsExactly(Arrays.array(강남역, 역삼역));
    }

    @DisplayName("외부에 구간추가 - 기존 구간의 하행역과 신규 구간의 상행역이 같을 경우")
    @Test
    void addSection1() {
        // when
        이호선.addSection(역삼역, 교대역, 20);

        // then
        assertThat(이호선.getSections().getStations()).containsExactly(Arrays.array(강남역, 역삼역, 교대역));
    }

    @DisplayName("외부에 구간추가 - 기존 구간의 상행역과 신규 구간의 하행역이 같을 경우")
    @Test
    void addSection2() {
        // when
        이호선.addSection(교대역, 강남역, 20);

        // then
        assertThat(이호선.getSections().getStations()).containsExactly(Arrays.array(교대역, 강남역, 역삼역));
    }

    @DisplayName("기존 구간 중간에 역 추가 - 기존 구간의 상행역과 새로 추가된 구간의 상행역이 같을 경우")
    @Test
    void addSectionInMiddle1() {
        // when
        이호선.addSection(강남역, 교대역, 5);

        // then
        assertThat(이호선.getSections().getStations()).containsExactly(Arrays.array(강남역, 교대역, 역삼역));
    }

    @DisplayName("기존 구간 중간에 역 추가 - 기존 구간의 하행역과 새로 추가된 구간의 하행역이 같을 경우")
    @Test
    void addSectionInMiddle2() {
        // when
        이호선.addSection(교대역, 역삼역, 5);

        // then
        assertThat(이호선.getSections().getStations()).containsExactly(Arrays.array(강남역, 교대역, 역삼역));
    }

    @DisplayName("중간에 들어가는 새로운 구간의 길이가 기존 구간 길이와 같거나 더 크면 에러발생")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, Integer.MAX_VALUE})
    void addSectionInMiddleOverDistance(int distance) {
        // when/then
        assertThatThrownBy(()->{
            이호선.addSection(교대역, 역삼역, distance);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("새로운 구간의 길이가 너무 길어서 등록할 수 없습니다.");
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncludedStation() {
        // when/then
        assertThatThrownBy(()->{
            이호선.addSection(강남역, 역삼역, 5);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @DisplayName("노선에 존재하지 않는 역들을 구간으로 추가시 에러 발생")
    @Test
    void addSectionNotIncludedStation() {
        // when/then
        assertThatThrownBy(()->{
            이호선.addSection(잠실역, 강변역, 5);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("일치하는 상행역 혹은 하행역이 구간에 없습니다.");
    }

    @Test
    void removeSection() {
        // given
        이호선.addSection(역삼역, 교대역, 20);

        // when
        이호선.removeSection(교대역.getId());

        // then
        assertThat(이호선.getSections().getSections()).hasSize(1);
        assertThat(이호선.getSections().getSections().get(0).getDownStation()).isEqualTo(역삼역);
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
