package atdd.station.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        this.line = LINE_1;
    }

    @Test
    public void setLineEdges() {
        List<Edge> legacyEdges = Arrays.asList(EDGE_1, EDGE_2, EDGE_3);
        Edge newEdge = NEW_EDGE_1;
        newEdge.setId(STATION_ID_4);

        line.addNewLineEdges(legacyEdges, newEdge);

        List<Edge> lineEdges = line.getLineEdges();

        assertThat(lineEdges.get(0).getSourceStationId()).isEqualTo(STATION_ID_1);
        assertThat(lineEdges.get(0).getTargetStationId()).isEqualTo(STATION_ID_2);

        assertThat(lineEdges.get(1).getSourceStationId()).isEqualTo(STATION_ID_2);
        assertThat(lineEdges.get(1).getTargetStationId()).isEqualTo(STATION_ID_3);

        assertThat(lineEdges.get(2).getSourceStationId()).isEqualTo(STATION_ID_3);
        assertThat(lineEdges.get(2).getTargetStationId()).isEqualTo(STATION_ID_4);

        assertThat(lineEdges.get(3).getSourceStationId()).isEqualTo(STATION_ID_4);
        assertThat(lineEdges.get(3).getTargetStationId()).isEqualTo(STATION_ID_5);
    }
}
