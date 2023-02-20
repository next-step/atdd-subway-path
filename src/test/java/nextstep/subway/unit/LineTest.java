package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.SectionExceptionMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 도메인 객체 단위 테스트")
class LineTest {

    private Line 서울2호선 = new Line("2호선", "green");
    private Station 신촌역 = new Station("신촌역");
    private Station 당산역 = new Station("당산역");
    private Station 신도림역 = new Station("신도림역");
    private Station 신림역 = new Station("신림역");
    private Section section;

    // given
    @BeforeEach
    void setUp() {
        section = new Section(서울2호선, 당산역, 신도림역, 20);
        서울2호선.addSection(section);
    }

    @DisplayName("구간 추가 - 정상1 : 새로운 노선에 구간을 추가")
    @Test
    void addSection_case1() {
        // then
        assertThat(서울2호선.getSectionsCount()).isEqualTo(1);

        Section firstSection = 서울2호선.getFirstSection();
        assertThat(firstSection).isEqualTo(section);
    }

    @DisplayName("구간 추가 - 정상2 : 기존 구간의 하행, 상행역 기준으로 새로운 구간 추가")
    @Test
    void addSection_case2() {
        // when
        서울2호선.addSection(new Section(서울2호선, 신도림역, 신림역, 10));
        서울2호선.addSection(new Section(서울2호선, 신촌역, 당산역, 5));

        // then
        assertThat(서울2호선.getStations()).containsExactly(신촌역, 당산역, 신도림역, 신림역);
    }

    @DisplayName("구간 추가 - 정상3 : 기존 구간 사이에 새로운 구간 추가")
    @Test
    void addSection_case3() {
        // when
        서울2호선.addSection(new Section(서울2호선, 당산역, 신촌역, 10));

        // then
        assertThat(서울2호선.getStations()).containsExactly(당산역, 신촌역, 신도림역);
    }

    @DisplayName("구간 추가 - 예외1 : 기존 구간 사이에 추가되는 새로운 구간의 길이가 기존 구간 길이 이상인 경우")
    @Test
    void addSection_InvalidCase1() {
        // when - then
        assertThatThrownBy(() -> 서울2호선.addSection(new Section(서울2호선, 당산역, 신촌역, 30)))
                .isInstanceOf(DataIntegrityViolationException.class).hasMessage(SectionExceptionMessages.INVALID_DISTANCE);
    }

    @DisplayName("구간 추가 - 예외2 : 새로운 구간의 상,하행역 모두 이미 존재하는 경우")
    @Test
    void addSection_InvalidCase2() {
        // when - then
        assertThatThrownBy(() -> 서울2호선.addSection(new Section(서울2호선, 당산역, 신도림역, 10)))
                .isInstanceOf(DataIntegrityViolationException.class).hasMessage(SectionExceptionMessages.ALREADY_EXIST);

        assertThatThrownBy(() -> 서울2호선.addSection(new Section(서울2호선, 신도림역, 당산역, 10)))
                .isInstanceOf(DataIntegrityViolationException.class).hasMessage(SectionExceptionMessages.ALREADY_EXIST);
    }

    @DisplayName("구간 추가 - 예외3 : 새로운 구간의 상,하행역 모두 존재하지 않는 경우")
    @Test
    void addSection_InvalidCase3() {
        // when - then
        assertThatThrownBy(() -> 서울2호선.addSection(new Section(서울2호선, 신촌역, 신림역, 10)))
                .isInstanceOf(DataIntegrityViolationException.class).hasMessage(SectionExceptionMessages.NOTHING_EXIST);
    }

    @DisplayName("구간 제거 - 정상1 : 상행 종점역 제거")
    @Test
    void removeSection() {
        // given
        서울2호선.addSection(new Section(서울2호선, 신도림역, 신림역, 10));
        assertThat(서울2호선.getSectionsCount()).isEqualTo(2);
        assertThat(서울2호선.getStations()).containsExactly(당산역, 신도림역, 신림역);

        // when
        서울2호선.removeSection(당산역);

        // then
        assertThat(서울2호선.getSectionsCount()).isEqualTo(1);
        assertThat(서울2호선.getStations()).containsExactly(신도림역, 신림역);
    }

}
