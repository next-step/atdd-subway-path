package atdd.station.service;

import atdd.station.model.entity.Edge;
import atdd.station.repository.LineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static atdd.TestConstant.*;
import static org.mockito.ArgumentMatchers.any;
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
}
