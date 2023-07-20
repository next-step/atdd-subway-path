package nextstep.subway.unit;

import nextstep.marker.ClassicUnitTest;
import nextstep.subway.common.NotFoundLineException;
import nextstep.subway.controller.request.LineCreateRequest;
import nextstep.subway.controller.request.LineModifyRequest;
import nextstep.subway.controller.request.SectionAddRequest;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.command.SectionAddCommand;
import nextstep.subway.domain.exception.IllegalSectionStationException;
import nextstep.subway.domain.exception.NotEnoughSectionException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;

@ClassicUnitTest
class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Line secondaryLine;
    private Station gangnameStation;
    private Station eonjuStation;
    private Station seongsuStation;

    @BeforeEach
    void setUp() {
        gangnameStation = getStation("강남역");
        eonjuStation = getStation("언주역");
        seongsuStation = getStation("성수역");
        secondaryLine = getLine("2호선", "bg-green-300", gangnameStation, eonjuStation, 10L);
    }

    @Test
    void 노선_생성() {
        // given
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "bg-green-300", gangnameStation.getId(), eonjuStation.getId(), 10L);

        // when
        LineResponse response = lineService.saveLine(lineCreateRequest);

        // then
        verifyLineResponse(response, "2호선", "bg-green-300", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_목록_조회() {
        // when
        List<LineResponse> lines = lineService.findAllLines();

        // then
        assertThat(lines).hasSize(1);
        LineResponse response = lines.get(0);
        verifyLineResponse(response, "2호선", "bg-green-300", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_단건_조회() {
        // when
        LineResponse response = lineService.findLineById(secondaryLine.getId());

        // then
        verifyLineResponse(response, "2호선", "bg-green-300", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_단건_조회_했으나_찾지_못한_경우() {
        // when & then
        thenCode(() -> lineService.findLineById(10L)).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void 노선_정보_수정() {
        // given
        LineModifyRequest request = new LineModifyRequest(("3호선"), "bg-red-700");

        // when
        lineService.modifyLine(secondaryLine.getId(), request);

        // then
        LineResponse response = lineService.findLineById(secondaryLine.getId());
        verifyLineResponse(response, "3호선", "bg-red-700", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_제거() {
        // when
        lineService.deleteLineById(secondaryLine.getId());

        // then
        thenCode(() -> lineService.findLineById(secondaryLine.getId())).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void 구간_추가() {
        // given
        SectionAddCommand sectionAddCommand = new SectionAddRequest((eonjuStation.getId()), seongsuStation.getId(), 3L);

        // when
        lineService.addSection(secondaryLine.getId(), sectionAddCommand);

        // then
        LineResponse response = lineService.findLineById(secondaryLine.getId());
        verifyLineResponse(response, "2호선", "bg-green-300", 13L);
        verifyStationResponse(response, "강남역", "언주역", "성수역");
    }


    @Nested
    class 구간_제거 {

        @Test
        void 상행역_구간_제거() {
            // given
            SectionAddCommand sectionAddCommand = new SectionAddRequest((eonjuStation.getId()), seongsuStation.getId(), 3L);
            lineService.addSection(secondaryLine.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "성수역");

            // when
            lineService.deleteStationAtLine(secondaryLine.getId(), gangnameStation.getId());

            // then
            LineResponse deletedStationResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 3L);
            verifyStationResponse(deletedStationResponse, "언주역", "성수역");
        }

        @Test
        void 하행역_구간_제거() {
            // given
            SectionAddCommand sectionAddCommand = new SectionAddRequest((eonjuStation.getId()), seongsuStation.getId(), 3L);
            lineService.addSection(secondaryLine.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "성수역");

            // when
            lineService.deleteStationAtLine(secondaryLine.getId(), seongsuStation.getId());

            // then
            LineResponse deletedStationResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역");
        }

        @Test
        void 중간역_구간_제거() {
            // given
            SectionAddCommand sectionAddCommand = new SectionAddRequest((eonjuStation.getId()), seongsuStation.getId(), 3L);
            lineService.addSection(secondaryLine.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "성수역");

            // when
            lineService.deleteStationAtLine(secondaryLine.getId(), eonjuStation.getId());

            // then
            LineResponse deletedStationResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(deletedStationResponse, "강남역", "성수역");
        }

        @Test
        void 없는_역을_제거_할수_없다() {
            // given
            Station notInSectionStation = getStation("고척역");

            SectionAddCommand sectionAddCommand = new SectionAddRequest((eonjuStation.getId()), seongsuStation.getId(), 3L);
            lineService.addSection(secondaryLine.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "성수역");

            // when & then
            thenCode(() -> lineService.deleteStationAtLine(secondaryLine.getId(), notInSectionStation.getId())).isInstanceOf(IllegalSectionStationException.class);

            // then
            LineResponse deletedStationResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역", "성수역");
        }

        @Test
        void 단일_구간_상행역_제거_불가() {
            // given
            LineResponse savedLineResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역");

            // when & then
            thenCode(() -> lineService.deleteStationAtLine(secondaryLine.getId(), gangnameStation.getId())).isInstanceOf(NotEnoughSectionException.class);

            // then
            LineResponse deletedStationResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역");
        }

        @Test
        void 단일_구간_하행역_제거_불가() {
            // given
            LineResponse savedLineResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역");

            // when & then
            thenCode(() -> lineService.deleteStationAtLine(secondaryLine.getId(), eonjuStation.getId())).isInstanceOf(NotEnoughSectionException.class);

            // then
            LineResponse deletedStationResponse = lineService.findLineById(secondaryLine.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역");
        }
    }

    private Line getLine(String name, String color, Station upStation, Station downStation, long distance) {
        Line secondaryLine = Line.builder()
                .name(name)
                .color(color)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
        return lineRepository.save(secondaryLine);
    }

    private Station getStation(String name) {
        return stationRepository.save(Station.create(() -> name));
    }

    private void verifyLineResponse(LineResponse response, String name, String color, long distance) {
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(color, response.getColor());
        Assertions.assertEquals(distance, response.getDistance());
    }

    private void verifyStationResponse(LineResponse response, String... stationNames) {
        List<StationResponse> stations = response.getStations();
        assertThat(stations).hasSize(stationNames.length)
                .map(StationResponse::getName)
                .containsExactly(stationNames);
    }
}
