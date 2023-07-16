package subway.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.SubwayNotFoundException;
import subway.line.dto.LineModifyRequest;
import subway.line.dto.SectionCreateRequest;
import subway.line.dto.SectionDeleteRequest;
import subway.line.model.Line;
import subway.line.repository.LineRepository;
import subway.line.service.LineService;
import subway.station.model.Station;
import subway.station.repository.StationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("LineService 단위 테스트 (spring integration test)")
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
     * Given 노선이 있을 때
     * When 구간을 추가하면
     * Then 구간을 조회 할 수 있다.
     */
    @DisplayName("노선에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        Station 강남역 = stationRepository.save(new Station(1L, "강남역"));
        Station 역삼역 = stationRepository.save(new Station(2L, "역삼역"));
        Line 이호선 = lineRepository.save(Line.builder().name("2호선").color("bg-green-600").build());
        final long distance = 10;

        // when
        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .distance(distance)
                .upStationId(강남역.getId())
                .downStationId(역삼역.getId())
                .build();
        lineService.appendSection(이호선.getId(), 구간_요청);

        // then
        assertThat(이호선.getLineSections().getSectionsCount()).isEqualTo(1);
    }


    /**
     * Given 2개의 구간을 가진 노선이 있을때
     * When 노선을 1개 삭제하면
     * Then 1개의 구간을 가진 노선이 된다.
     */
    @DisplayName("노선의 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        Station 강남역 = stationRepository.save(new Station(1L, "강남역"));
        Station 역삼역 = stationRepository.save(new Station(2L, "역삼역"));
        Station 선릉역 = stationRepository.save(new Station(3L, "선릉역"));
        Line 이호선 = lineRepository.save(Line.builder().name("2호선").color("bg-green-600").build());
        final long distance = 10;

        SectionCreateRequest 첫번째구간_요청 = SectionCreateRequest.builder()
                .distance(distance)
                .upStationId(강남역.getId())
                .downStationId(역삼역.getId())
                .build();
        lineService.appendSection(이호선.getId(), 첫번째구간_요청);

        SectionCreateRequest 두번째구간_요청 = SectionCreateRequest.builder()
                .distance(distance)
                .upStationId(역삼역.getId())
                .downStationId(선릉역.getId())
                .build();
        lineService.appendSection(이호선.getId(), 두번째구간_요청);

        // when
        SectionDeleteRequest 구간_삭제_요청 = SectionDeleteRequest.builder()
                .lineId(이호선.getId())
                .stationId(선릉역.getId())
                .build();
        lineService.deleteSection(구간_삭제_요청);

        // then
        long sectionsCount = lineService.findLineById(이호선.getId()).getLineSections().getSectionsCount();
        assertThat(sectionsCount).isEqualTo(1);

    }

    /**
     * Given 노선이 있을 때
     * When 노선의 정보를 변경하면
     * Then 노선의 정보가 변경 된 것을 죄회로 확인할 수 있다.
     */
    @DisplayName("노선의 정보를 변경한다")
    @Test
    void updateLine() {
        // given
        Station 강남역 = stationRepository.save(new Station(1L, "강남역"));
        Station 역삼역 = stationRepository.save(new Station(2L, "역삼역"));
        Line 이호선 = lineRepository.save(Line.builder().name("2호선").color("bg-green-600").build());
        final long distance = 10;

        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .distance(distance)
                .upStationId(강남역.getId())
                .downStationId(역삼역.getId())
                .build();
        lineService.appendSection(이호선.getId(), 구간_요청);

        // when
        final String changeName = "3호선";
        final String changeColor = "bg-amber-600";
        LineModifyRequest modifyRequest = LineModifyRequest.builder()
                .name(changeName)
                .color(changeColor)
                .build();
        lineService.updateLine(이호선.getId(), modifyRequest);

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getName()).isEqualTo(changeName);

    }

    /**
     * Given 노선이 있을 때
     * When 노선을 삭제 하면
     * Then 노선이 조회되지 않는다.
     */
    @DisplayName("노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        Station 강남역 = stationRepository.save(new Station(1L, "강남역"));
        Station 역삼역 = stationRepository.save(new Station(2L, "역삼역"));
        Line 이호선 = lineRepository.save(Line.builder().name("2호선").color("bg-green-600").build());
        final long distance = 10;

        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .distance(distance)
                .upStationId(강남역.getId())
                .downStationId(역삼역.getId())
                .build();
        lineService.appendSection(이호선.getId(), 구간_요청);

        // when
        lineService.deleteById(이호선.getId());

        // then
        assertThatThrownBy(() -> lineService.findLineById(이호선.getId()))
                .isInstanceOf(SubwayNotFoundException.class);


    }

    /**
     * When 노선을 저장하면
     * Then 노선이 저장된 것을 조회로 확인할 수 있다.
     */
    @DisplayName("노선을 저장한다.")
    @Test
    void saveLine() {
        // when
        Station 강남역 = stationRepository.save(new Station(1L, "강남역"));
        Station 역삼역 = stationRepository.save(new Station(2L, "역삼역"));
        Line 이호선 = lineRepository.save(Line.builder().name("2호선").color("bg-green-600").build());
        final long distance = 10;

        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .distance(distance)
                .upStationId(강남역.getId())
                .downStationId(역삼역.getId())
                .build();
        lineService.appendSection(이호선.getId(), 구간_요청);

        // then
        Line foundLine = lineService.findLineById(이호선.getId());
        assertThat(foundLine).isNotNull();
    }
}

