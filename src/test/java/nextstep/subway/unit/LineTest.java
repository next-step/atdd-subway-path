package nextstep.subway.unit;

import static nextstep.subway.common.LineSomething.DISTANCE_BASIC;
import static nextstep.subway.common.LineSomething.DISTANCE_INVALID;
import static nextstep.subway.common.LineSomething.DISTANCE_VALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.transaction.Transactional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

@Transactional
class LineTest {
    private Line 노선;

    private Long 노선Id = 1L;
    private Long 역1Id = 1L;
    private Long 역2Id = 2L;
    private Long 추가역Id = 3L;

    @BeforeEach
    void setUp() {
        노선에_구간이_한개_들어가있다();
    }

    @DisplayName("노선을 이름과 색상으로 생성한다.")
    @Test
    void 노선_생성_테스트_1() {
        final String 이름 = "노선이름";
        final String 색상 = "노선색상";
        노선 = Line.of(이름, 색상);

        assertThat(노선.getName()).isEqualTo(이름);
        assertThat(노선.getColor()).isEqualTo(색상);
    }

    @DisplayName("노선을 이름, 색상, 구간으로 생성한다.")
    @Test
    void 노선_생성_테스트_2() {
        final String 노선이름 = "노선이름";
        final String 노선색상 = "노선색상";
        final Station 상행역 = Station.of("상행역");
        final Station 하행역 = Station.of("하행역");
        final int 거리 = 10;

        Section 구간 = Section.of(상행역, 하행역, 거리);
        노선 = Line.of(노선이름, 노선색상, 구간);

        assertThat(노선.getName()).isEqualTo(노선이름);
        assertThat(노선.getColor()).isEqualTo(노선색상);
        assertThat(노선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("기존 지하철 노선 뒤에 구간 추가 성공하는 단순 케이스 (해피케이스)")
    @Test
    void addSection_성공케이스() {
        Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();

        when_구간추가한다_해피케이스: {
            노선.addSection(추가구간);
        }

        then_구간조회_시에_추가_확인된다: {
            assertThat(노선.getStations().stream()
                .mapToLong(Station::getId)
            ).containsExactly(1L, 2L, 3L);
        }
    }

    @DisplayName("지하철 노선에 상행선쪽에 구간 등록을 성공")
    @Test
    void addSection_성공케이스_1() {
        when_상행역_앞쪽으로_구간_추가: {
            Section 추가구간 = 상행역_앞쪽으로_추가성공_할_구간을_만든다();
            노선.addSection(추가구간);
        }

        then_구간조회_시에_상행역_앞쪽으로_추가역이_확인된다: {
            assertThat(노선.getStations().stream()
                .mapToLong(Station::getId)
            ).containsExactly(3L, 1L, 2L);
        }
    }

    @DisplayName("지하철 노선에 있는 구간들 중 상행역 쪽 중간역에 구간 등록을 성공")
    @Test
    void addSection_성공케이스_2() {
        when_상행역_쪽의_중간으로_구간_추가: {
            Section 추가구간 = 상행역_쪽_중간으로_추가성공_할_구간을_만든다();

            노선.addSection(추가구간);
        }

        then_구간조회_시에_중간에_추가역이_확인된다: {
            assertThat(노선.getStations().stream()
                .mapToLong(Station::getId)
            ).containsExactly(1L, 3L, 2L);
        }
    }

    @DisplayName("지하철 노선에 있는 구간들 중 하행역 쪽 중간역에 구간 등록을 성공")
    @Test
    void addSection_성공케이스_3() {
        when_하행역_쪽의_중간으로_구간_추가: {
            Section 추가구간 = 하행역_쪽_중간으로_추가성공_할_구간을_만든다();

            노선.addSection(추가구간);
        }

        then_구간조회_시에_중간에_추가역이_확인된다: {
            assertThat(노선.getStations().stream()
                .mapToLong(Station::getId)
            ).containsExactly(1L, 3L, 2L);
        }
    }

    @DisplayName("지하철 노선에 하행선쪽에 구간 등록을 성공")
    @Test
    void addSection_성공케이스_4() {
        when_하행선_쪽으로_구간을_등록: {
            Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();

            노선.addSection(추가구간);
        }

        then_하행선_쪽에_추가역이_확인된다: {
            assertThat(노선.getStations().stream()
                .mapToLong(Station::getId)
            ).containsExactly(1L, 2L, 3L);
        }
    }

    @DisplayName("지하철 노선에 있는 구간 사이에 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 실패")
    @Test
    void addSection_실패케이스_1() {
        whenThen: {
            Section 추가구간 = 거리가_크거나_같아서_실패하도록_추가할_구간을_만든다();

            assertThatThrownBy(() -> {
                노선.addSection(추가구간);
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @DisplayName("지하철 노선에 구간을 등록 시 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록 실패")
    @Test
    void addSection_실패케이스_2() {
        whenThen: {
            Section 추가구간 = 연결되는_역이_없어서_실패하도록_추가할_구간을_만든다();

            assertThatThrownBy(() -> {
                노선.addSection(추가구간);
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @DisplayName("지하철 노선에 구간을 등록 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록 실패")
    @Test
    void addSection_실패케이스_3() {
        whenThen: {
            Section 추가구간 = 역들이_둘다_등록되어있어서_실패하도록_추가할_구간을_만든다();

            assertThatThrownBy(() -> {
                노선.addSection(추가구간);
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @DisplayName("노선에 속해있는 역 목록 조회하여 예상한 역이 맞다.")
    @Test
    void getStations() {
        given: {
            Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();

            노선.addSection(추가구간);
        }

        whenThen: {
            assertThat(노선.getStations().stream()
                .mapToLong(Station::getId)
            ).containsExactly(1L, 2L, 3L);
        }
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {

    }

    private void 노선에_구간이_한개_들어가있다() {
        Station 역1 = Station.of(1L, "역1");
        Station 역2 = Station.of(2L, "역2");
        Section 구간1 = Section.of(노선, 역1, 역2, DISTANCE_BASIC);
        노선 = Line.of(노선Id, "일호선", "색상", 구간1);
    }

    private Section 상행역_앞쪽으로_추가성공_할_구간을_만든다() {
        Station 추가역 = Station.of(추가역Id, "추가역");
        Station 역1 = Station.of(역1Id, "역1");
        return Section.of(추가역, 역1, DISTANCE_VALID);
    }

    private Section 상행역_쪽_중간으로_추가성공_할_구간을_만든다() {
        Station 역1 = Station.of(역1Id, "역1");
        Station 추가역 = Station.of(추가역Id, "추가역");
        return Section.of(역1, 추가역, DISTANCE_VALID);
    }

    private Section 하행역_쪽_중간으로_추가성공_할_구간을_만든다() {
        Station 추가역 = Station.of(추가역Id, "추가역");
        Station 역2 = Station.of(역2Id, "역2");
        return Section.of(추가역, 역2, DISTANCE_VALID);
    }

    private Section 하행역_뒤쪽으로_추가성공_할_구간을_만든다() {
        Station 역2 = Station.of(역2Id, "역2");
        Station 추가역 = Station.of(추가역Id, "추가역");
        return Section.of(역2, 추가역, DISTANCE_VALID);
    }

    private Section 거리가_크거나_같아서_실패하도록_추가할_구간을_만든다() {
        Station 역1 = Station.of(역1Id, "역1");
        Station 추가역 = Station.of(추가역Id, "추가역");
        return Section.of(역1, 추가역, DISTANCE_INVALID);
    }

    private Section 연결되는_역이_없어서_실패하도록_추가할_구간을_만든다() {
        Station 역17 = Station.of(17L, "역17");
        Station 역23 = Station.of(23L, "역23");
        return Section.of(역17, 역23, DISTANCE_VALID);
    }

    private Section 역들이_둘다_등록되어있어서_실패하도록_추가할_구간을_만든다() {
        Station 역1 = Station.of(1L, "역1");
        Station 역2 = Station.of(2L, "역2");
        return Section.of(역1, 역2, DISTANCE_VALID);
    }

}
