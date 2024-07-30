package nextstep.subway.acceptance;

import nextstep.subway.acceptance.utils.*;
import nextstep.subway.dto.*;
import nextstep.subway.utils.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SectionAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("구간 등록 시 노선 처음에 등록한다")
    void addSectionToFirst() {
        //given
        var lineResponse = 구간_1개인_지하철_노선이_등록됨();

        //when
        var sectionDistance = 3L;
        var sectionResponse = 노선_처음에_구간_추가(lineResponse, sectionDistance);

        //then
        var expectedDistance = lineResponse.toResponse().getDistance() + sectionDistance;
        노선에_구간_추가_성공(sectionResponse, expectedDistance);
    }

    @Test
    @DisplayName("구간 등록 시 노선 중간에 등록한다.")
    void addSectionMiddle() {
        //given
        var lineInfo = 구간_2개인_지하철_노선_등록됨();

        //when
        var createdResponse = 노선_중간에_구간_추가(lineInfo, 3L);

        //then
        노선에_구간_추가_성공(createdResponse, lineInfo.getDistance());

    }

    @Test
    @DisplayName("기존 구간과 동일한 구간을 등록할 수 없다")
    void failAddSection() {
        //given
        var lineInfo = 구간_2개인_지하철_노선_등록됨();

        //when
        var createdResponse = 기존_구간과_동일한_신규_구간_등록(lineInfo, 3L);

        //then
        assertTrue(createdResponse.isBadRequest());

    }

    private ResponseHelper<SubwayLineResponse> 구간_1개인_지하철_노선이_등록됨() {
        var upStation = StationRequestHelper.requestCreate(new StationCreateRequest(RandomGenerator.generateString())).toResponse();
        var downStation = StationRequestHelper.requestCreate(new StationCreateRequest(RandomGenerator.generateString())).toResponse();
        var lineRequest = new SubwayLineRequest.SubwayLineRequestBuilder()
                .name(LINE_SINBUNDANG)
                .color(COLOR_RED)
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(10L)
                .build();
        return LineRequestHelper.requestCreate(lineRequest);
    }

    private ResponseHelper<SectionResponse> 노선_처음에_구간_추가(ResponseHelper<SubwayLineResponse> lineresponse, Long sectionDistance) {
        var newUpStation = StationRequestHelper.requestCreate(new StationCreateRequest(RandomGenerator.generateString())).toResponse();
        var sectionRequest = new SectionRequest.SectionRequestBuilder()
                .upStationId(newUpStation.getId())
                .downStationId(lineresponse.toResponse().getUpStationId())
                .distance(sectionDistance)
                .build();

        return SectionRequestHelper.requestCreate(lineresponse.toResponse().getId(), sectionRequest);
    }

    private void 노선에_구간_추가_성공(ResponseHelper<SectionResponse> response, Long expectedDistance) {
        assertTrue(response.isCreated());

        var lineId = response.toResponse().getSubwayLineId();
        var distance = LineRequestHelper.requestGet(lineId).toResponse().getDistance();
        assertThat(distance).isEqualTo(expectedDistance);
    }

    private SubwayLineInfo 구간_2개인_지하철_노선_등록됨() {
        var initialLineResponse = 구간_1개인_지하철_노선이_등록됨();
        노선_처음에_구간_추가(initialLineResponse, 2L);
        var updatedLineResponse = LineRequestHelper.requestGet(initialLineResponse.toResponse().getId()).toResponse();
        return new SubwayLineInfo.SubwayLineInfoBuilder()
                .subwayLineId(initialLineResponse.toResponse().getId())
                .middleStationId(initialLineResponse.toResponse().getUpStationId())
                .upStationId(updatedLineResponse.getUpStationId())
                .downStationId(updatedLineResponse.getDownStationId())
                .distance(updatedLineResponse.getDistance())
                .build();
    }

    private ResponseHelper<SectionResponse> 노선_중간에_구간_추가(SubwayLineInfo lineInfo, Long sectionDistance) {
        var stationToAdd = StationRequestHelper.requestCreate(new StationCreateRequest(RandomGenerator.generateString())).toResponse();
        var sectionRequest = new SectionRequest.SectionRequestBuilder()
                .upStationId(lineInfo.getMiddleStationId())
                .downStationId(stationToAdd.getId())
                .distance(sectionDistance)
                .build();
        return SectionRequestHelper.requestCreate(lineInfo.getSubwayLineId(), sectionRequest);
    }

    private ResponseHelper<SectionResponse> 기존_구간과_동일한_신규_구간_등록(SubwayLineInfo lineInfo, Long distance) {
        var sectionRequest = new SectionRequest.SectionRequestBuilder()
                .distance(distance)
                .upStationId(lineInfo.getUpStationId())
                .downStationId(lineInfo.getMiddleStationId())
                .build();
        return SectionRequestHelper.requestCreate(lineInfo.getSubwayLineId(), sectionRequest);
    }
}
