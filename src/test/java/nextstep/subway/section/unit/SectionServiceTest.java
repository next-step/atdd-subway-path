package nextstep.subway.section.unit;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.exception.SectionException;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.util.LineStep.지하철_노선_생성;
import static nextstep.subway.util.StationStep.지하철_역_등록;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SectionServiceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    SectionService sectionService;

    private StationResponse 강남역;
    private StationResponse 선릉역;
    private StationResponse 삼성역;
    private StationResponse 언주역;
    private StationResponse 논현역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setup() {

        databaseCleanup.execute();

        강남역 = 지하철_역_등록("강남역");
        선릉역 = 지하철_역_등록("선릉역");
        삼성역 = 지하철_역_등록("삼성역");
        언주역 = 지하철_역_등록("언주역");
        논현역 = 지하철_역_등록("논현역");

        신분당선 = 지하철_노선_생성("신분당선", "Red", 강남역.getId(), 선릉역.getId(), 10L);
    }

    @Test
    @DisplayName("새로운 구간을 첫번째 구간에 생성한다.")
    @Transactional
    @Rollback
    public void createSection_first() {
        // when
        var 삼성역_강남역_구간_생성_요청 = SectionRequest.of(삼성역.getId(), 강남역.getId(), 5L);
        var 삼성역_강남역_구간_생성_응답 = sectionService.createSection(신분당선.getId(), 삼성역_강남역_구간_생성_요청);

        // then
        Assertions.assertEquals(삼성역_강남역_구간_생성_응답.getLineId(), 신분당선.getId());
        Assertions.assertEquals(삼성역_강남역_구간_생성_응답.getUpStationResponse().getId(), 삼성역.getId());
        Assertions.assertEquals(삼성역_강남역_구간_생성_응답.getUpStationResponse().getName(), 삼성역.getName());
        Assertions.assertEquals(삼성역_강남역_구간_생성_응답.getDownStationResponse().getId(), 강남역.getId());
        Assertions.assertEquals(삼성역_강남역_구간_생성_응답.getDownStationResponse().getName(), 강남역.getName());
    }

    @Test
    @DisplayName("새로운 구간을 중간 구간에 생성한다.")
    public void createSection_middle() {
        // given
        sectionService.createSection(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 5L));

        // when
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        var 선릉역_언주역_구간_생성_응답 = sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // then
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getLineId(), 신분당선.getId());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getId(), 선릉역.getId());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getName(), 선릉역.getName());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getId(), 언주역.getId());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getName(), 언주역.getName());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getDistance(), 1L);
    }

    @Test
    @DisplayName("새로운 구간을 마지막 구간에 생성한다.")
    public void createSection_last() {
        // given
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when
        var 선릉역_언주역_구간_생성_응답 = sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // then
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getLineId(), 신분당선.getId());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getId(), 선릉역.getId());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getUpStationResponse().getName(), 선릉역.getName());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getId(), 언주역.getId());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getDownStationResponse().getName(), 언주역.getName());
        Assertions.assertEquals(선릉역_언주역_구간_생성_응답.getDistance(), 1L);
    }

    @Test
    @DisplayName("새로운 구간의 lineId를 찾을 수 없다.")
    public void createSection_fail_lineId_cannot_found() {
        // given
        var 언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(LineNotFoundException.class, () -> sectionService.createSection(2L, 언주역_구간_생성_요청))
                .getMessage().equals("노선을 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("새로운 구간의 upStation을 찾을 수 없다.")
    public void createSection_fail_upStation_cannot_found() {
        // given
        var 언주역_구간_생성_요청 = SectionRequest.of(10L, 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 언주역_구간_생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 downStation을 찾을 수 없다.")
    public void createSection_fail_downStation_cannot_found() {
        // given
        var 언주역_구간_생성_요청 = SectionRequest.of(언주역.getId(), 10L, 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 언주역_구간_생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("상행 종점역을 가진 첫 번째 구간을 삭제한다.")
    public void delete_section_first_section() {
        // given
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        sectionService.deleteSection(신분당선.getId(), 강남역.getId());
    }

    @Test
    @DisplayName("중간 구간을 삭제한다.")
    public void delete_section_middle_section() {
        // given
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);
        var 언주역_논현역_구간_생성_요청 = SectionRequest.of(언주역.getId(), 논현역.getId(), 2L);
        sectionService.createSection(신분당선.getId(), 언주역_논현역_구간_생성_요청);


        // when & then
        sectionService.deleteSection(신분당선.getId(), 선릉역.getId());
    }

    @Test
    @DisplayName("하행 종점역을 가진 마지막 구간을 삭제한다.")
    public void delete_section_last_section() {
        // given
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        sectionService.deleteSection(신분당선.getId(), 언주역.getId());
    }

    @Test
    @DisplayName("구간에 존재하지 않는 역의 삭제 요청은 실패한다.")
    public void delete_section_fail1() {
        // given
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> sectionService.deleteSection(신분당선.getId(), 논현역.getId()))
                .getMessage().equals("구간을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("삭제 구간의 대상이 되는 노선의 lineId를 찾을 수 없다.")
    public void delete_section_fail2() {
        // given
        var 선릉역_언주역_구간_생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        sectionService.createSection(신분당선.getId(), 선릉역_언주역_구간_생성_요청);

        // when & then
        Assertions.assertThrows(LineNotFoundException.class, () -> sectionService.deleteSection(2L, 언주역.getId()))
                .getMessage().equals("노선을 찾을 수 없습니다.");
    }
}
