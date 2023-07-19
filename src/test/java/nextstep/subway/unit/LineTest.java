package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    private Station 첫번째역;
    private Station 두번째역;
    private Station 세번째역;
    private Line 첫번째노선;
    @BeforeEach
    public void setup(){
        첫번째역 = new Station(1L,"첫번째역");
        두번째역 = new Station(2L,"두번째역");
        세번째역 = new Station(3L,"세번째역");
        첫번째노선 = Line.of("첫번째노선","BLUE", 첫번째역, 두번째역, 10L);
    }
    @DisplayName("도메인(Line): 새로운 구간을 추가")
    @Test
    void addSection() {
        //When
        첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
        Section section = 첫번째노선.getSections().getLastSection();
        //Then
        assertThat(section.getDistance()).isEqualTo(10L);
        assertThat(section.getUpStation()).isEqualTo(두번째역);
        assertThat(section.getDownStation()).isEqualTo(세번째역);
    }
    @DisplayName("도메인(Line): 노선에 속해있는 역들에 대한 조회")
    @Test
    void getStations() {
        //When
        첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
        //Then
        assertThat(첫번째노선.getStations()).containsExactly(첫번째역, 두번째역, 세번째역);
    }
    @DisplayName("도메인(Line): 구간의 마지막 역을 삭제")
    @Test
    void removeSection() {
        //Given
        첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
        //When
        첫번째노선.deleteSection(세번째역);
        //Then
        assertThat(첫번째노선.getStations().stream().map(station -> station.getName())).containsExactly(첫번째역.getName(), 두번째역.getName());
    }
}
