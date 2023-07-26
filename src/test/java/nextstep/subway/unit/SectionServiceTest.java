package nextstep.subway.unit;

import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class SectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionService sectionService;

    private Station 당고개역, 이수역, 사당역;
    private Line line;
    private SectionDto sectionDto;

    @BeforeEach
    void setUpStation() {
        당고개역 = stationRepository.save(당고개역());
        이수역 = stationRepository.save(이수역());
        사당역 = stationRepository.save(사당역());
        line = lineRepository.save(line(당고개역, 이수역));
    }

    @DisplayName("구간을 추가한다. - 역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection() {
        // given
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), 3);

        // when
        sectionService.addSection(line.getId(), sectionDto);

        // then
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "사당역", "이수역"
                );
    }

    @DisplayName("구간을 추가한다. - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection2() {
        // given
        sectionDto = sectionDto(사당역.getId(), 당고개역.getId(), 3);

<<<<<<< HEAD
        // when
        sectionService.addSection(line.getId(), sectionDto);

        // then
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "사당역", "당고개역", "이수역"
                );
    }

    @DisplayName("구간을 추가한다. - 새로운역을 하행 종점으로 등록할 경우")
    @Test
    void addSection3() {
        // given
        sectionDto = sectionDto(이수역.getId(), 사당역.getId(), 3);

=======
>>>>>>> 46c7249c (충돌해결)
        // when
        sectionService.addSection(line.getId(), sectionDto);

        // then
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
<<<<<<< HEAD
                        "당고개역", "이수역", "사당역"
                );
    }

=======
                        "사당역", "당고개역", "이수역"
                );
    }

    @DisplayName("구간을 추가한다. - 새로운역을 하행 종점으로 등록할 경우")
    @Test
    void addSection3() {
        // given
        sectionDto = sectionDto(이수역.getId(), 사당역.getId(), 3);

        // when
        sectionService.addSection(line.getId(), sectionDto);

        // then
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "이수역", "사당역"
                );
    }

>>>>>>> 46c7249c (충돌해결)
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같을때 예외 발생")
    void addSectionThrowExceptionIsINVALID_DISTANCE(int distance) {
        // given
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), distance);

        // when then
        assertThatThrownBy(() -> sectionService.addSection(line.getId(), sectionDto))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    @DisplayName("새로운 구간을 등록할 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionThrowExceptionIsALREADY_SECTION() {
        // given
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), 3);
        sectionService.addSection(line.getId(), sectionDto);

        // when then
        assertThatThrownBy(() -> sectionService.addSection(line.getId(), sectionDto))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining(ErrorCode.ALREADY_SECTION.getMessage());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionThrowExceptionIsCAN_NOT_BE_ADDED_SECTION() {
        // given
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), 3);
        sectionService.addSection(line.getId(), sectionDto);
        Station 동작역 = stationRepository.save(동작역());
        Station 이촌역 = stationRepository.save(이촌역());
        sectionDto = sectionDto(동작역.getId(), 이촌역.getId(), 3);

        // when  then
        assertThatThrownBy(() -> sectionService.addSection(line.getId(), sectionDto))
                .isInstanceOf(SubwayException.class)
                .hasMessageContaining(ErrorCode.CAN_NOT_BE_ADDED_SECTION.getMessage());
    }

<<<<<<< HEAD
    @DisplayName("구간 제거 - 첫번째 역을 제거 했을 경우")
=======
    @DisplayName("구간을 제거한다.")
>>>>>>> 46c7249c (충돌해결)
    @Test
    void removeSection() {
        // given : 선행조건 기술
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), 3);
<<<<<<< HEAD
        sectionService.addSection(line.getId(), sectionDto);

        // when : 기능 수행
        sectionService.removeSection(line.getId(), 당고개역.getId());

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("사당역", "이수역"));
    }

    @DisplayName("구간 제거 - 중간 역을 제거 했을 경우")
    @Test
    void removeSection2() {
        // given : 선행조건 기술
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), 3);
=======
>>>>>>> 46c7249c (충돌해결)
        sectionService.addSection(line.getId(), sectionDto);

        // when : 기능 수행
        sectionService.removeSection(line.getId(), 이수역.getId());

        // then : 결과 확인
<<<<<<< HEAD
        역_목록_검증(line, line.getStations().size(), Arrays.asList("당고개역", "이수역"));
    }

    @DisplayName("구간 제거 - 마지막 역을 제거 했을 경우")
    @Test
    void removeSection3() {
        // given : 선행조건 기술
        sectionDto = sectionDto(당고개역.getId(), 사당역.getId(), 3);
        sectionService.addSection(line.getId(), sectionDto);

        // when : 기능 수행
        sectionService.removeSection(line.getId(), 이수역.getId());

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("당고개역", "사당역"));
    }

    private void 역_목록_검증(Line line, int size, List<String> names) {
        assertThat(line.getStations()).hasSize(size)
                .extracting("name")
                .containsExactly(
                        names.toArray()
=======
        assertThat(line.getStations()).hasSize(2)
                .extracting("name")
                .containsExactly(
                        "당고개역", "사당역"
>>>>>>> 46c7249c (충돌해결)
                );
    }

    private Line line(Station upStation, Station downStation) {
        return Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .section(section(upStation, downStation))
                .build();
    }

    private Station 당고개역() {
        return new Station("당고개역");
    }

    private Station 이수역() {
        return new Station("이수역");
    }

    private Station 사당역() {
        return new Station("사당역");
    }

    private Station 동작역() {
        return new Station("동작역");
    }

    private Station 이촌역() {
        return new Station("이촌역");
    }

    private Section section(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .build();
    }

    private SectionDto sectionDto(Long upStationId, Long downStationId, int distance) {
        return SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }
}
