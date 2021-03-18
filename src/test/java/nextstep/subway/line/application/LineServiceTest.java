package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.line.exception.LineAlreadyExistException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
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

    private Station savedStationGyoDae;
    private Station savedStationGangnam;
    private Station savedStationYeoksam;
    private Station savedStationSeolleung;
    private Station savedStationSamseong;

    private LineRequest line2Request;

    @BeforeEach
    void setUp() {
        savedStationGyoDae = stationRepository.save(new Station("교대역"));
        savedStationGangnam = stationRepository.save(new Station("강남역"));
        savedStationYeoksam = stationRepository.save(new Station("역삼역"));
        savedStationSeolleung = stationRepository.save(new Station("선릉역"));
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
        assertThat(lineService.findAllLineResponses()).hasSize(0);
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
        List<LineResponse> lineResponses = lineService.findAllLineResponses();

        // then
        assertThat(lineResponses).hasSize(2);
    }

    @Test
    @DisplayName("노선에 신규 상행역 구간 추가")
    void addSectionInUp() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);

        // when
        lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationGyoDae, savedStationGangnam, 5));

        // then
        Line line = lineService.findLineById(savedLineResponse.getId());
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGyoDae, savedStationGangnam, savedStationYeoksam));
    }

    @Test
    @DisplayName("노선 중간에 신규 구간 추가")
    void addSectionInMiddle() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);
        lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // when
        lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSeolleung, 3));

        // then
        Line line = lineService.findLineById(savedLineResponse.getId());
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSeolleung, savedStationSamseong));
    }

    @Test
    @DisplayName("노선의 하행역에 신규 구간 추가")
    void addSectionInDown() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);

        // when
        lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // then
        Line line = lineService.findLineById(savedLineResponse.getId());
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 있는 상행 종점역 구간 제거")
    void removeUpStationSection() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);
        savedLineResponse = lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));

        // when
        lineService.deleteSectionToLine(savedLineResponse.getId(), savedStationGangnam.getId());

        // then
        Line resultLine = lineService.findLineById(savedLineResponse.getId());
        assertThat(resultLine.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("노선에 있는 중간 구간 제거")
    void removeMiddleSection() {
        // given
        LineResponse savedLineResponse = lineService.saveLine(line2Request);
        savedLineResponse = lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSamseong, 6));
        savedLineResponse = lineService.addSectionToLine(savedLineResponse.getId(), createSectionRequest(savedStationYeoksam, savedStationSeolleung, 3));

        // when
        lineService.deleteSectionToLine(savedLineResponse.getId(), savedStationYeoksam.getId());

        // then
        Line resultLine = lineService.findLineById(savedLineResponse.getId());
        assertThat(resultLine.getSections()).hasSize(2);
    }

    @Test
    @DisplayName("노선에 있는 하행 종점역 구간 제거")
    void removeDownStationSection() {
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
