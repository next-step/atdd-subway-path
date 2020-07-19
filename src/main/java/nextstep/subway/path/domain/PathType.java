package nextstep.subway.path.domain;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.domain.LineStation;

public enum PathType {
    DISTANCE, DURATION;

    private boolean isDuration() {
        return DURATION == this;
    }

    private boolean isDistance() {
        return DISTANCE == this;
    }

    public double getWeight(LineStation lineStation) {
        if (isDistance()) {
            return lineStation.getDistance();
        }
        if (isDuration()) {
            return lineStation.getDuration();
        }
        throw new NotValidRequestException("지원하지 않는 경로 타입 입니다");
    }
}
