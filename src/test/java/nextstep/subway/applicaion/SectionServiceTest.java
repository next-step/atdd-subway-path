package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static nextstep.subway.utils.LineStepUtil.기존노선;
import static nextstep.subway.utils.LineStepUtil.기존색상;
import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionServiceTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    SectionService sectionService;

    Station 기존_지하철;
    Station 새로운_지하철;
    LineResponse 라인_등록_응답;

    @BeforeEach
    void setUp() {
        기존_지하철 = stationRepository.save(new Station(기존지하철));
        새로운_지하철 = stationRepository.save(new Station(새로운지하철));

        sectionService = new SectionService(lineRepository, stationRepository);
        LineService lineService = new LineService(lineRepository, stationRepository);

        LineRequest 라인_등록_파라미터 = LineRequest.of(기존노선, 기존색상,기존_지하철,새로운_지하철, 역간_거리);

        라인_등록_응답 = lineService.saveLine(라인_등록_파라미터);
    }

    /**
     * Given 구간을 생성한다.
     * When  구간을 저장한다.
     * Then  구간이 저장된다.
     */
    @DisplayName("구간을 등록한다.")
    @Test
    void 구간_등록() {
        //given
        Station 처음_보는_지하철_1 = stationRepository.save(new Station("처음보는지하철1"));
        SectionRequest 구간_등록_파라미터 = new SectionRequest(기존_지하철.getId(),처음_보는_지하철_1.getId(),1);

        //when
        Section section = sectionService.createSection(라인_등록_응답.getId(), 구간_등록_파라미터);

        //then
        assertThat(section.getUpStation().getId()).isEqualTo(기존_지하철.getId());
    }
}
