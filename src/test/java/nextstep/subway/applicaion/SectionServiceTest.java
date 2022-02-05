package nextstep.subway.applicaion;

import nextstep.fake.LineFakeRepository;
import nextstep.fake.StationFakeRepository;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.LineStepUtil.기존노선;
import static nextstep.subway.utils.LineStepUtil.기존색상;
import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class SectionServiceTest {

    LineRepository lineFakeRepository = new LineFakeRepository();

    StationFakeRepository stationFakeRepository = new StationFakeRepository();

    SectionService sectionService;
    LineService lineService;

    Station 기존_지하철;
    Station 새로운_지하철;
    LineResponse 라인_등록_응답;

    @BeforeEach
    void setUp() {
        기존_지하철 = stationFakeRepository.save(new Station(기존지하철));
        새로운_지하철 = stationFakeRepository.save(new Station(새로운지하철));

        sectionService = new SectionService(lineFakeRepository, stationFakeRepository);
        lineService = new LineService(lineFakeRepository, stationFakeRepository);

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
        Station 처음_보는_지하철_역 = stationFakeRepository.save(new Station("처음_보는_지하철_역"));
        SectionRequest 구간_등록_파라미터 = new SectionRequest(기존_지하철.getId(), 처음_보는_지하철_역.getId(), 1);

        //when
        Section section = sectionService.createSection(라인_등록_응답.getId(), 구간_등록_파라미터);

        //then
        assertThat(section.getUpStation().getId()).isEqualTo(기존_지하철.getId());
    }

    /**
     * Given 노선에 구간을 2개 등록한다.
     * When  구간에 등록된 역을 하나 삭제한다
     * Then  구간이 삭제된다
     */
    @DisplayName("구간에 등록된 역을 삭제한다")
    @Test
    void 구간_삭제() {
        //given
        Station 처음_보는_지하철_역_1 = stationFakeRepository.save(new Station("처음보는지하철1"));
        Station 처음_보는_지하철_역_2 = stationFakeRepository.save(new Station("처음보는지하철2"));
        SectionRequest 구간_등록_파라미터 = new SectionRequest(기존_지하철.getId(), 처음_보는_지하철_역_1.getId(), 역간_거리 / 2);
        SectionRequest 구간_등록_파라미터2 = new SectionRequest(처음_보는_지하철_역_1.getId(), 처음_보는_지하철_역_2.getId(), 역간_거리 / 4);
        Section 구간_등록_응답_1 = sectionService.createSection(라인_등록_응답.getId(), 구간_등록_파라미터);
        Section 구간_등록_응답_2 = sectionService.createSection(라인_등록_응답.getId(), 구간_등록_파라미터2);

        //when
        sectionService.deleteSection(라인_등록_응답.getId(), 처음_보는_지하철_역_1.getId());

        //then
        assertThat(lineService.findById(라인_등록_응답.getId()).getStations()).hasSize(2);
    }
}
