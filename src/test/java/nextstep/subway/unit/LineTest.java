package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 잠실역;
    private Long 강남역삼구간거리;
    private Long 역삼선릉구간거리;
    private Long 선릉잠실구간거리;
    private Line 이호선;
    private Section 강남역삼구간;
    private Section 역삼선릉구간;
    private Section 선릉잠실구간;

    @BeforeEach
    public void setGivenData() {
        //given
        강남역 = Station.builder()
                .id(1L)
                .name("강남역")
                .build();
        역삼역 = Station.builder()
                .id(2L)
                .name("역삼역")
                .build();
        선릉역 = Station.builder()
                .id(3L)
                .name("선릉역")
                .build();
        잠실역 = Station.builder()
                .id(4L)
                .name("잠실역")
                .build();

        이호선 = new Line("2호선","Green");
        강남역삼구간거리 = 5L;
        역삼선릉구간거리 = 20L;
        선릉잠실구간거리 = 10L;

        강남역삼구간 = new Section(이호선, 강남역, 역삼역, 강남역삼구간거리);
        역삼선릉구간 = new Section(이호선, 역삼역, 선릉역, 역삼선릉구간거리);
        선릉잠실구간= new Section(이호선, 선릉역, 잠실역, 선릉잠실구간거리);

    }

    @DisplayName("새로운 역을 상행 종점으로 등록하고 새로운 역을 하행 종점으로 등록할 경우.")
    @Test
    void addSection1() {

        //when
        이호선.addSection(역삼선릉구간);
        이호선.addSection(강남역삼구간);
        이호선.addSection(선릉잠실구간);

        //then
        assertThat(이호선.getOrderedStationList()).containsExactly(강남역,역삼역,선릉역,잠실역);
        assertThat(이호선.getFirstUpStation()).isEqualTo(강남역);
        assertThat(이호선.getLastDownStation()).isEqualTo(잠실역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection2() {

        //given
        Station 삼성역 = Station.builder()
                .id(5L)
                .name("삼성역")
                .build();
        Station 종합운동장역 = Station.builder()
                .id(6L)
                .name("종합운동장역")
                .build();
        Section 선릉삼성구간 = new Section(이호선, 선릉역, 삼성역, 선릉잠실구간거리/2-1);
        Section 잠실운동장잠실구간 = new Section(이호선, 종합운동장역, 잠실역, 선릉잠실구간거리/2-1);

        이호선.addSection(선릉잠실구간);

        //when
        이호선.addSection(선릉삼성구간);
        이호선.addSection(잠실운동장잠실구간);

        //then
        assertThat(이호선.getOrderedStationList()).containsExactly(선릉역,삼성역,종합운동장역,잠실역);
        assertThat(이호선.getFirstUpStation()).isEqualTo(선릉역);
        assertThat(이호선.getLastDownStation()).isEqualTo(잠실역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void invalidAddSection1() {

        //given
        Station 삼성역 = Station.builder()
                .id(5L)
                .name("삼성역")
                .build();
        Station 종합운동장역 = Station.builder()
                .id(6L)
                .name("종합운동장역")
                .build();

        이호선.addSection(선릉잠실구간);

        //when
        Section 기존길이보다큰경우 = new Section(이호선, 선릉역, 삼성역, 선릉잠실구간거리+1);
        Section 기존길이와같은경우 = new Section(이호선, 종합운동장역, 잠실역, 선릉잠실구간거리);

        //then
        assertThatThrownBy(() ->  이호선.addSection(기존길이보다큰경우))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        assertThatThrownBy(() ->  이호선.addSection(기존길이와같은경우))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void invalidAddSection2() {
        //given
        Section 역삼강남구간 = new Section(이호선,역삼역 , 강남역, 강남역삼구간거리);
        이호선.addSection(강남역삼구간);
        //when
        Section invalidSection1 = 강남역삼구간;
        Section invalidSection2 = 역삼강남구간;

        //then
        assertThatThrownBy(() ->  이호선.addSection(invalidSection1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        assertThatThrownBy(() ->  이호선.addSection(invalidSection2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void invalidAddSection3() {
        //given
        Section 선릉잠실구간 = new Section(이호선,선릉역 , 잠실역, 선릉잠실구간거리);
        이호선.addSection(강남역삼구간);

        //when
        Section invalidSection1 = 선릉잠실구간;

        //then
        assertThatThrownBy(() ->  이호선.addSection(invalidSection1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
    }

}
