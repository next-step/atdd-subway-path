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
        Edge edgeA = Edge.builder()
                .lineId(1L)
                .elapsedTime(3)
                .distance(new BigDecimal("1.1"))
                .sourceStationId(1L)
                .targetStationId(2L)
                .build();
        Edge edgeB = Edge.builder()
                .lineId(1L)
                .elapsedTime(3)
                .distance(new BigDecimal("1.3"))
                .sourceStationId(2L)
                .targetStationId(3L)
                .build();

        edgeRepository.save(edgeA);
        edgeRepository.save(edgeB);


        EdgeListResponse edges = edgeService.findByLineId(1L);

        assertThat(edges.getEdges().get(0).getLineId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(0).getSourceStationId()).isEqualTo(1L);
        assertThat(edges.getEdges().get(0).getTargetStationId()).isEqualTo(2L);

    }
}