package nextstep.subway.unit;

import nextstep.subway.exception.PathNotFoundException;
import nextstep.subway.line.LineCreateRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.path.PathResponse;
import nextstep.subway.path.PathService;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

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
     * When : 방화역에서 강남역까지의 경로를 조회하면
     * Then : 이어져있지 않기 때문에 경로를 찾을 수 없다는 예외를 던진다
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
     * Given: 5호선: (방화역 - 올림픽공원역), 9호선: (올림픽공원역 - 종합운동장역 - 석촌역)을 생성하고
     *        9호선에서 올림픽공원역을 삭제하고
     * When : 방화역에서 석촌역까지의 경로를 조회하면
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

    /**
     * Given: (방화역 - 5 - 마천역)을 생성하고, 방화역과 마천역의 중간에 강동역(거리3)을 추가하고
     *        (방화역 - 3 - 강동역 - 2 - 마천역)
     * When : 방화역에서 마천역까지의 경로를 조회하면
     * Then : 거리가 5가 되어야 한다
     */
    @Test
    void testMiddleAdditionSuccess() {
        // given
        Station banghwa = stationRepository.save(new Station("방화역"));
        Station macheon = stationRepository.save(new Station("마천역"));
        LineResponse line5 = lineService.create(new LineCreateRequest(
                "5호선",
                "purple",
                5,
                banghwa.getId(),
                macheon.getId()
        ));

        Station gangdong = stationRepository.save(new Station("강동역"));
        lineService.addSection(line5.getId(), new SectionAddRequest(
                banghwa.getId(),
                gangdong.getId(),
                3
        ));

        // when
        PathResponse pathResponse = pathService.showShortestPath(banghwa.getId(), macheon.getId());

        // then
        assertThat(pathResponse.getDistance()).isEqualTo(5);

        pathResponse = pathService.showShortestPath(gangdong.getId(), macheon.getId());
        assertThat(pathResponse.getDistance()).isEqualTo(2);

    }

    /**
     * Given: (교대역 - 2 - 남부터미널역 - 5 - 양재역)
     *        (교대역 - 2 - 남부터미널역 - 3 - 가락시장역 - 2 - 양재역) -- 가락시장역 중간에 추가
     *        (교대역 - 5 - 가락시장역 - 2 - 양재역)                -- 남부터미널역 삭제
     * When : 교대역부터 양재역까지의 경로를 조회하면
     * Then : 거리는 7, 경로는 (교대역 - 가락시장역 - 양재역)이 되어야 한다.
     */
    @Test
    void testComplicatedCase() {
        // given
        Station gyodae = stationRepository.save(new Station("교대역"));
        Station nambooterminal = stationRepository.save(new Station("남부터미널역"));
        Station garakmarket = stationRepository.save(new Station("가락시장역"));
        Station yangjae = stationRepository.save(new Station("양재역"));
        LineResponse line3 = lineService.create(new LineCreateRequest(
                "3호선",
                "brown",
                2,
                gyodae.getId(),
                nambooterminal.getId()
        ));
        lineService.addSection(line3.getId(), new SectionAddRequest(
                nambooterminal.getId(),
                yangjae.getId(),
                5
        ));
        lineService.addSection(line3.getId(), new SectionAddRequest(
                nambooterminal.getId(),
                garakmarket.getId(),
                3
        ));
        lineService.deleteSection(line3.getId(), nambooterminal.getId());

        // when
        PathResponse pathResponse = pathService.showShortestPath(gyodae.getId(), yangjae.getId());

        // then
        assertThat(pathResponse.getDistance()).isEqualTo(7);
        List<Long> stationIds = parseStations(pathResponse.getStations());
        assertThat(stationIds).containsExactly(gyodae.getId(), garakmarket.getId(), yangjae.getId());
    }

    /**
     * Given: (방화역 - 3 - 강동역 - 2 - 마천역)을 생성하고, 강동역을 삭제하고
     *        (방화역 - 5 - 마천역)
     * When : 방화역에서 마천역까지의 경로를 조회하면
     * Then : 거리가 5가 되어야 한다
     */
    @Test
    void testMiddleDeletionSuccess() {
        // given
        Station banghwa = stationRepository.save(new Station("방화역"));
        Station macheon = stationRepository.save(new Station("마천역"));
        LineResponse line5 = lineService.create(new LineCreateRequest(
                "5호선",
                "purple",
                5,
                banghwa.getId(),
                macheon.getId()
        ));

        Station gangdong = stationRepository.save(new Station("강동역"));
        lineService.addSection(line5.getId(), new SectionAddRequest(
                banghwa.getId(),
                gangdong.getId(),
                3
        ));

        lineService.deleteSection(line5.getId(), gangdong.getId());

        // when
        PathResponse pathResponse = pathService.showShortestPath(banghwa.getId(), macheon.getId());

        // then
        assertThat(pathResponse.getDistance()).isEqualTo(5);


    }

    private List<Long> parseStations(List<StationResponse> stationResponses) {
        return stationResponses.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }
}
