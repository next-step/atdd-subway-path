package nextstep.subway.unit;

import nextstep.subway.line.*;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
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
        Station banghwa = stationRepository.save(new Station("방화역"));
        Station gangdong = stationRepository.save(new Station("강동역"));
        Station macheon = stationRepository.save(new Station("마천역"));
        LineResponse line5 = lineService.create(new LineCreateRequest(
                "5호선",
                "purple",
                5,
                banghwa.getId(),
                gangdong.getId()
        ));

        // when
        // lineService.addSection 호출
        lineService.addSection(line5.getId(), new SectionAddRequest(
                gangdong.getId(),
                macheon.getId(),
                3
        ));

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineRepository.findById(line5.getId()).orElseThrow();
        assertThat(line.getSections().getLastDownstation()).isEqualTo(macheon);
    }
}
