package nextstep.subway.line.entity.handler;

import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectionAdditionHandlerMapping {

    List<SectionAdditionHandler> handlerList = List.of(
            new AddSectionAtLastHandler()
    );

    public SectionAdditionHandler getHandler(Sections sections, Section section) {
        for  (SectionAdditionHandler handler : handlerList) {
            if (handler.checkApplicable(sections, section)) {
                return handler;
            }
        }
        throw new IllegalArgumentException("구간 정보에 맞는 핸들러가 존재하지 않습니다.");
    }
}
