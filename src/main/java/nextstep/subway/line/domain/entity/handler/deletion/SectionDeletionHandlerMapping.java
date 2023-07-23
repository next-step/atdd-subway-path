package nextstep.subway.line.domain.entity.handler.deletion;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectionDeletionHandlerMapping {

    private final List<SectionDeletionHandler> handlerList = List.of(
    );

    public SectionDeletionHandler getHandler(Sections sections, Station station) {
        for  (SectionDeletionHandler handler : handlerList) {
            if (handler.checkApplicable(sections, station)) {
                return handler;
            }
        }
        throw new CreationValidationException("section.0004");
    }
}
