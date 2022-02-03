package nextstep.subway.unit;

import static nextstep.subway.common.LineSomething.DISTANCE_BASIC;
import static nextstep.subway.common.LineSomething.DISTANCE_INVALID;
import static nextstep.subway.common.LineSomething.DISTANCE_VALID;
import static nextstep.subway.unit.SectionTest.상행역_하행역_의_구간을_만든다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
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

    private final Long 노선Id = 1L;
    private final Long 역1Id = 1L;
    private final String 역1이름 = "역1이름";
    private final Long 역2Id = 2L;
    private final String 역2이름 = "역2이름";
    private final Long 추가역Id = 3L;

    private final String 노선이름 = "노선이름";
    private final String 노선색상 = "노선색상";
    private Station 역1;
    private Station 역2;
    private Section 구간1;
    private Station 추가역;
    private Station 없는역 = Station.of("없는역");

    @BeforeEach
    void setUp() {
        노선에_구간이_한개_들어가있다();
    }

    @DisplayName("노선에서 (상행역) 역ID 를 사용해서 구간을 삭제 성공한다.")
    @Test
    void lineDeleteSectionByStation_테스트_1() {
        // given
        Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();
        노선.addSection(추가구간);

        // when
        int distanceBefore = 노선.getSections().getTotalDistance();
        노선.deleteSectionByStation(역1);

        // then
        assertThat(노선.getStations().stream()
            .map(Station::getName)
            .collect(Collectors.toList()))
            .containsExactly("역2이름", "추가역");
        assertThat(노선.getSections().getTotalDistance())
            .isEqualTo(distanceBefore - DISTANCE_BASIC);
    }

    @DisplayName("노선에서 (중간역) 역ID 를 사용해서 구간을 삭제 성공한다.")
    @Test
    void lineDeleteSectionByStation_테스트_2() {
    // given
        Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();
        노선.addSection(추가구간);

        // when
        int distanceBefore = 노선.getSections().getTotalDistance();
        노선.deleteSectionByStation(역2);

        // then
        assertThat(노선.getStations().stream()
            .map(Station::getName)
            .collect(Collectors.toList()))
            .containsExactly("역1이름", "추가역");
        assertThat(노선.getSections().getTotalDistance())
            .isEqualTo(distanceBefore);
    }

    @DisplayName("노선에서 (하행역) 역ID 를 사용해서 구간을 삭제 성공한다.")
    @Test
    void lineDeleteSectionByStation_테스트_3() {
        // given
        Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();
        노선.addSection(추가구간);

        // when
        int distanceBefore = 노선.getSections().getTotalDistance();
        노선.deleteSectionByStation(추가역);

        // then
        assertThat(노선.getStations().stream()
            .map(Station::getName)
            .collect(Collectors.toList()))
            .containsExactly("역1이름", "역2이름");
        assertThat(노선.getSections().getTotalDistance())
            .isEqualTo(distanceBefore - 추가구간.getDistance());
    }

    @DisplayName("노선에서 역ID 로 구간을 삭제 시, 구간이 1개 이하면 실패한다.")
    @Test
    void lineDeleteSectionByStation_테스트_4() {
        // when // then
        assertThatThrownBy(() -> {
            노선.deleteSectionByStation(역1);
        }).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("노선에서 역ID 로 구간을 삭제 시, 해당 역이 없으면 실패한다.")
    @Test
    void lineDeleteSectionByStation_테스트_5() {
        // given 역이 3개 있다.
        Section 추가구간 = 하행역_뒤쪽으로_추가성공_할_구간을_만든다();
        노선.addSection(추가구간);

        // when // then
        assertThatThrownBy(() -> {
            노선.deleteSectionByStation(없는역);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선을 이름과 색상으로 생성한다.")
    @Test
    void 노선_생성_테스트_1() {
        // when
        노선 = Line.of(노선이름, 노선색상);

        // then
        assertThat(노선.getName()).isEqualTo(노선이름);
        assertThat(노선.getColor()).isEqualTo(노선색상);
    }

    @DisplayName("노선을 노선이름, 노선색상, 구간으로 생성한다.")
    @Test
    void 노선_생성_테스트_2() {
        // given
        Section 구간 = 상행역_하행역_의_구간을_만든다();

        // when
        노선 = Line.of(노선이름, 노선색상, 구간);

        // then
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

            assertThat(노선.getTotalDistance()).isEqualTo(DISTANCE_BASIC + DISTANCE_VALID);
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

            assertThat(노선.getTotalDistance()).isEqualTo(DISTANCE_BASIC);
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

            assertThat(노선.getTotalDistance()).isEqualTo(DISTANCE_BASIC);
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

            assertThat(노선.getTotalDistance()).isEqualTo(DISTANCE_BASIC + DISTANCE_VALID);
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
    void getStations_1() {
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

    @DisplayName("노선에 속해있는 역 목록 조회 시 구간이 없으면 빈 역리스트가 온다.")
    @Test
    void getStations_2() {
        given: {
            노선 = Line.of("빈노선이름", "빈노선색상");
        }

        whenThen: {
            assertThat(노선.getStations().size()).isEqualTo(0);
        }
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {

    }

    private void 노선에_구간이_한개_들어가있다() {
        역1 = Station.of(역1Id, 역1이름);
        역2 = Station.of(역2Id, 역2이름);
        구간1 = Section.of(노선, 역1, 역2, DISTANCE_BASIC);
        노선 = Line.of(노선Id, "일호선", "노선색상", 구간1);
    }

    private Section 상행역_앞쪽으로_추가성공_할_구간을_만든다() {
        추가역 = Station.of(추가역Id, "추가역");
        역1 = Station.of(역1Id, 역1이름);
        return Section.of(추가역, 역1, DISTANCE_VALID);
    }

    private Section 상행역_쪽_중간으로_추가성공_할_구간을_만든다() {
        역1 = Station.of(역1Id, 역1이름);
        추가역 = Station.of(추가역Id, "추가역");
        return Section.of(역1, 추가역, DISTANCE_VALID);
    }

    private Section 하행역_쪽_중간으로_추가성공_할_구간을_만든다() {
        추가역 = Station.of(추가역Id, "추가역");
        역2 = Station.of(역2Id, 역2이름);
        return Section.of(추가역, 역2, DISTANCE_VALID);
    }

    private Section 하행역_뒤쪽으로_추가성공_할_구간을_만든다() {
        역2 = Station.of(역2Id, 역2이름);
        추가역 = Station.of(추가역Id, "추가역");
        return Section.of(역2, 추가역, DISTANCE_VALID);
    }

    private Section 거리가_크거나_같아서_실패하도록_추가할_구간을_만든다() {
        역1 = Station.of(역1Id, 역1이름);
        추가역 = Station.of(추가역Id, "추가역");
        return Section.of(역1, 추가역, DISTANCE_INVALID);
    }

    private Section 연결되는_역이_없어서_실패하도록_추가할_구간을_만든다() {
        Station 역17 = Station.of(17L, "역17");
        Station 역23 = Station.of(23L, "역23");
        return Section.of(역17, 역23, DISTANCE_VALID);
    }

    private Section 역들이_둘다_등록되어있어서_실패하도록_추가할_구간을_만든다() {
        역1 = Station.of(역1Id, 역1이름);
        역2 = Station.of(역2Id, 역2이름);
        return Section.of(역1, 역2, DISTANCE_VALID);
    }

}
