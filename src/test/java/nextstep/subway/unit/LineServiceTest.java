package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.assertj.core.api.Assertions;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("지하철 노선에 새로운 정착역을 바탕으로 구간을 추가한다")
    void addSection() {
        // given
        Long 강남역 = 주어진_역_이름을_바탕으로_지하철역을_생성한다("강남역");
        Long 정자역 = 주어진_역_이름을_바탕으로_지하철역을_생성한다("정자역");
        Long 신분당선 = 주어진_종착역을_바탕으로_신분당선_노선을_생성한다(강남역, 정자역, 10);

        Long 새로운_종착역 = 주어진_역_이름을_바탕으로_지하철역을_생성한다("새로운_종착역");

        // when
        lineService.addSection(신분당선, new SectionRequest(정자역, 새로운_종착역, 5));

        // then
        Line line = lineRepository.findById(신분당선).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections();
        Assertions.assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가한다")
    void addSectionInPreviousSection() {
        // given
        Long 강남역 = 주어진_역_이름을_바탕으로_지하철역을_생성한다("강남역");
        Long 정자역 = 주어진_역_이름을_바탕으로_지하철역을_생성한다("정자역");
        Long 신분당선 = 주어진_종착역을_바탕으로_신분당선_노선을_생성한다(강남역, 정자역, 10);

        Long 중간역 = 주어진_역_이름을_바탕으로_지하철역을_생성한다("중간역");

        // when
        lineService.addSection(신분당선, new SectionRequest(강남역, 중간역, 4));

        // then
        Line line = lineRepository.findById(신분당선).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections();
        Assertions.assertThat(sections.size()).isEqualTo(2);
        구간들이_위치에_맞게_등록되었는지_검증한다(신분당선, 강남역, 정자역, 중간역);
    }

    void 구간들이_위치에_맞게_등록되었는지_검증한다(Long lineId, Long 상행_종착역, Long 하행_종착역, Long 중간역) {
        List<Long> sectionIds = 주어진_노선에_속하는_구간의_역들을_순서대로_정렬한다(lineId);
        Long 상행_종착역_중간역_구간_아이디 = sectionIds.get(0);
        Long 중간역_하행_종착역_구간_아이디 = sectionIds.get(1);

        Section 상행_종착역_중간역_구간 = 주어진_구간_아이디를_바탕으로_노선의_구간_조회(lineId, 상행_종착역_중간역_구간_아이디);
        Section 중간역_하행_종착역_구간 = 주어진_구간_아이디를_바탕으로_노선의_구간_조회(lineId, 중간역_하행_종착역_구간_아이디);

        Assertions.assertThat(상행_종착역_중간역_구간.getUpStationId()).isEqualTo(상행_종착역);
        Assertions.assertThat(상행_종착역_중간역_구간.getDownStationId()).isEqualTo(중간역);
        Assertions.assertThat(중간역_하행_종착역_구간.getUpStationId()).isEqualTo(중간역);
        Assertions.assertThat(중간역_하행_종착역_구간.getDownStationId()).isEqualTo(하행_종착역);
    }

    Section 주어진_구간_아이디를_바탕으로_노선의_구간_조회(Long lineId, Long sectionId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections();

        for (Section section : sections) {
            if(section.getId() == sectionId) {
                return section;
            }
        }

        throw new NoSuchElementException();
    }

    Long 주어진_종착역을_바탕으로_신분당선_노선을_생성한다(Long finalUpStationId, Long finalDownStationId, int distance) {
        LineRequest lineRequest = new LineRequest("신분당선", "red", finalUpStationId, finalDownStationId, distance);
        return 지하철_노선을_생성한다(lineRequest);
    }

    List<Long> 주어진_노선에_속하는_구간의_역들을_순서대로_정렬한다(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        List<Section> sections = line.getSections();

        Long source = line.getFinalUpStationId();
        Long target = line.getFinalDownStationId();

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Section s : sections) {
            graph.addVertex(s.getUpStationId());
            graph.addVertex(s.getDownStationId());
            graph.setEdgeWeight(graph.addEdge(s.getUpStationId(), s.getDownStationId()), s.getDistance());
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Long> vertexList = dijkstraShortestPath.getPath(source, target).getVertexList();

        return vertexList;
    }

    Long 주어진_역_이름을_바탕으로_지하철역을_생성한다(String name) {
        Station station = new Station(name);
        station = stationRepository.save(station);

        return station.getId();
    }

    void 지하철_노선에_구간을_추가한다(Long lineId, Long upStationId, Long downStationId, int distance) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);
        Station 상행역 = stationRepository.findById(upStationId)
                .orElseThrow(NoSuchElementException::new);
        Station 하행역 = stationRepository.findById(downStationId)
                .orElseThrow(NoSuchElementException::new);

        Section 새로운_구간 = new Section(line, 상행역, 하행역, distance);
        line.addSection(새로운_구간);
    }

    Long 지하철_노선을_생성한다(LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        return lineResponse.getId();
    }

    Long 주어진_역들을_바탕으로_구간을_생성한다(Long upStationId, Long downStationId, int distance) {
        Station 상행역 = stationRepository.findById(upStationId)
                .orElseThrow(NoSuchElementException::new);
        Station 하행역 = stationRepository.findById(downStationId)
                .orElseThrow(NoSuchElementException::new);
        return null;
    }


}
