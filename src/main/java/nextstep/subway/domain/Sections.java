package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.BusinessException;
import nextstep.subway.applicaion.exception.NotLastSectionException;
import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public boolean isLastSection(){
       return sections.size() == 1;
    }

    public void deleteSection(Long stationId) {
        if (isLastSection()) {
            throw new BusinessException("마지막 구간 삭제 불가", HttpStatus.BAD_REQUEST);
        }
        int count = countStation(stationId);
        deleteSection(stationId, count);
    }

    private void deleteSection(Long stationId, int count) {
        int lastDownStation = 1;
        if (count == lastDownStation) {
            Section findSection = sections
                    .stream()
                    .filter(section ->
                            section.isSameDownStation(stationId))
                    .findFirst()
                    .orElse(null);
            sections.remove(findSection);
            return;
        }
        throw new NotLastSectionException();
    }

    public int countStation(Long stationId){
        return (int) sections.stream()
                .filter(section ->
                        section.isSameUpStation(stationId) || section.isSameDownStation(stationId))
                .count();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

}
