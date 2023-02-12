package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionsTest {

    private Line 이호선;

    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;

    @BeforeEach
    void SetUp(){
        이호선 = new Line("2호선", "green");

        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
    }

    @DisplayName("성공: 길이가 10보다 작은 구간 추가")
    @ParameterizedTest(name="{index} => {0}길이 구간 추가")
    @ValueSource(ints = {3,6,9})
    void success_add_section(int distance){
        Sections sections = new Sections();
        sections.add(new Section(이호선, 강남역, 잠실역, 10));
        sections.add(new Section(이호선, 강남역, 삼성역, distance));

        assertThat(sections.getStations())
                .extracting(Station::getName)
                .containsExactly("강남역", "삼성역", "잠실역");
    }

    @DisplayName("실패: 길이가 10과 같거나 큰 구간 추가")
    @ParameterizedTest(name="{index} => {0}길이 구간 추가")
    @ValueSource(ints = {10,12,15})
    void fail_add_section(int distance){
        Sections sections = new Sections();
        sections.add(new Section(이호선, 강남역, 잠실역, 10));

        assertThrows(DataIntegrityViolationException.class, ()-> {
            sections.add(new Section(이호선, 강남역, 삼성역, distance));
        });
    }
}
