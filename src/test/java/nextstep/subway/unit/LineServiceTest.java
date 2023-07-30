package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.exception.BadRequestSectionsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 서비스 단위 테스트 without Mock")
@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;
    @Autowired
    private SectionService sectionService;

    private final String COLOR_RED = "bg-red-600";

    private final int DISTANCE = 10;

    private Station 신사역, 논현역, 신논현역, 광교역, 강남역;
    private Line 신분당선;
    @BeforeEach
    void set(){
        신사역 = stationRepository.save(new Station("신사역"));
        논현역 = stationRepository.save(new Station("논현역"));
        신논현역 = stationRepository.save(new Station("신논현역"));
        강남역 = stationRepository.save(new Station("강남역"));
        광교역 = stationRepository.save(new Station("광교역"));

        신분당선 = lineRepository.save(new Line("신분당선", COLOR_RED, 논현역, 강남역, 30));
    }


    @DisplayName("노선의 상행 종점역 구간을 등록")
    @Test
    void addFirstSection() {

        // when
        sectionService.saveSection(신분당선.getId(),new SectionRequest(신사역.getId(),논현역.getId(),DISTANCE));

        // then
        assertThat(신분당선.getStations()).containsExactly(신사역,논현역,강남역);
    }

    @DisplayName("기존 구간 중간에 신규 구간을 등록")
    @Test
    void addMiddleSection() {

        // when
        sectionService.saveSection(신분당선.getId(),new SectionRequest(논현역.getId(),신논현역.getId(),DISTANCE));

        // then
        assertThat(신분당선.getStations()).containsExactly(논현역,신논현역,강남역);
    }

    @DisplayName("노선의 하행 종점역 구간을 등록")
    @Test
    void addLastSection() {

        // when
        sectionService.saveSection(신분당선.getId(),new SectionRequest(강남역.getId(),광교역.getId(),DISTANCE));

        // then
        assertThat(신분당선.getStations()).containsExactly(논현역,강남역,광교역);
    }


    @DisplayName("노선에서 상행 종점 구간을 삭제")
    @Test
    void removeFirstSection() {
        // given
        sectionService.saveSection(신분당선.getId(),new SectionRequest(강남역.getId(),광교역.getId(),DISTANCE));

        // when
        sectionService.removeSection(신분당선.getId(),논현역.getId());

        // then
        assertThat(신분당선.getStations()).containsExactly(강남역,광교역);
    }

    @DisplayName("노선에서 중간 구간을 삭제")
    @Test
    void removeMiddleSection() {
        // given
        sectionService.saveSection(신분당선.getId(),new SectionRequest(강남역.getId(),광교역.getId(),DISTANCE));

        // when
        sectionService.removeSection(신분당선.getId(),강남역.getId());

        // then
        assertThat(신분당선.getStations()).containsExactly(논현역,광교역);
    }

    @DisplayName("노선에서 하행 종점 구간을 삭제")
    @Test
    void removeLastSection() {
        // given
        sectionService.saveSection(신분당선.getId(),new SectionRequest(강남역.getId(),광교역.getId(),DISTANCE));

        // when
        sectionService.removeSection(신분당선.getId(),광교역.getId());

        // then
        assertThat(신분당선.getStations()).containsExactly(논현역,강남역);
    }

    @DisplayName("노선에서 등록되지 않은 구간을 삭제할 경우 에러를 던짐")
    @Test
    void removeSectionWhenNotExist() {
        // then
        Assertions.assertThrows(BadRequestSectionsException.class
                                ,()->sectionService.removeSection(신분당선.getId(),광교역.getId()));
    }

}
