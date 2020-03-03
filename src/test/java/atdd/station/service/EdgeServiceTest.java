package atdd.station.service;

import atdd.station.model.entity.Edge;
import atdd.station.repository.EdgeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static atdd.TestConstant.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = EdgeService.class)
public class EdgeServiceTest {
    @Autowired
    private EdgeService edgeService;

    @MockBean
    private EdgeRepository edgeRepository;

    @Test
    public void createEdge() {
        List<Edge> legacyEdges = Arrays.asList(EDGE_1, EDGE_2, EDGE_3);

        Edge newEdge = NEW_EDGE_1;
        newEdge.setId(4l);

        given(edgeRepository.save(newEdge)).willReturn(newEdge);

        edgeService.createEdge(legacyEdges, newEdge);
    }
}
