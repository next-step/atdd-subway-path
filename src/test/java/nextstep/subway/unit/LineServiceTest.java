package nextstep.subway.unit;

import nextstep.subway.line.*;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
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

    /**
     * Given: (상행종점역 - 하행종점역) : (방화역 - 강동역)을 생성하고
     * When : (강동역 - 마천역) 구간을 마지막에 추가하면
     * Then : 하행종점역은 마천역이 되고 노선의 길이는 8이 되어야 한다. (최종: 방화역 - 강동역 - 마천역)
     */
    @Test
    void addLastSection() {
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
        assertThat(line.getDistance()).isEqualTo(8);
    }

    /**
     * Given: (상행종점역 - 하행종점역) : (방화역 - 강동역)을 생성하고
     * When : (방화역 - 마천역) 구간을 중간에 추가하면
     * Then : 하행종점역은 강동역이고, 노선의 길이는 그대로 5가 되고, 마지막 구간(마천역 - 강동역)의 길이는 2가 되어야 한다.
     * (최종: 방화역 - 마천역 - 강동역)
     */
    @Test
    void insertSection() {
        // given
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
        lineService.addSection(line5.getId(), new SectionAddRequest(
                banghwa.getId(),
                macheon.getId(),
                3
        ));

        // then
        Line line = lineRepository.findById(line5.getId()).orElseThrow();
        assertThat(line.getSections().getLastDownstation()).isEqualTo(gangdong);
        assertThat(line.getDistance()).isEqualTo(5);
        assertThat(line.getSections().getLastSectionDistance()).isEqualTo(2);
    }

    /**
     * Given: (방화역 - 강동역 - 마천역)을 생성하고
     * When : 방화역을 삭제하면
     * Then : 상행종점역은 강동역이 되고, 노선의 길이는 3이 되어야 한다.
     */
    @Test
    void removeFirstSection() {
        // given
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
        lineService.addSection(line5.getId(), new SectionAddRequest(
                gangdong.getId(),
                macheon.getId(),
                3
        ));

        // when
        lineService.deleteSection(line5.getId(), banghwa.getId());

        // then
        Line line = lineRepository.findById(line5.getId()).orElseThrow();
        assertThat(line.getSections().getFirstUpstation()).isEqualTo(gangdong);
        assertThat(line.getDistance()).isEqualTo(3);
    }

    /**
     * Given: (방화역 - 강동역 - 마천역)을 생성하고
     * When : 강동역을 삭제하면
     * Then : 하행종점역은 마천역, 노선의 길이는 8이 되어야 한다.
     * (최종: 방화역 - 마천역)
     */
    @Test
    void removeSection() {
        // given
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
        lineService.addSection(line5.getId(), new SectionAddRequest(
                gangdong.getId(),
                macheon.getId(),
                3
        ));

        // when
        lineService.deleteSection(line5.getId(), gangdong.getId());

        // then
        Line line = lineRepository.findById(line5.getId()).orElseThrow();
        assertThat(line.getSections().getFirstUpstation()).isEqualTo(banghwa);
        assertThat(line.getSections().getLastDownstation()).isEqualTo(macheon);
        assertThat(line.getDistance()).isEqualTo(8);
    }

    /**
     * Given: (방화역 - 강동역 - 마천역)을 생성하고
     * When : 마천역을 삭제하면
     * Then : 하행종점역은 강동역, 노선의 길이는 5가 되어야 한다.
     * (최종: 방화역 - 강동역)
     */
    @Test
    void removeLastSection() {
        // given
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
        lineService.addSection(line5.getId(), new SectionAddRequest(
                gangdong.getId(),
                macheon.getId(),
                3
        ));

        // when
        lineService.deleteSection(line5.getId(), macheon.getId());

        // then
        Line line = lineRepository.findById(line5.getId()).orElseThrow();
        assertThat(line.getSections().getLastDownstation()).isEqualTo(gangdong);
        assertThat(line.getDistance()).isEqualTo(5);
    }
}
