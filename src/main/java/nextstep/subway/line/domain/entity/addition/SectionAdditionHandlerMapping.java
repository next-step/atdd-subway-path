package nextstep.subway.line.domain.entity.addition;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.vo.Sections;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectionAdditionHandlerMapping {

    private final List<SectionAdditionHandler> handlerList = List.of(
            new AddSectionAtLastHandler(),
            new AddSectionAtFirstHandler(),
            new AddSectionAtMiddleRightHandler(),
            new AddSectionAtMiddleLeftHandler()
    );

    public SectionAdditionHandler getHandler(Sections sections, Section section) {
        for  (SectionAdditionHandler handler : handlerList) {
            if (handler.checkApplicable(sections, section)) {
                return handler;
            }
        }
        throw new CreationValidationException("section.0004");
    }
}
