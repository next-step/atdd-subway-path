package nextstep.subway.line.section;

public class ApplyDistance {
    private final ApplyType applyType;
    private final Long distance;

    public ApplyDistance(ApplyType applyType,
                         Long distance) {
        this.applyType = applyType;
        this.distance = distance;
    }

    public static ApplyDistance applyFirst(Long distance) {
        return new ApplyDistance(ApplyType.FIRST, distance);
    }

    public static ApplyDistance applyLast(Long distance) {
        return new ApplyDistance(ApplyType.LAST, distance);
    }

    public static ApplyDistance applyMiddle(Long distance) {
        return new ApplyDistance(ApplyType.MIDDLE, distance);
    }

    public Long applyValue() {
        if(this.applyType.isApplyMiddle()) {
            return 0L;
        }
        return this.distance;
    }
}
