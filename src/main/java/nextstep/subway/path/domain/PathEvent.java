package nextstep.subway.path.domain;

import org.springframework.context.ApplicationEvent;

public class PathEvent extends ApplicationEvent {
    public PathEvent(Object source) {
        super(source);
    }
}

