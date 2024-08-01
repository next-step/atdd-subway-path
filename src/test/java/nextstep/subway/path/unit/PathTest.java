package nextstep.subway.path.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.dto.Path;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    Station 강남역;
    Station 역삼역;
    Section 강남역_역삼역_구간;
    Sections 구간들;
    Line 신분당선;
    double pathWeight = 10.0;
    Path path;
    GraphPath<Long, DefaultWeightedEdge> mockGraphPath;


    @BeforeEach
    public void setup() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");


        강남역_역삼역_구간 = new Section(강남역, 역삼역, 10L);
        구간들 = new Sections(Collections.singletonList(강남역_역삼역_구간));
        신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        path = new Path(List.of(강남역, 역삼역), pathWeight);

    }

    @DisplayName("getVertexList와 getWeight의 정상 동작을 확인한다.")
    @Test
    public void getVertexList_getWeight() {
        // then
        assertAll(
                () -> assertEquals(List.of(강남역, 역삼역), path.getStationList()),
                () -> assertEquals(pathWeight, path.getWeight())
        );
    }

    @DisplayName("[createPathResponse] pathResponse를 생성한다.")
    @Test
    void createPathResponse_success() {
        // when
        var pathResponse = path.createPathResponse();

        // then
        assertAll(
                () -> assertNotNull(pathResponse),
                () -> assertEquals(pathWeight, pathResponse.getDistance()),
                () -> assertEquals(List.of(
                        new StationResponse(강남역.getId(), 강남역.getName()),
                        new StationResponse(역삼역.getId(), 역삼역.getName())
                ), pathResponse.getStationResponseList())
        );
    }

    @DisplayName("[createPathResponse] path의 stationList가 비어 있으면 예외가 발생한다.")
    @Test
    void createPathResponse_fail1() {
        // given
        var path = new Path(List.of(), pathWeight);

        // when & then
        assertThrows(PathException.class, () -> path.createPathResponse())
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

}
