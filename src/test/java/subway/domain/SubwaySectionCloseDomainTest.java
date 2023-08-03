package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 지하철 노선 구간 제거에 대한 도메인 단위 테스트
 */
@UnitTest
@DisplayName("지하철 노선 구간 종점 제거에 대한 도메인 단위 테스트")
public class SubwaySectionCloseDomainTest {

    /**
     * @given 지하철 역이 존재하고
     * @given 구간 2개 이상의 지하철 노선이 존재한다면
     * @when 지하철 노선에 마지막 구간을 비활성화할 때
     * @then 지하철 구간이 비활성화된다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 종점역을 비활성화할 때 비활성화된다.")
    void closeSectionAtEnd() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionCloseManager manager = new SectionCloseManager(new SectionTailCloser());

        //when
        이호선.closeSection(선릉역, manager);

        //then
        assertThat(이호선.getSections())
                .hasSize(1)
                .extracting("upStation.id", "downStation.id")
                .containsExactly(
                        tuple(강남역.getId(), 역삼역.getId()));
    }


    /**
     * @given 지하철 역이 존재하고
     * @given 구간이 1개인 지하철 노선이 존재한다면
     * @when 지하철 노선에 마지막 구간을 비활성화할 때
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("구간이 1개인 노선은 마지막 구간을 비활성화할 수 없다.")
    void closeSectionWhenOnlyOne() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));
        SectionCloseManager manager = new SectionCloseManager(new SectionTailCloser());

        //when
        Throwable throwable = catchThrowable(() -> 이호선.closeSection(역삼역, manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 비어있습니다.");
    }


    /**
     * @given 지하철 역이 존재하고
     * @given 구간 2개 이상의 지하철 노선이 존재한다면
     * @when 지하철 노선에 중간 구간을 비활성화할 때
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("지하철 노선의 중간 구간을 비활성화할 수 없다.")
    void closeSectionAtMiddle() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionCloseManager manager = new SectionCloseManager(new SectionTailCloser());

        //when
        Throwable throwable = catchThrowable(() -> 이호선.closeSection(역삼역, manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 연결되어있지 않습니다.");
    }
}
