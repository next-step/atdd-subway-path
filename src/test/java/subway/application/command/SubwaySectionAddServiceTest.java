package subway.application.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import subway.application.command.in.SubwaySectionAddUsecase;
import subway.application.command.out.StationMapLoadByInPort;
import subway.application.command.out.SubwayLineLoadPort;
import subway.application.command.out.SubwaySectionAddPort;
import subway.application.command.validator.SubwaySectionAddCommandValidator;
import subway.common.annotation.UnitTest;
import subway.domain.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

/**
 * 지하철 구간 추가에 대한 서비스를 테스트합니다.
 */
@UnitTest
@DisplayName("지하철 구간 추가에 대한 서비스 단위 테스트")
class SubwaySectionAddServiceTest {

    private final StationMapLoadByInPort stationMapLoadByInPort = Mockito.mock(StationMapLoadByInPort.class);
    private final SubwayLineLoadPort subwayLineLoadPort = Mockito.mock(SubwayLineLoadPort.class);
    private final SectionUpdateManager sectionUpdateManager = new SectionUpdateManager(new SectionTailAdder());
    private final SubwaySectionAddPort subwaySectionAddPort = Mockito.mock(SubwaySectionAddPort.class);
    private final SubwaySectionAddService subwaySectionAddService = new SubwaySectionAddService(stationMapLoadByInPort, subwayLineLoadPort, sectionUpdateManager, subwaySectionAddPort);


    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 추가될 지하철 역이 존재한다면
     * @when 지하철 구간을 추가할 때
     * @then 지하철 구간이 추가된다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 지하철 구간을 종점역에 추가할 때 지하철 구간이 추가된다.")
    void addSection() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));

        given(subwayLineLoadPort.findOne(any()))
                .willReturn(이호선);

        //given
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        given(stationMapLoadByInPort.findAllByIn(anyList()))
                .willReturn(Map.of(
                        역삼역.getId(), 역삼역,
                        선릉역.getId(), 선릉역));
        //when
        SubwaySectionAddUsecase.Command.SectionCommand subwaySection = SubwaySectionAddUsecase.Command.SectionCommand
                .builder()
                .upStationId(역삼역.getId())
                .downStationId(선릉역.getId())
                .distance(Kilometer.of(10))
                .build();

        SubwaySectionAddUsecase.Command command = SubwaySectionAddUsecase.Command
                .builder()
                .subwayLineId(이호선.getId())
                .subwaySection(subwaySection)
                .validator(new SubwaySectionAddCommandValidator())
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

    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 추가될 지하철 역이 존재하지 않다면
     * @when 지하철 구간을 추가할 때
     * @then 에러 메세지를 출력한다.
     */
    @Test
    @DisplayName("존재하지 않은 지하철 역을 노선에 등록할 수 없다.")
    void tryToNotExistStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));

        given(subwayLineLoadPort.findOne(any()))
                .willReturn(이호선);

        //given
        given(stationMapLoadByInPort.findAllByIn(anyList()))
                .willReturn(Map.of(
                        역삼역.getId(), 역삼역));

        //when
        SubwaySectionAddUsecase.Command.SectionCommand subwaySection = SubwaySectionAddUsecase.Command.SectionCommand
                .builder()
                .upStationId(역삼역.getId())
                .downStationId(new Station.Id(3L))
                .distance(Kilometer.of(10))
                .build();

        SubwaySectionAddUsecase.Command command = SubwaySectionAddUsecase.Command
                .builder()
                .subwayLineId(이호선.getId())
                .subwaySection(subwaySection)
                .validator(new SubwaySectionAddCommandValidator())
                .build();

        Throwable throwable = catchThrowable(() -> subwaySectionAddService.addSubwaySection(command));
        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("%d는 존재하지 않는 역 id 입니다.", 3L));
    }
}