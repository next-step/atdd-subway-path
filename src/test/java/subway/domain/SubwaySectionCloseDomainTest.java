package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 지하철 노선 구간 비활성화에 대한 도메인 단위 테스트
 */
@UnitTest
@DisplayName("지하철 노선 구간 종점 비활성화에 대한 도메인 단위 테스트")
public class SubwaySectionCloseDomainTest {

    /**
     * @given 지하철 역이 존재하고
     * @given 구간 2개 이상의 지하철 노선이 존재한다면
     * @when 구간을 비활성화할 때
     * @then 지하철 구간이 비활성화된다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 종점역을 비활성화할 때 비활성화된다.")
    void closeSection() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionCloseManager manager = new SectionCloseManager();

        //when
        이호선.closeSection(선릉역, manager);

        //then
        assertThat(이호선.getSections())
                .hasSize(1)
                .extracting(SubwaySection::getUpStationId, SubwaySection::getDownStationId)
                .containsExactly(
                        tuple(강남역.getId(), 역삼역.getId()));
    }


    /**
     * @given 지하철 역이 존재하고
     * @given 구간이 1개인 지하철 노선이 존재한다면
     * @when 구간을 비활성화할 때
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
        SectionCloseManager manager = new SectionCloseManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.closeSection(역삼역, manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 비어있습니다.");
    }

    /**
     * @given 지하철 역이 존재하고
     * @given 기존 지하철 노선이 존재하고
     * @given 비활성화될 지하철 역이 노선에 등록되어 있지 않다면
     * @when 지하철 구간을 비활성화할 때
     * @then 에러 메세지를 출력한다.
     */
    @Test
    @DisplayName("노선에 등록되지 않은 지하철 역을 노선에서 비활성화할 수 없다.")
    void cantToTryToNotRegisterStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));
        SectionCloseManager manager = new SectionCloseManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.closeSection(선릉역, manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("선릉역 역은 현재 노선에 존재하지 않은 역입니다.");
    }

    /**
     * @given A, B, C 지하철 역이 존재하고
     * @given A-B-C 구간을 갖고 있는 노선이 존재한다면
     * @when 출발역인 A역을 비활성화할 때
     * @then 다음역인 B역으로 출발역이 변경된다.
     */
    @Test
    @DisplayName("출발역을 비활성화하면 다음역이 출발역으로 변경된다.")
    void changeStartSectionWhenStartSectionIsClosed() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionCloseManager manager = new SectionCloseManager();

        //when
        이호선.closeSection(강남역, manager);

        //then
        assertThat(이호선.getStartStationId()).isEqualTo(역삼역.getId());
    }
}
