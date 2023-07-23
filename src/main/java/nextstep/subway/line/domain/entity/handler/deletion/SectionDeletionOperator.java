package nextstep.subway.line.domain.entity.handler.deletion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SectionDeletionOperator {
    private List<SectionDeletionFilter> filterList = List.of(
            new StationNotExistFilter(),
            new SingularSectionExistFilter()
    );
    private final SectionDeletionHandlerMapping sectionDeletionHandlerMapping;

    public void apply(Sections sections, Station station) {
        filterList.stream()
                .forEach(f -> f.doFilter(sections, station));

        SectionDeletionHandler handler = sectionDeletionHandlerMapping.getHandler(sections, station);
        handler.apply(sections, station);
    }
}
