package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 지하철 노선 구간 추가에 대한 도메인 단위 테스트
 */
@UnitTest
@DisplayName("지하철 노선 구간 종점 추가에 대한 도메인 단위 테스트")
public class SubwaySectionAddDomainTest {

    /**
     * @given 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @given 추가될 지하철 구간이 종점역에서 시작한다면
     * @when 지하철 노선에 구간을 추가할 때
     * @then 지하철 노선에 구간이 추가된다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 지하철 구간을 종점역에 추가할 때 지하철 구간이 추가된다.")
    void addSectionAtEnd() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));
        SectionUpdateManager manager = new SectionUpdateManager(new SectionTailAdder());

        //given
        SubwaySection section = SubwaySection.register(역삼역, 선릉역, Kilometer.of(10));

        //when
        이호선.addSection(section, manager);

        //then
        assertThat(이호선.getSections())
                .hasSize(2)
                .extracting("upStation.id", "downStation.id")
                .containsExactly(
                        tuple(강남역.getId(), 역삼역.getId()),
                        tuple(역삼역.getId(), 선릉역.getId()));
    }

    /**
     * @given 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @given 추가될 지하철 구간이 중간역에서 시작한다면
     * @when 지하철 노선에 구간을 추가할 때
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 지하철 구간을 중간에 추가할 수 없다.")
    void addSectionAtMiddle() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionUpdateManager manager = new SectionUpdateManager(new SectionTailAdder());

        //given
        SubwaySection section = SubwaySection.register(역삼역, 성수역, Kilometer.of(10));

        //when
        Throwable throwable = catchThrowable(() -> 이호선.addSection(section, manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역이 이미 노선에 등록되어 있습니다.");
    }
}
