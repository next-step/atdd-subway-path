package nextstep.subway.line.section;

public class ApplyDistance {
    private final ApplyType applyType;
    private final Long distance;

    public ApplyDistance(ApplyType applyType,
                         Long distance) {
        this.applyType = applyType;
        this.distance = distance;
    }

    public static ApplyDistance applyAddFirst(Long distance) {
        return new ApplyDistance(ApplyType.ADD_FIRST, distance);
    }

    public static ApplyDistance applyAddLast(Long distance) {
        return new ApplyDistance(ApplyType.ADD_LAST, distance);
    }

    public static ApplyDistance applyAddMiddle(Long distance) {
        return new ApplyDistance(ApplyType.ADD_MIDDLE, distance);
    }

    public static ApplyDistance applyDeleteFirst(Long distance) {
        return new ApplyDistance(ApplyType.DELETE_FIRST, distance);
    }

    public static ApplyDistance applyDeleteLast(Long distance) {
        return new ApplyDistance(ApplyType.DELETE_LAST, distance);
    }

    public static ApplyDistance applyDeleteMiddle(Long distance) {
        return new ApplyDistance(ApplyType.DELETE_MIDDLE, distance);
    }

    public Long applyValue() {
        if(this.applyType.isApplyMiddle()) {
            return 0L;
        }
        return this.distance;
    }

    public void validAdd(Long distance) {
        if(this.applyType.isApplyMiddle() && (distance <= this.distance)) {
                throw new IllegalArgumentException("중간에 추가되는 구간은 라인보다 길수 없습니다.");
        }
    }
}
