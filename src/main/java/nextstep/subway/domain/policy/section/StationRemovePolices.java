package nextstep.subway.domain.policy.section;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.policy.section.remove.StationBetweenRemovePolicy;
import nextstep.subway.domain.policy.section.remove.StationFirstRemovePolicy;
import nextstep.subway.domain.policy.section.remove.StationLastRemovePolicy;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

import java.util.Arrays;
import java.util.List;

public class StationRemovePolices {

    private final List<StationRemovePolicy> values
            = Arrays.asList(new StationBetweenRemovePolicy()
            , new StationFirstRemovePolicy()
            , new StationLastRemovePolicy()
    );

    public StationRemovePolicy isSuitable(Sections sections, Station station) {
        return values.stream().
                filter(policy -> policy.isSatisfied(sections, station))
                .findFirst()
                .orElseThrow(() -> new SubwayException(SubwayExceptionMessage.STATION_CANNOT_REMOVE));
    }


}
