package subway.application.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import subway.application.command.in.SubwaySectionCloseUsecase;
import subway.application.command.out.StationLoadPort;
import subway.application.command.out.SubwayLineLoadPort;
import subway.application.command.out.SubwaySectionClosePort;
import subway.application.command.validator.SubwaySectionCloseCommandValidator;
import subway.common.annotation.UnitTest;
import subway.domain.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * 지하철 구간 비활성화에 대한 서비스를 테스트합니다.
 */
@UnitTest
@DisplayName("지하철 구간 비활성화에 대한 서비스 단위 테스트")
class SubwaySectionCloseServiceTest {

    private final SectionCloseManager sectionCloseManager = new SectionCloseManager();
    private final StationLoadPort stationLoadPort = Mockito.mock(StationLoadPort.class);

    private final SubwayLineLoadPort subwayLineLoadPort = Mockito.mock(SubwayLineLoadPort.class);

    private final SubwaySectionClosePort subwaySectionClosePort = Mockito.mock(SubwaySectionClosePort.class);

    private final SubwaySectionCloseService subwaySectionCloseService = new SubwaySectionCloseService(sectionCloseManager, subwayLineLoadPort, stationLoadPort, subwaySectionClosePort);

    /**
     * @given 구간을 3개 가진 기존 지하철 노선이 존재하고
     * @given 비활성화할 구간이 존재한다면
     * @when 구간을 비활성화할 때
     * @then 노선에 구간이 삭제된다.
     */
    @Test
    @DisplayName("구간을 비활성화할 때 노선에 구간이 비활성화된다.")
    void closeSection() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection sectionSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, sectionSection));

        given(subwayLineLoadPort.findOne(any()))
                .willReturn(Optional.of(이호선));

        //given
        given(stationLoadPort.findOne(any()))
                .willReturn(Optional.of(선릉역));

        //when
        SubwaySectionCloseUsecase.Command.SectionCommand sectionCommand = SubwaySectionCloseUsecase.Command.SectionCommand
                .builder()
                .stationId(선릉역.getId())
                .build();

        SubwaySectionCloseUsecase.Command command = SubwaySectionCloseUsecase.Command
                .builder()
                .section(sectionCommand)
                .subwayLineId(이호선.getId())
                .validator(new SubwaySectionCloseCommandValidator())
                .build();

        subwaySectionCloseService.closeSection(command);

        //then
        assertThat(이호선.getSections())
                .doesNotContain(sectionSection);
    }

    /**
     * @given 구간을 3개 가진 기존 지하철 노선이 존재하고
     * @given 비활성화할 구간이 존재한다면
     * @when 구간을 비활성화할 때
     * @then 구간 데이터 비활성화 요청을 한다.
     */
    @Test
    @DisplayName("구간을 비활성화할 때 구간 데이터 비활성화 요청을 한다.")
    void requestToCloseSectionData() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection sectionSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, sectionSection));

        given(subwayLineLoadPort.findOne(any()))
                .willReturn(Optional.of(이호선));

        //given
        given(stationLoadPort.findOne(any()))
                .willReturn(Optional.of(선릉역));

        //when
        SubwaySectionCloseUsecase.Command.SectionCommand sectionCommand = SubwaySectionCloseUsecase.Command.SectionCommand
                .builder()
                .stationId(선릉역.getId())
                .build();

        SubwaySectionCloseUsecase.Command command = SubwaySectionCloseUsecase.Command
                .builder()
                .section(sectionCommand)
                .subwayLineId(이호선.getId())
                .validator(new SubwaySectionCloseCommandValidator())
                .build();

        subwaySectionCloseService.closeSection(command);

        //then
        then(subwaySectionClosePort)
                .should(times(1))
                .closeSection(any());
    }

    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 비활성화할 지하철 역이 존재하지 않다면
     * @when 지하철 구간을 비활성화할 때
     * @then 에러 메세지를 출력한다.
     */
    @Test
    @DisplayName("존재하지 않은 지하철 역을 노선에서 비활성화할 수 없다.")
    void tryToNotExistStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection sectionSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, sectionSection));

        given(subwayLineLoadPort.findOne(any()))
                .willReturn(Optional.of(이호선));

        //given
        given(stationLoadPort.findOne(any()))
                .willReturn(Optional.empty());

        //when
        SubwaySectionCloseUsecase.Command.SectionCommand sectionCommand = SubwaySectionCloseUsecase.Command.SectionCommand
                .builder()
                .stationId(선릉역.getId())
                .build();

        SubwaySectionCloseUsecase.Command command = SubwaySectionCloseUsecase.Command
                .builder()
                .section(sectionCommand)
                .subwayLineId(이호선.getId())
                .validator(new SubwaySectionCloseCommandValidator())
                .build();

        Throwable throwable = catchThrowable(() -> subwaySectionCloseService.closeSection(command));

        //then
        assertThat(throwable)
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(String.format("%d는 존재하지 않는 역 id 입니다.", command.getStationId().getValue()));
    }
}