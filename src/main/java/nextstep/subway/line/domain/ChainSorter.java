package nextstep.subway.line.domain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChainSorter<DATA, KEY> {

    private final Function<DATA, KEY> source;
    private final Function<DATA, KEY> target;

    public ChainSorter(final Function<DATA, KEY> source, final Function<DATA, KEY> target) {
        this.source = source;
        this.target = target;
    }

    public List<KEY> getSortedStationIds(final List<DATA> sections) {
        List<KEY> stationIds = new ArrayList<>();
        Map<KEY, KEY> upDownMap = sections.stream()
                .collect(Collectors.toMap(source, target));
        Set<KEY> downStationIds = sections.stream()
                .map(target).collect(Collectors.toSet());

        KEY currentStationId = getFirstUpStationId(upDownMap, downStationIds);

        stationIds.add(currentStationId);
        for (int i = 0; i < upDownMap.size(); i++) {
            KEY nextStationId = upDownMap.get(currentStationId);
            stationIds.add(nextStationId);
            currentStationId = nextStationId;
        }
        return stationIds;
    }

    private KEY getFirstUpStationId(final Map<KEY, KEY> upDownMap, final Set<KEY> downStationIds) {
        for (KEY upStationId : upDownMap.keySet()) {
            if (!downStationIds.contains(upStationId)) {
                return upStationId;
            }
        }
        throw new IllegalStateException("첫번째 역을 찾을 수 없습니다.");
    }

}
