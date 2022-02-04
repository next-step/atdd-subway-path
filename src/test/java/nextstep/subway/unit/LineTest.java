package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.IllegalSectionArgumentException;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    private Line line;
    private Station 수원역 = new Station("수원역");
    private Station 부산역 = new Station("부산역");

    @BeforeEach
    void setUp() {
        line = new Line("간선", "blue");
        line.addSections(수원역, 부산역, 10);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void 새로운_구간_추가() {
        //when
        Station 서울역 = new Station("서울역");
        line.addSections(부산역, 서울역, 1);

        //then
        assertThat(line.getStations()).containsExactly(수원역, 부산역, 서울역);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void 첫번째_구간_추가() {
        //when
        Station 서울역 = new Station("서울역");
        line.addSections(서울역, 수원역, 1);

        //then
        assertThat(line.getStations()).containsExactly(서울역, 수원역, 부산역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간을 추가할 경우")
    @Test
    void 상행_기준_두번째_구간_추가() {
        //when
        Station 서울역 = new Station("서울역");
        Station 부천역 = new Station("부천역");
        line.addSections(서울역, 수원역, 2);
        line.addSections(서울역, 부천역, 1);

        //then
        assertThat(line.getStations()).containsExactly(서울역, 부천역, 수원역, 부산역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간을 하행으로 추가할 경우")
    @Test
    void 하행_기준_두번째_구간_추가() {
        //when
        Station 서울역 = new Station("서울역");
        Station 부천역 = new Station("부천역");
        line.addSections(서울역, 수원역, 2);
        line.addSections(서울역, 부천역, 1);

        //then
        assertThat(line.getStations()).containsExactly(서울역, 부천역, 수원역, 부산역);
    }

    @DisplayName("구간 목록 두번째에 새로운 구간 추가시 distance가 기존의 구간보다 긴 경우 등록 실패")
    @Test
    void 기존구간보다_거리가_긴_구간_추가() {
        //when
        Station 서울역 = new Station("서울역");
        Station 부천역 = new Station("부천역");
        line.addSections(서울역, 수원역, 2);

        //then
        assertThrows(IllegalSectionArgumentException.class, () -> line.addSections(서울역, 부천역, 3));
    }

    @DisplayName("존재하지 않는 상행역과 하행역을 가지고 있는 구간 등록")
    @Test
    void 존재하지_않는_상행역_추가() {
        Station 서울역 = new Station("서울역");
        Station 부천역 = new Station("부천역");

        //then
        assertThrows(IllegalSectionArgumentException.class, () -> line.addSections(서울역, 부천역, 3));
    }

    @DisplayName("구간의 상행역과 하행역이 이미 노선에 등록되어 있는 구간 등록")
    @Test
    void 이미_노선에_상행역과_하행역이_등록되어있는_구간_추가() {
        assertThrows(IllegalSectionArgumentException.class, () -> line.addSections(수원역, 부산역, 3));
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        //then
        assertThat(line.getStations()).containsExactly(수원역, 부산역);
    }

    @DisplayName("노선 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        //given
        Station 서울역 = new Station("서울역");
        line.addSections(부산역, 서울역, 1);

        //when
        line.removeSection(서울역);

        //then
        assertThat(line.getStations()).containsExactly(수원역, 부산역);
    }

    @DisplayName("노선에서 중간역 삭제")
    @Test
    void 중간역_삭제() {
        Station 서울역 = new Station("서울역");
        Station 남서울역 = new Station("남서울역");
        line.addSections(부산역, 서울역, 5);
        line.addSections(부산역, 남서울역, 1);

        //when
        line.removeSection(부산역);

        //then
        assertThat(line.getStations()).containsExactly(수원역, 남서울역, 서울역);
    }

    @DisplayName("노선에서 첫번째역 삭제")
    @Test
    void 첫번째역_삭제() {
        Station 서울역 = new Station("서울역");
        line.addSections(부산역, 서울역, 5);

        //when
        line.removeSection(수원역);

        //then
        assertThat(line.getStations()).containsExactly(부산역, 서울역);
    }

    @DisplayName("노선에서 존재하지 않는 역을 삭제")
    @Test
    void 존재하지않는역_삭제() {
        //given
        Station 서울역 = new Station("서울역");

        //when then
        assertThrows(IllegalSectionArgumentException.class, () -> line.removeSection(서울역));
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 삭제")
    @Test
    void 구간이_하나인_노선에서_마지막_구간_삭제() {
        assertThrows(IllegalSectionArgumentException.class, () -> line.removeSection(수원역));
    }

}
