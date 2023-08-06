package subway.application.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;
import subway.domain.*;

import java.util.List;

/**
 * 지하철 구간 비활성화에 대한 서비스를 테스트합니다.
 */
@UnitTest
@DisplayName("지하철 구간 비활성화에 대한 서비스 단위 테스트")
class SubwaySectionCloseServiceTest {

    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 노선에 구간이 추가되었다면
     * @when 구간을 비활성화할 때
     * @then 노선에 구간이 삭제된다.
     */
    @Test
    @DisplayName("")
    void closeSection() {
        //given

        //given

        //when

        //then
    }

    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 노선에 구간이 추가되었다면
     * @when 구간을 비활성화할 때
     * @then 구간을 삭제하기 위해 노선 업데이트 요청을 한다.
     */
    @Test
    @DisplayName("")
    void requestToDeleteSectionData() {
        //given

        //given

        //when

        //then
    }

    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 삭제될 지하철 역이 존재하지 않다면
     * @when 지하철 구간을 비활성화할 때
     * @then 에러 메세지를 출력한다.
     */
    @Test
    @DisplayName("존재하지 않은 지하철 역을 노선에서 삭제할 수 없다.")
    void tryToNotExistStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));

        //given

        //when

        //then
    }

    /**
     * @given 기존 지하철 노선이 존재하고
     * @given 삭제될 지하철 역이 존재하지 않다면
     * @when 지하철 구간을 비활성화할 때
     * @then 에러 메세지를 출력한다.
     */
    @Test
    @DisplayName("존재하지 않은 지하철 역을 노선에서 삭제할 수 없다.")
    void cantToTryToNotExistStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));

        //given

        //when

        //then
    }
}