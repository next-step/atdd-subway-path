package nextstep.subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("새로운 지하철 구간 관리 기능")
public class LineSectionAcceptanceTestV2 extends AcceptanceTest{

    @DisplayName("지하철 노선에 구간을 등록 시에 역 사이에 새로운 역을 등록")
    @Test
    void addLineSectionWhenRegisterBetweenSections(){}

    @DisplayName("지하철 노선에 구간을 등록 시에 새로운 역을 상행 종점으로 등록")
    @Test
    void addLineSectionWhenRegisterUpStation(){}

    @DisplayName("지하철 노선에 구간을 등록 시에 새로운 역을 히행 종점으로 등록")
    @Test
    void addLineSectionWhenRegisterDownStation(){}

    @DisplayName("지하철 노선에 구간을 등록 시에 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    @Test
    void cannotAddLineSectionIfSectionDistanceIsLargerThanLineDistance(){}

    @DisplayName("지하철 노선에 구간을 등록 시에 상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없다")
    @Test
    void cannotAddLineSectionIfUpStationAndDownStationAlreadyExisted(){}

    @DisplayName("지하철 노선에 구간을 등록 시에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void cannotAddLineSectionIfSectionUpStationAndDownStationNotExistedInLine(){}

    @DisplayName("지하철 노선 조회")
    @Test
    void getLines(){}

}
