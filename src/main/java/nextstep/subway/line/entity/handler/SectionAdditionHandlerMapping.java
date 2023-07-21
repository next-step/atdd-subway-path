package nextstep.subway.line.entity.handler;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

import java.util.List;

public class SectionAdditionHandlerMapping {

    private static final List<SectionAdditionHandler> handlerList = List.of(
            new AddSectionAtLastHandler(new AnyStationPreExistCheckHandler(null)),
            new AddSectionAtFirstHandler(new AnyStationPreExistCheckHandler(null)),
            new AddSectionAtMiddleRightHandler(new AnyStationPreExistCheckHandler(null)),
            new AddSectionAtMiddleLeftHandler(new AnyStationPreExistCheckHandler(null))
    );

    public static SectionAdditionHandler getHandler(Sections sections, Section section) {
        for  (SectionAdditionHandler handler : handlerList) {
            if (handler.checkApplicable(sections, section)) {
                return handler;
            }
        }
        throw new CreationValidationException("section.0004");
    }
}
