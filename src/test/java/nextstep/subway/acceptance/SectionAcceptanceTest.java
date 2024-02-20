package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import nextstep.subway.fixture.AcceptanceTest;
import nextstep.subway.fixture.LineSteps;
import nextstep.subway.fixture.SectionSteps;
import nextstep.subway.fixture.StationSteps;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.persistance.LineRepository;
import nextstep.subway.line.presentation.LineResponse;
import nextstep.subway.line.presentation.SectionRequest;
import nextstep.subway.station.presentation.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
@Transactional
public class SectionAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private LineRepository lineRepository;
    private StationResponse 구의역;
    private StationResponse 건대입구역;
    private StationResponse 강변역;
    private LineResponse 이호선;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        강변역 = StationSteps.createStation("강변역");
        건대입구역 = StationSteps.createStation("건대입구역");
        구의역 = StationSteps.createStation("구의역");
        이호선 = LineSteps.이호선_생성(건대입구역.getId(), 강변역.getId());
    }

    @Test
    @DisplayName("노선에 역 추가시 노선 가운데 추가 할 수 있다")
    public void addSectionInMiddle() {

        // when
        SectionSteps.라인에_구간을_생성한다(이호선.getId(), new SectionRequest(건대입구역.getId(), 구의역.getId(), 7));

        // then
        LineResponse lineResponse = LineSteps.노선을_조회한다(이호선.getId());
        노선이_가진_역을_검증한다(lineResponse.getStations(), List.of(건대입구역, 구의역, 강변역));
    }

    private void 노선이_가진_역을_검증한다(List<StationResponse> actual, List<StationResponse> expect) {
        assertThat(actual).isEqualTo(expect);
    }


    @Test
    @DisplayName("노선에 역 추가시 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이다")
    public void addSectionDistanceTest() {

        int distance = 10;
        LineResponse 이호선 = LineSteps.이호선_생성(건대입구역.getId(), 강변역.getId(), distance);

        // when
        SectionSteps.라인에_구간을_생성한다(이호선.getId(), new SectionRequest(건대입구역.getId(), 구의역.getId(), 7));

        // then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertThat(line.getDistance()).isEqualTo(distance);

    }


    @Test
    @DisplayName("노선에 역 추가시 기존 구간과 신규 구간의 모두 같을 수 없다")
    public void addSectionInMiddleFail() {
        // when
        Assertions.assertThatThrownBy(
            () -> SectionSteps.라인에_구간을_생성한다(이호선.getId(), new SectionRequest(건대입구역.getId(), 강변역.getId(), 20))
        );
    }

}
