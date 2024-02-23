package nextstep.subway.unit;

import static nextstep.subway.support.fixture.LineFixture.이호선_색;
import static nextstep.subway.support.fixture.LineFixture.이호선_이름;
import static nextstep.subway.support.fixture.StationFixture.강남역_이름;
import static nextstep.subway.support.fixture.StationFixture.교대역_이름;
import static nextstep.subway.support.fixture.StationFixture.낙성대역_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domians.domain.Line;
import nextstep.subway.domians.domain.Section;
import nextstep.subway.domians.domain.Station;
import nextstep.subway.support.fixture.LineFixture;
import nextstep.subway.support.fixture.SectionFixture;
import nextstep.subway.support.fixture.StationFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("노선 단위테스트")
class LineTest {


    Station 강남역;
    Station 교대역;
    Station 낙성대역;
    Section 강남역_교대역_구간;
    Section 교대역_낙성대역_구간;
    Line 이호선;


    @BeforeEach
    void setUp() {
        강남역 = StationFixture.giveOne(1L, 강남역_이름);
        교대역 = StationFixture.giveOne(2L, 교대역_이름);
        낙성대역 = StationFixture.giveOne(3L, 낙성대역_이름);

        이호선 = LineFixture.giveOne(1L, 이호선_이름, 이호선_색);

        강남역_교대역_구간 = SectionFixture.giveOne(1L, 이호선, 강남역, 교대역, 10L);
        교대역_낙성대역_구간 = SectionFixture.giveOne(2L, 이호선, 교대역, 낙성대역, 10L);
    }


    @DisplayName("노선 구간 추가 단위 테스트")
    @Test
    void addSection() {
        // given
        Line 이호선 = 강남역_교대역_구간_이호선();

        // when
        이호선.addSection(교대역_낙성대역_구간);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(이호선.getSections().getSections()).hasSize(2);
        });
    }


    @DisplayName("노선의 전체 역 목록 조회 단위 테스트")
    @Test
    void getStations() {
        // given
        Line 강남_교대_이호선 = 강남역_교대역_구간_이호선();

        // when
        List<Long> 이호선_모든_역_아이디_리스트 = 강남_교대_이호선.getAllStations().stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(이호선_모든_역_아이디_리스트).containsExactly(강남역.getId(), 교대역.getId());
        });
    }

    @DisplayName("노선의 구간 제거 단위 테스트")
    @Test
    void removeSection() {

        // given
        Line 이호선 = 강남역_교대역_구간_이호선();
        이호선.addSection(교대역_낙성대역_구간);

        // when
        이호선.removeSection(낙성대역.getId());
        List<Long> 이호선_모든_역_아이디_리스트 = 이호선.getAllStations().stream()
            .map(Station::getId)
            .collect(Collectors.toList());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(이호선_모든_역_아이디_리스트).doesNotContain(낙성대역.getId());
        });
    }

    @DisplayName("노선의 구간 제거 실패 단위 테스트 - 제거할 구간이 노선의 마지막 구간이 아닌 경우")
    @Test
    void removeSectionWithNotLastStation() {

        // given
        Line 이호선 = 강남역_교대역_구간_이호선();
        이호선.addSection(교대역_낙성대역_구간);

        // when
        Throwable catchThrowable = catchThrowable(() -> {
            이호선.removeSection(교대역.getId());
        });

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(catchThrowable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid section");
        });
    }

    private Line 강남역_교대역_구간_이호선() {
        이호선.addSection(강남역_교대역_구간);
        return 이호선;
    }

}
