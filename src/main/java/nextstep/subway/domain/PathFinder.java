package nextstep.subway.domain;

public class PathFinder {

    private Long source;
    private Long target;

    private PathFinder() { }

    private PathFinder(Long source, Long target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같을 수는 없습니다.");
        }

        this.source = source;
        this.target = target;
    }

    public static PathFinder of(Long source, Long target) {
        return new PathFinder(source, target);
    }
}
