package nextstep.subway.unit;

import nextstep.subway.exception.PathNotFoundException;
import nextstep.subway.line.LineCreateRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.path.PathService;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private PathService pathService;

    /**
     * Given: (방화역 - 마천역) (강남역 - 동대문역사문화공원역) 생성하고
     * When : 경로를 조회하면
     * Then : 경로를 찾을 수 없다는 예외를 던진다
     */
    @Test
    void pathNotFound() {
        // given
        Station banghwa = stationRepository.save(new Station("방화역"));
        Station macheon = stationRepository.save(new Station("마천역"));
        lineService.create(new LineCreateRequest(
                "5호선",
                "purple",
                5,
                banghwa.getId(),
                macheon.getId()
        ));

        Station gangnam = stationRepository.save(new Station("강남역"));
        Station ddp = stationRepository.save(new Station("동대문역사문화공원역"));
        lineService.create(new LineCreateRequest(
                "2호선",
                "green",
                3,
                gangnam.getId(),
                ddp.getId()
        ));

        // when & Then
        assertThrows(PathNotFoundException.class, () -> {
            pathService.showShortestPath(banghwa.getId(), gangnam.getId());
        });
    }

    /**
     * Given: (방화역 - 올림픽공원역) (올림픽공원역 - 종합운동장역 - 석촌역)을 생성하고
     * When : 9호선에서 올림픽공원역을 삭제하면
     * Then : 경로를 찾을 수 없다는 예외를 던진다
     */
    @Test
    void afterDeletion() {
        // given
        Station banghwa = stationRepository.save(new Station("방화역"));
        Station olimpicpark = stationRepository.save(new Station("올림픽공원역"));
        lineService.create(new LineCreateRequest(
                "5호선",
                "purple",
                5,
                banghwa.getId(),
                olimpicpark.getId()
        ));

        Station sportscomplex = stationRepository.save(new Station("종합운동장역"));
        Station seokchon = stationRepository.save(new Station("석촌역"));
        LineResponse line9 = lineService.create(new LineCreateRequest(
                "9호선",
                "gold",
                3,
                olimpicpark.getId(),
                sportscomplex.getId()
        ));
        lineService.addSection(line9.getId(), new SectionAddRequest(
                sportscomplex.getId(),
                seokchon.getId(), 1
        ));

        // when
        lineService.deleteSection(line9.getId(), olimpicpark.getId());

        // then
        assertThrows(PathNotFoundException.class, () -> {
            pathService.showShortestPath(banghwa.getId(), seokchon.getId());
        });
    }
}
