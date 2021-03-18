package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 광교역 = new Station("광교역");
        Station 양재역 = new Station("양재역");
        Station 청계산입구 = new Station("청계산입구");

        stationRepository.save(강남역);
        stationRepository.save(판교역);
        stationRepository.save(광교역);
        stationRepository.save(양재역);
        stationRepository.save(청계산입구);

        LineRequest request = new LineRequest("신분당선", "red-001", 강남역.getId(), 판교역.getId(), 10);
        LineResponse 신분당선 = lineService.saveLine(request);

        // when
        // lineService.addSection [신분당선]강남역-10-판교역-8-광교역
        SectionRequest sectionRequest = SectionRequest.of(판교역.getId(), 광교역.getId(), 8);
        lineService.addSection(신분당선.getId(), sectionRequest);

        // addFirstSection [신분당선]양재역-7-강남역-10-판교역-8-광교역
        sectionRequest = SectionRequest.of(양재역.getId(), 강남역.getId(), 7);
        lineService.addSection(신분당선.getId(), sectionRequest);

        // add(Middle)Section [신분당선]양재역-7-'강남역-4-청계산입구-6-판교역'-8-광교역
        sectionRequest = SectionRequest.of(강남역.getId(), 청계산입구.getId(),4);
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineService.findLineById(신분당선.getId());
        assertThat(line.getSections()).hasSize(4);
    }
}
