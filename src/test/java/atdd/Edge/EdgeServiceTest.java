package atdd.Edge;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EdgeServiceTest {

    @Autowired
    EdgeService edgeService;
    @Autowired
    EdgeRepository edgeRepository;

    @AfterEach
    public void cleanUp(){
        edgeRepository.deleteAll();
    }

    @DisplayName("노선 Id로 구간을 찾는다.")
    @Test
    void findByLineId() {

        createEdge(1L, 1L, 2L);
        createEdge(1L, 2L, 3L);


        EdgeListResponse edges = edgeService.findByLineId(1L);

        assertThat(edges.getEdges().get(0).getLineId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(0).getSourceStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(0).getTargetStationId()).isEqualTo(2L);

    }

    @DisplayName("지하철 역 Id로 구간을 찾는다.")
    @Test
    public void findEdgeByStationId(){
        createEdge(1L, 1L, 2L);
        createEdge(1L, 2L, 3L);
        createEdge(2L, 1L, 4L);

        EdgeListResponse edges = edgeService.findEdgeByStationId(1L);

        assertThat(edges.getEdges().size()).isEqualTo(2);

    }

    @DisplayName("중복되는 create 코드")
    public void createEdge(Long lineId, Long sourceStationId, Long targetStationID){
        Edge edgeA = Edge.builder()
                .lineId(lineId)
                .elapsedTime(3)
                .distance(new BigDecimal("1.1"))
                .sourceStationId(sourceStationId)
                .targetStationId(targetStationID)
                .build();

        edgeRepository.save(edgeA);

    }
}