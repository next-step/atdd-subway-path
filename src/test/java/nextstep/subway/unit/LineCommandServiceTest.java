package nextstep.subway.unit;

import nextstep.subway.applicaion.LineCommandService;
import nextstep.subway.applicaion.LineQueryService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRegistrationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.NoLastStationException;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.GivenUtils.FIRST_ID;
import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.FOURTH_ID;
import static nextstep.subway.utils.GivenUtils.GREEN;
import static nextstep.subway.utils.GivenUtils.TEN;
import static nextstep.subway.utils.GivenUtils.THIRD_ID;
import static nextstep.subway.utils.GivenUtils.YELLOW;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.강남역_이름;
import static nextstep.subway.utils.GivenUtils.분당선_이름;
import static nextstep.subway.utils.GivenUtils.분당선으로_수정_요청;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.역삼_선릉_구간_요청;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.역삼역_이름;
import static nextstep.subway.utils.GivenUtils.이호선;
import static nextstep.subway.utils.GivenUtils.이호선_객체_생성_요청;
import static nextstep.subway.utils.GivenUtils.이호선_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class LineCommandServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineQueryService lineQueryService;

    @Autowired
    private LineCommandService lineCommandService;

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    @DirtiesContext
    void createLine() {
        // given
        int expectedSize = 2;
        saveAllStations(강남역(), 역삼역());

        // when
        LineResponse lineResponse = lineCommandService.saveLine(이호선_객체_생성_요청());

        // then
        assertThat(lineResponse.getId()).isEqualTo(FIRST_ID);
        assertThat(lineResponse.getName()).isEqualTo(이호선_이름);
        assertThat(lineResponse.getColor()).isEqualTo(GREEN);
        assertThat(getStationNames(lineResponse)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("존재하지 않는 역의 id로 지하철 노선을 생성한다.")
    @DirtiesContext
    void createLineByInvalidStationIds() {
        // given

        // when
        Executable executable = () -> lineCommandService.saveLine(이호선_객체_생성_요청());

        // then
        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    @DirtiesContext
    void modifyLine() {
        // given
        Line 이호선 = 이호선();
        saveAllStations(강남역(), 역삼역());
        lineRepository.save(이호선);

        // when
        lineCommandService.modifyLine(이호선.getId(), 분당선으로_수정_요청());

        // then
        Line line = lineQueryService.findById(이호선.getId());
        assertThat(line.getName()).isEqualTo(분당선_이름);
        assertThat(line.getColor()).isEqualTo(YELLOW);
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @DirtiesContext
    void modifyNoneExistentLine() {
        // given
        Line 이호선 = 이호선();

        // when
        Executable executable = () -> lineCommandService.modifyLine(이호선.getId(), 분당선으로_수정_요청());

        // then
        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선을 제거한다.")
    @DirtiesContext
    void deleteLine() {
        // given
        Line 이호선 = 이호선();
        saveAllStations(강남역(), 역삼역());
        lineRepository.save(이호선);

        // when
        lineCommandService.deleteLineById(이호선.getId());

        // then
        assertThat(lineQueryService.findAllLines()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @DirtiesContext
    void deleteNonExistentLine() {
        // given

        // when
        Executable executable = () -> lineCommandService.deleteLineById(FIRST_ID);

        // then
        assertThrows(EmptyResultDataAccessException.class, executable);
    }

    @Test
    @DisplayName("노선에 구간을 추가한다")
    @DirtiesContext
    void addSection() {
        // given
        int expectedSize = 3;
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        이호선.addSection(강남역, 역삼역, TEN);
        saveAllStations(강남역, 역삼역, 양재역(), 선릉역);
        lineRepository.save(이호선);

        // when
        lineCommandService.addSection(이호선.getId(), 역삼_선릉_구간_요청());

        // then
        Line line = lineQueryService.findById(이호선.getId());
        assertThat(line.getStations()).hasSize(expectedSize)
                .containsExactly(강남역, 역삼역, 선릉역);
    }

    @Test
    @DisplayName("지하철 노선에 구간 추가 실패 - 노선의 상행, 하행 종점과 무관한 역 추가")
    @DirtiesContext
    void addSectionWithInvalidStation() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        이호선.addSection(강남역, 역삼역, TEN);
        saveAllStations(강남역, 역삼역, 양재역(), 선릉역());
        lineRepository.save(이호선);
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                THIRD_ID,
                FOURTH_ID,
                TEN
        );

        // when
        Executable executable = () -> lineCommandService.addSection(이호선.getId(), sectionRequest);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선에 구간 추가 실패 - 노선에 이미 존재하는 하행역 구간 추가")
    @DirtiesContext
    void addSectionWithDuplicatedStation() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        이호선.addSection(강남역, 역삼역, TEN);
        saveAllStations(강남역, 역삼역);
        lineRepository.save(이호선);
        SectionRegistrationRequest sectionRequest = new SectionRegistrationRequest(
                역삼역.getId(),
                강남역.getId(),
                TEN
        );

        // when
        Executable executable = () -> lineCommandService.addSection(이호선.getId(), sectionRequest);


        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("지하철 노선에 구간을 제거")
    @DirtiesContext
    void removeSection() {
        // given
        int expectedSize = 2;
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        이호선.addSection(강남역, 역삼역, TEN);
        이호선.addSection(역삼역, 선릉역, FIVE);
        saveAllStations(강남역, 역삼역, 양재역(), 선릉역);
        lineRepository.save(이호선);

        // when
        lineCommandService.removeSection(이호선.getId(), 선릉역.getId());

        // then
        LineResponse line = lineQueryService.findLine(이호선.getId());
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("지하철 노선에 구간 제거 실패 - 하행 종점역이 아닌 다른 역 제거")
    @DirtiesContext
    void removeSectionWithInvalidLastStation() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        이호선.addSection(강남역, 역삼역, TEN);
        이호선.addSection(역삼역, 선릉역, FIVE);
        saveAllStations(강남역, 역삼역, 양재역(), 선릉역);
        lineRepository.save(이호선);

        // when
        Executable executable = () -> lineCommandService.removeSection(이호선.getId(), 역삼역.getId());


        // then
        assertThrows(NoLastStationException.class, executable);
    }

    @DisplayName("지하철 노선에 구간 제거 실패 - 구간이 1개인 경우")
    @Test
    @DirtiesContext
    void removeSingleSection() {
        // given
        Line 이호선 = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        이호선.addSection(강남역, 역삼역, TEN);
        saveAllStations(강남역, 역삼역);
        lineRepository.save(이호선);

        // when
        Executable executable = () -> lineCommandService.removeSection(이호선.getId(), 역삼역.getId());

        // then
        assertThrows(SectionRemovalException.class, executable);
    }
    private List<String> getStationNames(LineResponse lineResponse) {
        return lineResponse.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }

    private void saveAllStations(Station... stations) {
        stationRepository.saveAll(List.of(stations));
    }

}
