package atdd.station.service;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import atdd.station.repository.LineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static atdd.TestConstant.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@SpringBootTest(classes = LineService.class)
public class LineServiceTest {
    @Autowired
    private LineService lineService;

    @MockBean
    private LineRepository lineRepository;

    @MockBean
    private EdgeService edgeService;

    @MockBean
    private StationService stationService;

    @Test
    public void addEdge() {
        List<Edge> legacyEdges = Arrays.asList(EDGE_1, EDGE_2, EDGE_3);
        Edge newEdge = NEW_EDGE_1;
        newEdge.setId(4l);

        given(lineRepository.findById(any())).willReturn(Optional.of(LINE_1));
        given(edgeService.findAllById(any())).willReturn(legacyEdges);
        given(edgeService.createEdge(legacyEdges, NEW_EDGE_1)).willReturn(NEW_EDGE_1);
        given(stationService.updateLine(new HashSet<>(), 1l)).willReturn(new ArrayList<>());
        given(lineRepository.save(any())).willReturn(any());

        lineService.addEdge(1l, newEdge);
    }

    @Test
    public void deleteEdge() {
        Station station = STATION_1;
        station.setId(1l);
        station.setLineIds(new ArrayList<>(Arrays.asList(1l)));

        Edge edge1 = EDGE_50;
        Edge edge2 = EDGE_51;
        Edge edge3 = EDGE_52;
        Edge edge4 = EDGE_53;

        edge1.setId(1l);
        edge2.setId(2l);
        edge3.setId(3l);
        edge4.setId(4l);

        Line line = LINE_1;
        line.setId(1l);

        given(lineRepository.findById(anyLong())).willReturn(Optional.of(LINE_1));
        given(stationService.findById(anyLong())).willReturn(station);
        given(lineRepository.save(any())).willReturn(LINE_1);
        given(edgeService.findAllById(anyList())).willReturn(new ArrayList<>(Arrays.asList(edge1, edge2, edge3, edge4)));
        given(edgeService.save(any())).willReturn(NEW_EDGE);
        given(stationService.save(any())).willReturn(any());

        lineService.deleteEdge(line.getId(), station.getId());
    }
}
