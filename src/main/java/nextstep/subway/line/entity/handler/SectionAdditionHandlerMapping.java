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
        throw new CreationValidationException("구간 정보에 맞는 핸들러가 존재하지 않습니다.");
    }
}
