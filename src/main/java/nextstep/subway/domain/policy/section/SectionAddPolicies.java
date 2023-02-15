package nextstep.subway.domain.policy.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.policy.section.add.SectionBetweenDownAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionBetweenUpAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionFirstAddPolicy;
import nextstep.subway.domain.policy.section.add.SectionLastAddPolicy;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

import java.util.Arrays;
import java.util.List;

public class SectionAddPolicies {
    private final List<SectionAddPolicy> values
            = Arrays.asList(new SectionFirstAddPolicy()
            , new SectionLastAddPolicy()
            , new SectionBetweenUpAddPolicy()
            , new SectionBetweenDownAddPolicy());

    public SectionAddPolicy getSuitable(Sections sections, Section section) {
        return values.stream().
                filter(policy -> policy.isSatisfied(sections, section))
                .findFirst()
                .orElseThrow(() -> new SubwayException(SubwayExceptionMessage.SECTION_CANNOT_ADD));
    }

}
