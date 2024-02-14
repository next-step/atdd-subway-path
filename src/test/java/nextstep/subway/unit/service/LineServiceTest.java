package nextstep.subway.unit.service;

import nextstep.subway.common.Constant;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.response.ShowLineResponse;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.service.SectionDto;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.service.StationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @DisplayName("노선 마지막에 구간을 추가한다.")
    @Test
    void 노선_마지막에_구간_등록() {
        // given
        Line 신분당선 = lineRepository.save(Line.of(Constant.신분당선, Constant.빨간색));
        Long 신분당선_ID = 신분당선.getLineId();
        Station 신논현역 = stationRepository.save(Station.from(Constant.신논현역));
        Long 신논현역_ID = 신논현역.getStationId();
        Station 강남역 = stationRepository.save(Station.from(Constant.강남역));
        Long 강남역_ID = 강남역.getStationId();
        Station 양재역 = stationRepository.save(Station.from(Constant.양재역));
        Long 양재역_ID = 양재역.getStationId();
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(강남역_ID, 양재역_ID, Constant.역_간격_10));

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<SectionDto> 신분당선_구간들 = 신분당선_조회_응답.getSections();
        assertTrue(신분당선_구간들.stream()
                .anyMatch(sectionDto ->
                        sectionDto.getUpStation().equals(StationDto.from(강남역))
                                && sectionDto.getDownStation().equals(StationDto.from(양재역))
                ));
    }

    @DisplayName("노선 중간에 구간을 추가한다.")
    @Test
    void 노선_중간에_구간_등록() {
        // given
        Line 신분당선 = lineRepository.save(Line.of(Constant.신분당선, Constant.빨간색));
        Long 신분당선_ID = 신분당선.getLineId();
        Station 신논현역 = stationRepository.save(Station.from(Constant.신논현역));
        Long 신논현역_ID = 신논현역.getStationId();
        Station 강남역 = stationRepository.save(Station.from(Constant.강남역));
        Long 강남역_ID = 강남역.getStationId();
        Station 양재역 = stationRepository.save(Station.from(Constant.양재역));
        Long 양재역_ID = 양재역.getStationId();
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 양재역_ID, Constant.역_간격_5));

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        assertTrue(신분당선_조회_응답.getSections().stream()
                .anyMatch(sectionDto ->
                        sectionDto.getUpStation().equals(StationDto.from(신논현역))
                                && sectionDto.getDownStation().equals(StationDto.from(양재역))
                ));
        assertTrue(신분당선_조회_응답.getSections().stream()
                .anyMatch(sectionDto ->
                        sectionDto.getUpStation().equals(StationDto.from(양재역))
                                && sectionDto.getDownStation().equals(StationDto.from(강남역))
                ));
    }

    @DisplayName("노선 처음에 구간을 추가한다.")
    @Test
    void 노선_처음에_구간_등록() {
        // given
        Line 신분당선 = lineRepository.save(Line.of(Constant.신분당선, Constant.빨간색));
        Long 신분당선_ID = 신분당선.getLineId();
        Station 논현역 = stationRepository.save(Station.from(Constant.논현역));
        Long 논현역_ID = 논현역.getStationId();
        Station 신논현역 = stationRepository.save(Station.from(Constant.신논현역));
        Long 신논현역_ID = 신논현역.getStationId();
        Station 강남역 = stationRepository.save(Station.from(Constant.강남역));
        Long 강남역_ID = 강남역.getStationId();
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        assertTrue(신분당선_조회_응답.getSections().stream()
                .anyMatch(sectionDto ->
                        sectionDto.getUpStation().equals(StationDto.from(논현역))
                                && sectionDto.getDownStation().equals(StationDto.from(신논현역))
                ));
        assertTrue(신분당선_조회_응답.getSections().stream()
                .anyMatch(sectionDto ->
                        sectionDto.getUpStation().equals(StationDto.from(신논현역))
                                && sectionDto.getDownStation().equals(StationDto.from(강남역))
                ));
    }

}
