package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import subway.application.in.SubwaySectionAddUsecase;
import subway.application.out.StationMapLoadByInPort;
import subway.application.out.SubwayLineLoadPort;
import subway.application.out.SubwaySectionAddPort;
import subway.domain.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

class SubwaySectionAddServiceTest {

    private final StationMapLoadByInPort stationMapLoadByInPort = Mockito.mock(StationMapLoadByInPort.class);
    private final SubwayLineLoadPort subwayLineLoadPort = Mockito.mock(SubwayLineLoadPort.class);
    private final SectionUpdateManager sectionUpdateManager = new SectionUpdateManager(new SectionTailAdder());
    private final SubwaySectionAddPort subwaySectionAddPort = Mockito.mock(SubwaySectionAddPort.class);
    private final SubwaySectionAddService subwaySectionAddService = new SubwaySectionAddService(stationMapLoadByInPort, subwayLineLoadPort, sectionUpdateManager, subwaySectionAddPort);


    /**
     * @given 지하철 역이 존재하고
     * @given 지하철 노선이 존재한다면
     * @when 지하철 구간을 종점역에 추가할 때
     * @then 지하철 구간이 추가된다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 지하철 구간을 종점역에 추가할 때 지하철 구간이 추가된다.")
    void addSection() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        given(stationMapLoadByInPort.findAllByIn(anyList()))
                .willReturn(Map.of(
                        역삼역.getId(), 역삼역,
                        선릉역.getId(), 선릉역));
        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));

        given(subwayLineLoadPort.findOne(any()))
                .willReturn(이호선);

        //when
        SubwaySectionAddUsecase.Command.SubwaySection subwaySection = SubwaySectionAddUsecase.Command.SubwaySection
                .builder()
                .upStationId(역삼역.getId())
                .downStationId(선릉역.getId())
                .distance(Kilometer.of(10))
                .build();

        SubwaySectionAddUsecase.Command command = SubwaySectionAddUsecase.Command
                .builder()
                .subwayLineId(이호선.getId())
                .subwaySection(subwaySection)
                .build();

        subwaySectionAddService.addSubwaySection(command);

        //then
        assertThat(이호선.getSections())
                .hasSize(2)
                .extracting("upStation.id", "downStation.id")
                .containsExactly(
                        tuple(강남역.getId(), 역삼역.getId()),
                        tuple(역삼역.getId(), 선릉역.getId()));

    }
}