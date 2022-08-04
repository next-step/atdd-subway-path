package nextstep.subway.acceptance;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class PathAcceptanceFixture {

    @Autowired
    private StationService stationService;
    @Autowired
    private LineService lineService;

    Long 칠호선;
    Long 구호선;
    Long 신분당선;
    Long 고속터미널역;
    Long 반포역;
    Long 논현역;
    Long 사평역;
    Long 신논현역;
    int 고속터미널_반포_거리;
    int 반포_논현_거리;
    int 논현_신논현_거리;
    int 고속터미널_신논현_거리;

    public void 지하철_노선이_구성된다() {
        칠호선 = saveLine("7호선", "olive");
        구호선 = saveLine("9호선", "gold");
        신분당선 = saveLine("신분당선", "red");
        고속터미널역 = stationService.saveStation(new StationRequest("고속터미널역")).getId();
        반포역 = stationService.saveStation(new StationRequest("반포역")).getId();
        논현역 = stationService.saveStation(new StationRequest("논현역")).getId();
        사평역 = stationService.saveStation(new StationRequest("사평역")).getId();
        신논현역 = stationService.saveStation(new StationRequest("신논현역")).getId();

        고속터미널_반포_거리 = 2;
        반포_논현_거리 = 2;
        논현_신논현_거리 = 3;
        고속터미널_신논현_거리 = 10;

        lineService.addSection(칠호선, new SectionRequest(고속터미널역, 반포역, 고속터미널_반포_거리));
        lineService.addSection(칠호선, new SectionRequest(반포역, 논현역, 반포_논현_거리));
        lineService.addSection(신분당선, new SectionRequest(논현역, 신논현역, 논현_신논현_거리));
        lineService.addSection(구호선, new SectionRequest(고속터미널역, 신논현역, 고속터미널_신논현_거리));
    }

    private Long saveLine(String name, String olive) {
        return lineService.saveLine(new LineRequest(name, olive)).getId();
    }
}