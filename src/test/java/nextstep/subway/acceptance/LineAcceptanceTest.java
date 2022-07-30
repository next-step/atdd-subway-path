package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.client.LineClient;
import nextstep.subway.client.StationClient;
import nextstep.subway.client.dto.LineCreationRequest;
import nextstep.subway.client.dto.SectionRegistrationRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.exception.InvalidDistanceBetweenStationsException;
import nextstep.subway.exception.NoLastStationException;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import nextstep.subway.utils.GivenUtils;
import nextstep.subway.utils.HttpStatusValidator;
import nextstep.subway.utils.JsonResponseConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.GREEN;
import static nextstep.subway.utils.GivenUtils.TEN;
import static nextstep.subway.utils.GivenUtils.YELLOW;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.강남역_이름;
import static nextstep.subway.utils.GivenUtils.교대역_이름;
import static nextstep.subway.utils.GivenUtils.분당선_이름;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.선릉역_이름;
import static nextstep.subway.utils.GivenUtils.신분당선_이름;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.역삼역_이름;
import static nextstep.subway.utils.GivenUtils.이호선;
import static nextstep.subway.utils.GivenUtils.이호선_이름;
import static nextstep.subway.utils.GivenUtils.이호선역_이름들;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    StationClient stationClient;

    @Autowired
    LineClient lineClient;

    @Autowired
    HttpStatusValidator statusValidator;

    @Autowired
    JsonResponseConverter responseConverter;
    
    @Autowired
    GivenUtils givenUtils;

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    @DirtiesContext
    void createLine() {
        // given

        // when
        ExtractableResponse<Response> response = lineClient.createLine(givenUtils.이호선_생성_요청());

        // then
        statusValidator.validateCreated(response);
        assertThat(responseConverter.convertToNames(lineClient.fetchLines()))
                .contains(이호선_이름);
    }

    /**
     * When 존재하지 않는 역의 id로 노선을 생성하면
     * Then 오류(EntityNotFoundException) 객체를 반환한다
     */
    @DisplayName("존재하지 않는 역의 id로 지하철 노선을 생성한다.")
    @Test
    @DirtiesContext
    void createLineByInvalidStationIds() {
        // given
        long upStationId = 1L;
        long downStationId = 2L;
        LineCreationRequest lineRequest = new LineCreationRequest(
                이호선_이름,
                GREEN,
                upStationId,
                downStationId,
                TEN
        );

        // when
        ExtractableResponse<Response> response = lineClient.createLine(lineRequest);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(EntityNotFoundException.class.getName());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    @DirtiesContext
    void getLines() {
        // given
        int stationSize = 2;
        int lineSize = 2;
        lineClient.createLine(givenUtils.이호선_생성_요청());
        lineClient.createLine(givenUtils.신분당선_생성_요청());

        // when
        ExtractableResponse<Response> linesResponse = lineClient.fetchLines();

        // then
        statusValidator.validateOk(linesResponse);
        assertThat(responseConverter.convertToList(linesResponse, "stations"))
                .allMatch(fetchStations -> fetchStations.size() == stationSize);
        assertThat(responseConverter.convertToNames(linesResponse))
                .hasSize(lineSize)
                .containsExactly(이호선_이름, 신분당선_이름);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    @DirtiesContext
    void getLine() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.fetchLine(lineId);

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convert(response, "stations", List.class))
                .hasSize(이호선역_이름들.size());
        assertThat(responseConverter.convertToName(response)).isEqualTo(이호선_이름);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    @DirtiesContext
    void modifyLine() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.modifyLine(lineId, 분당선_이름, YELLOW);

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convertToNames(lineClient.fetchLines()))
                .containsExactly(분당선_이름);
    }

    /**
     * When 존재하지 않는 지하철 노선을 수정하면
     * Then 오류(EntityNotFoundException) 객체를 반환한다
     */
    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    @DirtiesContext
    void modifyNoneExistentLine() {
        // given
        long notExistsLineId = 1L;

        // when
        ExtractableResponse<Response> response =
                lineClient.modifyLine(notExistsLineId, 분당선_이름, YELLOW);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(EntityNotFoundException.class.getName());
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    @DirtiesContext
    void deleteLine() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.deleteLine(lineId);

        // then
        statusValidator.validateNoContent(response);
        assertThat(responseConverter.convertToNames(lineClient.fetchLines()))
                .doesNotContain(이호선_이름);
    }

    /**
     * When 존재하지 않는 지하철 노선을 삭제하면
     * Then 오류(EmptyResultDataAccessException) 객체를 반환한다
     */
    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    @DirtiesContext
    void deleteNonExistentLine() {
        // given
        long notExistsLineId = 1L;

        // when
        ExtractableResponse<Response> response = lineClient.deleteLine(notExistsLineId);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(EmptyResultDataAccessException.class.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선의 하행 종점 뒤에 구간을 추가하면
     * Then 해당 구간이 노선 구간 목록에 존재한다
     */
    @Test
    @DisplayName("지하철 노선에 구간을 등록 - 하행 종점 뒤에 추가")
    @DirtiesContext
    void addSectionNextToTail() {
        // given
        int expectedSize = 3;
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, givenUtils.역삼_선릉_구간_생성_요청());

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convert(lineClient.fetchLine(lineId), "stations", List.class))
                .hasSize(expectedSize);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선의 하행 종점 앞에 구간을 추가하면
     * Then 해당 구간이 노선 구간 목록에 존재한다
     */
    @Test
    @DisplayName("지하철 노선에 구간을 등록 - 하행 종점 앞에 추가")
    @DirtiesContext
    void addSectionInFrontOfTail() {
        // given
        int expectedSize = 3;
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, givenUtils.선릉_역삼_구간_생성_요청(FIVE));

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convert(lineClient.fetchLine(lineId), "stations", List.class))
                .hasSize(expectedSize);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선의 상행 종점 뒤에 구간을 추가하면
     * Then 해당 구간이 노선 구간 목록에 존재한다
     */
    @Test
    @DisplayName("지하철 노선에 구간을 등록 - 상행 종점 뒤에 추가")
    @DirtiesContext
    void addSectionNextToHead() {
        // given
        int expectedSize = 3;
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, givenUtils.강남_교대_구간_생성_요청(FIVE));

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convert(lineClient.fetchLine(lineId), "stations", List.class))
                .hasSize(expectedSize);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선의 상행 종점 앞에 구간을 추가하면
     * Then 해당 구간이 노선 구간 목록에 존재한다
     */
    @Test
    @DisplayName("지하철 노선에 구간을 등록 - 상행 종점 앞에 추가")
    @DirtiesContext
    void addSectionInFrontOfHead() {
        // given
        int expectedSize = 3;
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, givenUtils.교대_강남_구간_생성_요청());

        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convert(lineClient.fetchLine(lineId), "stations", List.class))
                .hasSize(expectedSize);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선 하행 종점 앞에 추가 (역 사이 거리보다 같거나 큰 구간 추가) 하면
     * Then 오류(InvalidDistanceBetweenStationsException) 객체를 반환한다
     */
    @Test
    @DisplayName("지하철 노선에 구간 등록 실패 - 하행 종점 앞에 추가 (역 사이 거리보다 같거나 큰 구간 추가)")
    @DirtiesContext
    void addSectionInFrontOfTailWithInvalidDistance() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());
        Long 선릉역_Id = responseConverter.convertToId(stationClient.createStation(선릉역_이름));
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                선릉역_Id,
                역삼역().getId(),
                TEN
        );

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, sectionRequest);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(InvalidDistanceBetweenStationsException.class.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선 상행 종점 뒤에 추가 (역 사이 거리보다 같거나 큰 구간 추가) 하면
     * Then 오류(InvalidDistanceBetweenStationsException) 객체를 반환한다
     */
    @Test
    @DisplayName("지하철 노선에 구간 등록 실패 - 상행 종점 뒤에 추가 (역 사이 거리보다 같거나 큰 구간 추가)")
    @DirtiesContext
    void addSectionNextToHeadWithInvalidDistance() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());
        Long 선릉역_Id = responseConverter.convertToId(stationClient.createStation(선릉역_이름));
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                강남역().getId(),
                선릉역_Id,
                TEN
        );

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, sectionRequest);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(InvalidDistanceBetweenStationsException.class.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선에 이미 등록된 상행, 하행역을 추가 하면
     * Then 오류(SectionRegistrationException) 객체를 반환한다
     */
    @Test
    @DisplayName("지하철 노선에 구간 등록 실패 - 등록하려는 상행, 하행역이 노선에 이미 등록된 경우")
    @DirtiesContext
    void addSectionWithDuplicatedStations() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                강남역().getId(),
                역삼역().getId(),
                TEN
        );

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, sectionRequest);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(SectionRegistrationException.class.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선의 상행, 하행 종점에 포함되지 않는 상행, 하행역을 추가 하면
     * Then 오류(SectionRegistrationException) 객체를 반환한다
     */
    @Test
    @DisplayName("지하철 노선에 구간 등록 실패 - 등록하려는 역이 노선의 상행, 하행역에 포함되지 않는 경우")
    @DirtiesContext
    void addSectionWithInvalidStations() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());
        Long 선릉역_Id = responseConverter.convertToId(stationClient.createStation(선릉역_이름));
        Long 교대역_Id = responseConverter.convertToId(stationClient.createStation(교대역_이름));
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                선릉역_Id,
                교대역_Id,
                TEN
        );

        // when
        ExtractableResponse<Response> response = lineClient.addSection(lineId, sectionRequest);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(SectionRegistrationException.class.getName());
    }

    /**
     * Given 지하철 노선을 생성하고 구간을 추가한 후
     * When 지하철 노선에 구간을 삭제하면
     * Then 해당 구간이 노선 구간 목록에서 존재하지 않는다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    @DirtiesContext
    void removeSection() {
        // given
        int expectedSize = 2;
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());
        lineClient.addSection(lineId, givenUtils.역삼_선릉_구간_생성_요청());
        long stationId = 3L;

        // when
        ExtractableResponse<Response> response = lineClient.removeSection(lineId, stationId);

        // then
        statusValidator.validateNoContent(response);
        assertThat(responseConverter.convert(lineClient.fetchLine(lineId), "stations", List.class))
                .hasSize(expectedSize);
    }

    /**
     * Given 지하철 노선을 생성하고 구간을 추가한 후
     * When 지하철 노선에 하행 종점역이 아닌 다른 역을 삭제하면
     * Then Then 오류(NoLastStationException) 객체를 반환한다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 하행 종점역이 아닌 다른 역 제거")
    @Test
    @DirtiesContext
    void removeSectionWithInvalidLastStation() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());
        lineClient.addSection(lineId, givenUtils.역삼_선릉_구간_생성_요청());

        // when
        ExtractableResponse<Response> response = lineClient.removeSection(lineId, 역삼역().getId());

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(NoLastStationException.class.getName());
    }

    /**
     * Given 지하철 노선을 생성하고 구간을 추가한 후
     * When 지하철 노선에 하행 종점역이 아닌 다른 역을 삭제하면
     * Then Then 오류(SectionRemovalException) 객체를 반환한다
     */
    @DisplayName("지하철 노선에 구간을 제거 - 구간이 1개인 경우")
    @Test
    @DirtiesContext
    void removeSingleSection() {
        // given
        long lineId = responseConverter.convertToId(givenUtils.이호선_생성());

        // when
        ExtractableResponse<Response> response = lineClient.removeSection(lineId, 역삼역().getId());

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(SectionRemovalException.class.getName());
    }

}