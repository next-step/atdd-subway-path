package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * 지하철 노선의 최단 거리 탐색에 대한 단위 테스트를 진행합니다.
 */
@UnitTest
@DisplayName("노선 최단 거리 탐색에 대한 도메인 단위 테스트")
class PathShortestDistanceSearcherTest {

    PathStation 서울대입구역;
    PathStation 강남역;
    PathStation 성수역;
    PathStation 건대입구역;
    @BeforeEach
    void setUp() {
        //given
        PathStation.Id 서울대입구역Id = PathStation.Id.of(1L);
        서울대입구역 = PathStation.of(서울대입구역Id, "서울대입구역");

        PathStation.Id 강남역Id = PathStation.Id.of(2L);
        강남역 = PathStation.of(강남역Id, "강남역");

        PathStation.Id 성수역Id = PathStation.Id.of(3L);
        성수역 = PathStation.of(성수역Id, "성수역");

        PathStation.Id 건대입구역Id = PathStation.Id.of(4L);
        건대입구역 = PathStation.of(건대입구역Id, "건대입구역");
    }

    /**
     * @given A, B, C, D 역이 존재하고
     * @given 탑승역이 A역, 하차역이 B역, 거리가 5인 구간이 존재하고
     * @given 탑승역이 B역, 하차역이 C역, 거리가 12인 구간이 존재하고
     * @given 탑승역이 A역, 하차역이 D역, 거리가 5인 구간이 존재하고
     * @given 탑승역이 D역, 하차역이 C역, 거리가 8인 구간이 존재한다고 하면
     * @when 두가지 경로를 갖고 있는 A역과 C역을 기준으로 경로탐색을 할 때
     * @then 두 경로 중 총 거리가 더 적은 경로로 갈 수 있다.
     */
    @Test
    @DisplayName("두 경로 이상 중 총 거리가 더 적은 경로로 갈 수 있다.")
    void searchPathBetweenDifferentPaths() {

        //given
        PathSection firstSection = createPathSection(1L, 서울대입구역, 강남역, Kilometer.of(5));

        //given
        PathSection secondSection = createPathSection(2L, 강남역, 성수역, Kilometer.of(12));

        //given
        PathSection thirdSection = createPathSection(3L, 서울대입구역, 건대입구역, Kilometer.of(5));

        //given
        PathSection fourthSection = createPathSection(4L, 건대입구역, 성수역, Kilometer.of(8));

        //when
        PathSearcher pathSearcher = PathShortestDistanceSearcher.from(List.of(firstSection, secondSection, thirdSection, fourthSection));
        SubwayPath response = pathSearcher.search(서울대입구역, 성수역);

        //then
        assertAll(
                () -> assertThat(response).isNotNull()
                        .extracting(SubwayPath::getDistance)
                        .isEqualTo(Kilometer.of(13)),
                () -> assertThat(response.getStations())
                        .containsExactly(서울대입구역, 건대입구역, 성수역));
    }

    /**
     * @given 탑승역이 A역, 하차역이 B역, 거리가 5인 구간이 존재하고
     * @given 탑승역이 B역, 하차역이 C역, 거리가 8인 구간이 존재하고
     * @given 탑승역이 C역, 하차역이 D역, 거리가 12인 구간이 존재한다고 하면
     * @when A역과 D역을 기준으로 경로탐색을 할 때
     * @then 서로 다른 두 노선을 환승하여 최단 거리로 갈 수 있다.
     */
    @Test
    @DisplayName("서로 다른 두 노선을 환승하여 최단 거리로 갈 수 있다.")
    void searchPathBetweenTwoLines() {
        //given
        PathSection firstSection = createPathSection(1L, 서울대입구역, 강남역, Kilometer.of(5));

        //given
        PathSection secondSection = createPathSection(2L, 강남역, 성수역, Kilometer.of(8));

        //given
        PathSection thirdSection = createPathSection(3L, 성수역, 건대입구역, Kilometer.of(12));

        //when
        PathSearcher pathSearcher = PathShortestDistanceSearcher.from(List.of(firstSection, secondSection, thirdSection));
        SubwayPath response = pathSearcher.search(서울대입구역, 건대입구역);

        //then
        assertAll(
                () -> assertThat(response).isNotNull()
                        .extracting(SubwayPath::getDistance)
                        .isEqualTo(Kilometer.of(25)),
                () -> assertThat(response.getStations())
                        .containsExactly(서울대입구역, 강남역, 성수역, 건대입구역));
    }

    /**
     * @given 탑승역이 A역, 하차역이 B역, 거리가 5인 구간이 존재하고
     * @given 탑승역이 B역, 하차역이 C역, 거리가 12인 구간이 존재하고
     * @given 탑승역이 B역, 하차역이 C역, 거리가 8인 구간이 존재하고
     * @given 탑승역이 C역, 하차역이 D역, 거리가 12인 구간이 존재한다고 하면
     * @when 두 노선이 중복되는 구간이 있는 A역과 D역을 기준으로 경로탐색을 할 때
     * @then 더 짧은 거리로 갈 수 있는 노선을 선택하여 최단 거리로 갈 수 있다.
     */
    @Test
    @DisplayName("중복되는 구간이 있다면 그 중 더 짧은 거리로 갈 수 있는 노선을 선택하여 최단 거리로 갈 수 있다.")
    public void searchPathBetweenDuplicatedSection() {
        //given
        PathSection firstSection = createPathSection(1L, 서울대입구역, 강남역, Kilometer.of(5));

        //given
        PathSection secondSection = createPathSection(2L, 강남역, 건대입구역, Kilometer.of(12));

        //given
        PathSection thirdSection = createPathSection(3L, 강남역, 건대입구역, Kilometer.of(8));

        //given
        PathSection fourthSection = createPathSection(4L, 건대입구역, 성수역, Kilometer.of(12));

        //when
        PathSearcher pathSearcher = PathShortestDistanceSearcher.from(List.of(firstSection, secondSection, thirdSection, fourthSection));
        SubwayPath response = pathSearcher.search(서울대입구역, 성수역);

        //then
        assertAll(
                () -> assertThat(response).isNotNull()
                        .extracting(SubwayPath::getDistance)
                        .isEqualTo(Kilometer.of(25)),
                () -> assertThat(response.getStations())
                        .containsExactly(서울대입구역, 강남역, 건대입구역, 성수역));
    }

    /**
     * @given 탑승역이 A역, 하차역이 B역인 구간이 존재하고
     * @given 탑승역이 B역, 하차역이 C역인 구간이 존재하고
     * @given 탑승역이 C역, 하차역이 A역인 구간이 존재한다고 하면
     * @when 탑승역과 하차역을 모두 A역으로 경로탐색을 할 때
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("같은 역을 기준으로 경로탐색을 할 수 없다.")
    public void cantSearchPathBetweenSameStation() {
        //given
        PathSection firstSection = createPathSection(1L, 서울대입구역, 강남역, Kilometer.of(5));

        //given
        PathSection secondSection = createPathSection(2L, 강남역, 건대입구역, Kilometer.of(12));

        //given
        PathSection thirdSection = createPathSection(3L, 건대입구역, 서울대입구역, Kilometer.of(8));

        //when
        PathSearcher pathSearcher = PathShortestDistanceSearcher.from(List.of(firstSection, secondSection, thirdSection));

        Throwable throwable = catchThrowable(() -> pathSearcher.search(서울대입구역, 서울대입구역));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로를 찾을 수 없습니다.");
    }

    /**
     * @given A, B, C, D 역이 존재하고
     * @given 탑승역이 A역, 하차역이 B역인 구간이 존재하고
     * @given 탑승역이 C역, 하차역이 D역인 구간이 존재한다고 하면
     * @when 서로 이어지지 않은 A역과 D역을 기준으로 경로탐색을 할 때
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("서로 이어지지 않은 두 역을 기준으로 경로탐색을 할 수 없다.")
    public void cantSearchPathBetweenNotConnectedStations() {
        //given
        PathSection firstSection = createPathSection(1L, 서울대입구역, 강남역, Kilometer.of(5));

        //given
        PathSection secondSection = createPathSection(2L, 성수역, 건대입구역, Kilometer.of(12));

        //when
        PathSearcher pathSearcher = PathShortestDistanceSearcher.from(List.of(firstSection, secondSection));

        Throwable throwable = catchThrowable(() -> pathSearcher.search(서울대입구역, 서울대입구역));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로를 찾을 수 없습니다.");
    }

    private PathSection createPathSection(Long id, PathStation upStation, PathStation downStation, Kilometer distance) {
        PathSection.Id domainId = PathSection.Id.of(id);
        return PathSection.of(
                domainId,
                upStation,
                downStation,
                distance);
    }
}