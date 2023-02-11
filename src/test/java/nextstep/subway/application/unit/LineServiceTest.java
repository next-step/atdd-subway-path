package nextstep.subway.application.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.SubwayFixture.노선_생성;
import static nextstep.subway.SubwayFixture.역_생성;
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
        Station 수원역 = stationRepository.save(역_생성("수원역"));
        Station 매탄권선역 = stationRepository.save(역_생성("매탄권선역"));
        Line 분당선 = lineRepository.save(노선_생성("분당선", "red"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 매탄권선역.getId(), 10);
        lineService.addSection(분당선.getId(), sectionRequest);

        // then
        Line findLine = lineService.findById(분당선.getId());
        assertThat(findLine.getSections()).hasSize(1)
                .flatExtracting(Section::getStations).hasSize(2)
                .containsAnyElementsOf(findLine.getStations())
                .extracting(Station::getName)
                .containsExactlyInAnyOrder("수원역", "매탄권선역");
    }
}
