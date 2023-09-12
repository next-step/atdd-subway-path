package subway.domain;

/**
 * 지하철 경로 탐색 인터페이스
 */
public interface PathSearcher {

    /**
     * 지하철 경로 탐색을 합니다.
     * @param source 출발역
     * @param target 도착역
     * @return 경로 탐색 결과
     */
    SubwayPath search(PathStation source, PathStation target);

}
