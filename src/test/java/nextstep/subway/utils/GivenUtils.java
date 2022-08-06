package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineCreationRequest;
import nextstep.subway.applicaion.dto.LineModificationRequest;
import nextstep.subway.applicaion.dto.SectionRegistrationRequest;
import nextstep.subway.client.LineClient;
import nextstep.subway.client.StationClient;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GivenUtils {

    public static final Long FIRST_ID = 1L;
    public static final Long SECOND_ID = 2L;
    public static final Long THIRD_ID = 3L;
    public static final Long FOURTH_ID = 4L;
    public static final String GREEN = "green";
    public static final String RED = "red";
    public static final String YELLOW = "yellow";
    public static final int TEN = 10;
    public static final int FIVE = 5;
    public static final String 이호선_이름 = "2호선";
    public static final String 신분당선_이름 = "신분당선";
    public static final String 분당선_이름 = "분당선";
    public static final String 강남역_이름 = "강남역";
    public static final String 역삼역_이름 = "역삼역";
    public static final String 양재역_이름 = "양재역";
    public static final String 선릉역_이름 = "선릉역";
    public static final String 교대역_이름 = "교대역";
    public static final String 남부터미널역_이름 = "남부터미널역";
    public static final List<String> 이호선역_이름들 = List.of(강남역_이름, 역삼역_이름);
    public static final List<String> 신분당선역_이름들 = List.of(강남역_이름, 양재역_이름);

    private final StationClient stationClient;
    private final LineClient lineClient;
    private final JsonResponseConverter responseConverter;

    public static Station 강남역() {
        return new Station(FIRST_ID, 강남역_이름);
    }

    public static Station 역삼역() {
        return new Station(SECOND_ID, 역삼역_이름);
    }

    public static Station 양재역() {
        return new Station(THIRD_ID, 양재역_이름);
    }

    public static Station 선릉역() {
        return new Station(FOURTH_ID, 선릉역_이름);
    }

    public static Line 이호선() {
        Line line = new Line(FIRST_ID, 이호선_이름, GREEN);
        line.addSection(강남역(), 역삼역(), TEN);
        return line;
    }

    public static Line 신분당선() {
        Line line = new Line(SECOND_ID, 신분당선_이름, RED);
        line.addSection(강남역(), 양재역(), FIVE);
        return line;
    }

    public static Line 분당선() {
        return new Line(THIRD_ID, 분당선_이름, YELLOW);
    }

    public static Section 강남_역삼_구간() {
        return new Section(이호선(), 강남역(), 역삼역(), TEN);
    }

    public static Section 역삼_선릉_구간() {
        return new Section(이호선(), 역삼역(), 선릉역(), FIVE);
    }

    public static LineCreationRequest 이호선_객체_생성_요청() {
        return new LineCreationRequest(
                이호선_이름,
                GREEN,
                강남역().getId(),
                역삼역().getId(),
                TEN
        );
    }

    public static SectionRegistrationRequest 강남_역삼_구간_요청() {
        return new SectionRegistrationRequest(강남역().getId(), 역삼역().getId(), TEN);
    }

    public static SectionRegistrationRequest 역삼_선릉_구간_요청() {
        return new SectionRegistrationRequest(역삼역().getId(), 선릉역().getId(), FIVE);
    }

    public static LineModificationRequest 분당선으로_수정_요청() {
        return new LineModificationRequest(분당선_이름, YELLOW);
    }

    public ExtractableResponse<Response> 강남역_생성() {
        return stationClient.createStation(강남역_이름);
    }

    public ExtractableResponse<Response> 교대역_생성() {
        return stationClient.createStation(교대역_이름);
    }

    public ExtractableResponse<Response> 양재역_생성() {
        return stationClient.createStation(양재역_이름);
    }

    public ExtractableResponse<Response> 남부터미널역_생성() {
        return stationClient.createStation(남부터미널역_이름);
    }

    public ExtractableResponse<Response> 지하철_생성(
            String name,
            String color,
            Long upStationId,
            Long downStationId,
            int distance) {
        return lineClient.createLine(new nextstep.subway.client.dto.LineCreationRequest(
                name,
                color,
                upStationId,
                downStationId,
                distance
        ));
    }

    public nextstep.subway.client.dto.LineCreationRequest 이호선_생성_요청() {
        List<Long> stationIds = responseConverter.convertToIds(stationClient.createStations(이호선역_이름들));
        return new nextstep.subway.client.dto.LineCreationRequest(
                이호선_이름,
                GREEN,
                stationIds.get(0),
                stationIds.get(1),
                TEN
        );
    }

    public nextstep.subway.client.dto.LineCreationRequest 신분당선_생성_요청() {
        List<Long> stationIds = responseConverter.convertToIds(stationClient.createStations(신분당선역_이름들));
        return new nextstep.subway.client.dto.LineCreationRequest(
                신분당선_이름,
                RED,
                stationIds.get(0),
                stationIds.get(1),
                FIVE
        );
    }

    public ExtractableResponse<Response> 이호선_생성() {
        return lineClient.createLine(이호선_생성_요청());
    }

    public nextstep.subway.client.dto.SectionRegistrationRequest 역삼_선릉_구간_생성_요청() {
        List<Long> stationIds = responseConverter.convertToIds(stationClient.fetchStations());
        Long downStationId = responseConverter.convertToId(stationClient.createStation(선릉역_이름));
        return new nextstep.subway.client.dto.SectionRegistrationRequest(
                stationIds.get(stationIds.size() - 1),
                downStationId,
                TEN
        );
    }

    public nextstep.subway.client.dto.SectionRegistrationRequest 선릉_역삼_구간_생성_요청(int distance) {
        List<Long> stationIds = responseConverter.convertToIds(stationClient.fetchStations());
        Long upStationId = responseConverter.convertToId(stationClient.createStation(선릉역_이름));
        return new nextstep.subway.client.dto.SectionRegistrationRequest(
                upStationId,
                stationIds.get(stationIds.size() - 1),
                distance
        );
    }

    public nextstep.subway.client.dto.SectionRegistrationRequest 교대_강남_구간_생성_요청() {
        List<Long> stationIds = responseConverter.convertToIds(stationClient.fetchStations());
        Long upStationId = responseConverter.convertToId(stationClient.createStation(교대역_이름));
        return new nextstep.subway.client.dto.SectionRegistrationRequest(
                upStationId,
                stationIds.get(0),
                TEN
        );
    }

    public nextstep.subway.client.dto.SectionRegistrationRequest 강남_교대_구간_생성_요청(int distance) {
        List<Long> stationIds = responseConverter.convertToIds(stationClient.fetchStations());
        Long downStationId = responseConverter.convertToId(stationClient.createStation(교대역_이름));
        return new nextstep.subway.client.dto.SectionRegistrationRequest(
                stationIds.get(0),
                downStationId,
                distance
        );
    }

}
