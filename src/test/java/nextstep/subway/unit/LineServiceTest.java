package nextstep.subway.unit;

import nextstep.marker.ClassicUnitTest;
import nextstep.subway.common.NotFoundLineException;
import nextstep.subway.controller.request.LineCreateRequest;
import nextstep.subway.controller.request.LineModifyRequest;
import nextstep.subway.controller.request.SectionAddRequest;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.IllegalSectionStationException;
import nextstep.subway.domain.exception.NotEnoughSectionException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.domain.command.SectionAddCommand;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void 노선_생성() {
        // given
        Station upStation = getStation("강남역");
        Station downStation = getStation("언주역");
        LineCreateRequest lineCreateRequest = new LineCreateRequest("2호선", "bg-green-300", upStation.getId(), downStation.getId(), 10L);

        // when
        LineResponse response = lineService.saveLine(lineCreateRequest);

        // then
        verifyLineResponse(response, "2호선", "bg-green-300", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_목록_조회() {
        // given
        Station upStation = getStation("강남역");
        Station downStation = getStation("언주역");
        getLine("2호선", "bg-green-300", upStation, downStation, 10L);

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
        // given
        Station upStation = getStation("강남역");
        Station downStation = getStation("언주역");
        Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

        // when
        LineResponse response = lineService.findLineById(line.getId());

        // then
        verifyLineResponse(response, "2호선", "bg-green-300", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_단건_조회_했으나_찾지_못한_경우() {
        // when & then
        thenCode(() -> lineService.findLineById(1L)).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void 노선_정보_수정() {
        // given
        Station upStation = getStation("강남역");
        Station downStation = getStation("언주역");
        Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);
        LineModifyRequest request = new LineModifyRequest(("3호선"), "bg-red-700");

        // when
        lineService.modifyLine(line.getId(), request);

        // then
        LineResponse response = lineService.findLineById(line.getId());
        verifyLineResponse(response, "3호선", "bg-red-700", 10L);
        verifyStationResponse(response, "강남역", "언주역");
    }

    @Test
    void 노선_제거() {
        // given
        Station upStation = getStation("강남역");
        Station downStation = getStation("언주역");
        Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

        // when
        lineService.deleteLineById(line.getId());

        // then
        thenCode(() -> lineService.findLineById(line.getId())).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void 구간_추가() {
        // given
        Station upStation = getStation("강남역");
        Station downStation = getStation("언주역");
        Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

        Station newStation = getStation("길음역");

        SectionAddCommand sectionAddCommand = new SectionAddRequest((downStation.getId()), newStation.getId(), 3L);

        // when
        lineService.addSection(line.getId(), sectionAddCommand);

        // then
        LineResponse response = lineService.findLineById(line.getId());
        verifyLineResponse(response, "2호선", "bg-green-300", 13L);
        verifyStationResponse(response, "강남역", "언주역", "길음역");
    }


    @Nested
    class 구간_제거 {

        @Test
        void 상행역_구간_제거() {
            // given
            Station upStation = getStation("강남역");
            Station downStation = getStation("언주역");
            Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

            Station newStation = getStation("길음역");

            SectionAddCommand sectionAddCommand = new SectionAddRequest((downStation.getId()), newStation.getId(), 3L);
            lineService.addSection(line.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(line.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "길음역");

            // when
            lineService.deleteStationAtLine(line.getId(), upStation.getId());

            // then
            LineResponse deletedStationResponse = lineService.findLineById(line.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 3L);
            verifyStationResponse(deletedStationResponse,  "언주역", "길음역");
        }

        @Test
        void 하행역_구간_제거() {
            // given
            Station upStation = getStation("강남역");
            Station downStation = getStation("언주역");
            Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

            Station newStation = getStation("길음역");

            SectionAddCommand sectionAddCommand = new SectionAddRequest((downStation.getId()), newStation.getId(), 3L);
            lineService.addSection(line.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(line.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "길음역");

            // when
            lineService.deleteStationAtLine(line.getId(), newStation.getId());

            // then
            LineResponse deletedStationResponse = lineService.findLineById(line.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역");
        }

        @Test
        void 중간역_구간_제거() {
            // given
            Station upStation = getStation("강남역");
            Station downStation = getStation("언주역");
            Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

            Station newStation = getStation("길음역");

            SectionAddCommand sectionAddCommand = new SectionAddRequest((downStation.getId()), newStation.getId(), 3L);
            lineService.addSection(line.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(line.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "길음역");

            // when
            lineService.deleteStationAtLine(line.getId(), downStation.getId());

            // then
            LineResponse deletedStationResponse = lineService.findLineById(line.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(deletedStationResponse, "강남역", "길음역");
        }

        @Test
        void 없는_역을_제거_할수_없다() {
            // given
            Station upStation = getStation("강남역");
            Station downStation = getStation("언주역");
            Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

            Station newStation = getStation("길음역");
            Station notInSectionStation = getStation("고척역");

            SectionAddCommand sectionAddCommand = new SectionAddRequest((downStation.getId()), newStation.getId(), 3L);
            lineService.addSection(line.getId(), sectionAddCommand);

            LineResponse savedLineResponse = lineService.findLineById(line.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역", "길음역");

            // when & then
            thenCode(() -> lineService.deleteStationAtLine(line.getId(), notInSectionStation.getId())).isInstanceOf(IllegalSectionStationException.class);

            // then
            LineResponse deletedStationResponse = lineService.findLineById(line.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 13L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역", "길음역");
        }

        @Test
        void 단일_구간_상행역_제거_불가() {
            // given
            Station upStation = getStation("강남역");
            Station downStation = getStation("언주역");
            Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

            LineResponse savedLineResponse = lineService.findLineById(line.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역");

            // when & then
            thenCode(() -> lineService.deleteStationAtLine(line.getId(), upStation.getId())).isInstanceOf(NotEnoughSectionException.class);

            // then
            LineResponse deletedStationResponse = lineService.findLineById(line.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역");
        }

        @Test
        void 단일_구간_하행역_제거_불가() {
            // given
            Station upStation = getStation("강남역");
            Station downStation = getStation("언주역");
            Line line = getLine("2호선", "bg-green-300", upStation, downStation, 10L);

            LineResponse savedLineResponse = lineService.findLineById(line.getId());
            verifyLineResponse(savedLineResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(savedLineResponse, "강남역", "언주역");

            // when & then
            thenCode(() -> lineService.deleteStationAtLine(line.getId(), downStation.getId())).isInstanceOf(NotEnoughSectionException.class);

            // then
            LineResponse deletedStationResponse = lineService.findLineById(line.getId());
            verifyLineResponse(deletedStationResponse, "2호선", "bg-green-300", 10L);
            verifyStationResponse(deletedStationResponse, "강남역", "언주역");
        }
    }


    private Line getLine(String name, String color, Station upStation, Station downStation, long distance) {
        Line line = Line.builder()
                .name(name)
                .color(color)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
        return lineRepository.save(line);
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
