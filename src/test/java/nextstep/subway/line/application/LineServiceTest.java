package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.line.exception.LineAlreadyExistException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.CannotRemoveStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("노선 비즈니스 로직 단위 테스트")
@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    private Station savedStationGangnam;
    private Station savedStationYeoksam;
    private Station savedStationSamseong;

    private LineRequest line2Request;

    @BeforeEach
    void setUp() {
        savedStationGangnam = stationRepository.save(new Station("강남역"));
        savedStationYeoksam = stationRepository.save(new Station("역삼역"));
        savedStationSamseong = stationRepository.save(new Station("삼성역"));

        line2Request = new LineRequest("2호선", "bg-green-600", savedStationGangnam.getId(), savedStationYeoksam.getId(), 10);
    }

    @Test
    @DisplayName("노선 저장")
    void saveLine() {
        // when
        LineResponse savedLineResponse = lineService.saveLine(line2Request);

        // then
        assertThat(savedLineResponse).isNotNull();
        assertThat(savedLineResponse.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("노선 저장 시 존재하는 이름이 있으면 에러 발생")
    void validateNameToSaveLine() {
        // given
        lineService.saveLine(line2Request);

        // when & then
        assertThatExceptionOfType(LineAlreadyExistException.class)
                .isThrownBy(() -> {
                    LineRequest line2Request2 = new LineRequest("2호선", "bg-green-600");
                    lineService.saveLine(line2Request2);
                });
    }

    @Test
    @DisplayName("노선 수정")
    void updateLine() {
        // given
        LineResponse savedLine2Response = lineService.saveLine(line2Request);

        // when
        LineResponse updatedLine2Response = lineService.updateLine(savedLine2Response.getId(), new LineRequest("2호선", "bg-green-100"));

        // then
        assertThat(updatedLine2Response.getColor()).isEqualTo("bg-green-100");
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        // given
        LineResponse savedLine2Response = lineService.saveLine(line2Request);

        // when
        lineService.deleteLineById(savedLine2Response.getId());

        // then
        assertThat(lineService.findAllLines()).hasSize(0);
    }

    @Test
    @DisplayName("모든 노선 조회")
    void findAllLines() {
        // given
        lineService.saveLine(line2Request);

        Station savedStationYangJae = stationRepository.save(new Station("양재역"));
        LineRequest lineNewBundangRequest = new LineRequest("신분당선", "bg-red-600", savedStationGangnam.getId(), savedStationYangJae.getId(), 4);
        lineService.saveLine(lineNewBundangRequest);

        // when
        List<LineResponse> lineResponses = lineService.findAllLines();

        // then
        assertThat(lineResponses).hasSize(2);
    }

    @Test
    @DisplayName("노선에 구간 추가")
    void addSection() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);

        // when
        lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // then
        Line line = lineService.findLineById(savedLineResponse.getId());
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 구간 삭제")
    void deleteSection() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);

        savedLineResponse = lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // when
        lineService.deleteSectionToLine(savedLineResponse.getId(), savedStationSamseong.getId());

        // then
        Line resultLine = lineService.findLineById(savedLineResponse.getId());
        assertThat(resultLine.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("노선에 구간 삭제 시 하행 종점역이 아니면 에러 발생")
    void validateDownStationToDeleteSection() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);
        lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // when & then
        assertThatExceptionOfType(CannotRemoveStationException.class)
                .isThrownBy(() -> {
                    lineService.deleteSectionToLine(savedLineResponse.getId(), savedStationYeoksam.getId());
                });
    }

    @Test
    @DisplayName("노선에 구간 삭제 시 구간이 1개만 있을 경우 에러 발생")
    void validateSectionSizeToDeleteSection() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);

        // when & then
        assertThatExceptionOfType(CannotRemoveSectionException.class)
                .isThrownBy(() -> lineService.deleteSectionToLine(savedLineResponse.getId(), savedStationYeoksam.getId()));
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation, int distance) {
        return new SectionRequest(upStation.getId(), downStation.getId(), distance);
    }
}
