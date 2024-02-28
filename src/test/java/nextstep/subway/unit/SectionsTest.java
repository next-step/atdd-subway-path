package nextstep.subway.unit;

import static nextstep.subway.support.fixture.LineFixture.이호선_색;
import static nextstep.subway.support.fixture.LineFixture.이호선_이름;
import static nextstep.subway.support.fixture.StationFixture.강남역_이름;
import static nextstep.subway.support.fixture.StationFixture.교대역_이름;
import static nextstep.subway.support.fixture.StationFixture.낙성대역_이름;
import static nextstep.subway.support.fixture.StationFixture.봉천역_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domians.domain.Line;
import nextstep.subway.domians.domain.Section;
import nextstep.subway.domians.domain.Sections;
import nextstep.subway.domians.domain.Station;
import nextstep.subway.support.fixture.LineFixture;
import nextstep.subway.support.fixture.SectionFixture;
import nextstep.subway.support.fixture.SectionsFixture;
import nextstep.subway.support.fixture.StationFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("구간 단위테스트")
class SectionsTest {


    Station 강남역;
    Station 교대역;
    Station 낙성대역;
    Station 봉천역;
    Section 강남역_낙성대역_구간;
    Section 낙성대역_봉천역_구간;
    Section 강남역_교대역_구간;
    Section 강남역_낙성대역_구간의_길이와_같은_강남역_교대역_구간;
    Line 이호선;


    @BeforeEach
    void setUp() {
        강남역 = StationFixture.giveOne(1L, 강남역_이름);
        교대역 = StationFixture.giveOne(2L, 교대역_이름);
        낙성대역 = StationFixture.giveOne(3L, 낙성대역_이름);
        봉천역 = StationFixture.giveOne(4L, 봉천역_이름);

        이호선 = LineFixture.giveOne(1L, 이호선_이름, 이호선_색);

        강남역_낙성대역_구간 = SectionFixture.giveOne(1L, 이호선, 강남역, 낙성대역, 10L);
        낙성대역_봉천역_구간 = SectionFixture.giveOne(2L, 이호선, 낙성대역, 봉천역, 10L);
        강남역_교대역_구간 = SectionFixture.giveOne(3L, 이호선, 강남역, 교대역, 5L);
        강남역_낙성대역_구간의_길이와_같은_강남역_교대역_구간 = SectionFixture.giveOne(4L, 이호선, 강남역, 교대역, 강남역_낙성대역_구간.getDistance());
    }


    @DisplayName("구간 추가 단위 테스트 - 마지막 위치에 추가")
    @Test
    void addSection() {
        // given
        Sections 강남_낙성대_구간들 = 구간들(강남역_낙성대역_구간);

        // when
        강남_낙성대_구간들.addSection(낙성대역_봉천역_구간);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(강남_낙성대_구간들.getSections()).hasSize(2);
            assertThat(강남_낙성대_구간들.getSortedStationsByUpDirection(false)).containsExactly(강남역, 낙성대역, 봉천역);
        });
    }

    @DisplayName("구간 추가 단위 테스트 - 처음 혹은 가운데 위치에 추가")
    @Test
    void addSectionWithMiddleIndex() {
        // given
        Sections 강남_낙성대_봉천_구간들 = 구간들(강남역_낙성대역_구간, 낙성대역_봉천역_구간);

        // when
        강남_낙성대_봉천_구간들.addSection(강남역_교대역_구간);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(강남_낙성대_봉천_구간들.getSections()).hasSize(3);
            assertThat(
                강남_낙성대_봉천_구간들.getSections()
                    .stream()
                    .map(Section::getDistance)
                    .collect(Collectors.toList())
            ).containsExactly(5L, 5L, 10L);
            assertThat(강남_낙성대_봉천_구간들.getSortedStationsByUpDirection(false)).containsExactly(강남역, 교대역, 낙성대역, 봉천역);
        });
    }

    @DisplayName("구간 추가 실패 단위 테스트 - 처음 혹은 가운데 위치에 추가하는 경우 유효하지 않은 구간의 길이인 경우")
    @Test
    void addSectionWithInvalidDistance() {
        // given
        Sections 강남_낙성대_봉천_구간들 = 구간들(강남역_낙성대역_구간, 낙성대역_봉천역_구간);

        // when

        Throwable catchThrowable = catchThrowable(() -> {
            강남_낙성대_봉천_구간들.addSection(강남역_낙성대역_구간의_길이와_같은_강남역_교대역_구간);
        });

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(catchThrowable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be smaller than exists");
        });
    }


    @DisplayName("구간 삭제 단위 테스트 - 상행 종점역 삭제")
    @Test
    void removeUpStation() {
        // given
        Sections 강남_낙성대_봉천_구간들 = 구간들(강남역_낙성대역_구간, 낙성대역_봉천역_구간);

        // when
        강남_낙성대_봉천_구간들.removeStation(강남역);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(강남_낙성대_봉천_구간들.getSections()).hasSize(1);
            assertThat(강남_낙성대_봉천_구간들.getSortedStationsByUpDirection(false)).containsExactly(낙성대역, 봉천역);
        });
    }

    @DisplayName("구간 삭제 단위 테스트 - 중간역 삭제")
    @Test
    void removeMiddleStation() {
        // given
        long 강남역_낙성대역_구간_길이 = 강남역_낙성대역_구간.getDistance();
        long 낙성대역_봉천역_구간_길이 = 낙성대역_봉천역_구간.getDistance();
        Sections 강남_낙성대_봉천_구간들 = 구간들(강남역_낙성대역_구간, 낙성대역_봉천역_구간);

        // when
        강남_낙성대_봉천_구간들.removeStation(낙성대역);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(강남_낙성대_봉천_구간들.getSections()).hasSize(1);

            assertThat(
                강남_낙성대_봉천_구간들.getSections()
                    .stream()
                    .map(Section::getDistance)
                    .collect(Collectors.toList())
            ).containsExactly(강남역_낙성대역_구간_길이 + 낙성대역_봉천역_구간_길이);
            assertThat(강남_낙성대_봉천_구간들.getSortedStationsByUpDirection(false)).containsExactly(강남역, 봉천역);
        });
    }


    private Sections 구간들(Section... 구간들) {
        ArrayList<Section> sectionList = new ArrayList<>(List.of(구간들));
        return SectionsFixture.giveOne(sectionList);
    }


}
