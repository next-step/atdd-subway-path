package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.applicaion.exception.SectionDeleteException;
import nextstep.subway.applicaion.exception.SectionExistException;
import nextstep.subway.applicaion.exception.SectionNotFoundException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (isExistedStation(section.getUpStation()) && isExistedStation(section.getDownStation())) {
            throw new SectionExistException("이미 지정된 구간입니다.");
        }
        if (!isExistedStation(section.getUpStation()) && !isExistedStation(section.getDownStation())) {
            throw new SectionExistException("구간으로 등록하려면 상행역 또는 하행역이 포함되어 있어야 합니다.");
        }
        modifyExistedSection(section);
        sections.add(section);
    }

    public void deleteSection(Station station) {
        if(sections.size() == 1) {
            throw new SectionDeleteException("구간이 하나인 경우 구간을 삭제할 수 없습니다.");
        }
        if(!isExistedStation(station)) {
            throw new SectionDeleteException("노선에 등록되지 않은 역은 제거할 수 없습니다.");
        }

        Optional<Section> upSection = findSectionByUpStation(station);
        Optional<Section> downSection = findSectionByDownStation(station);

        if(upSection.isEmpty() && downSection.isPresent()) {
            sections.remove(downSection.get());

        } else if(upSection.isPresent() && downSection.isEmpty()) {
            sections.remove(upSection.get());

        } else if(upSection.isPresent() && downSection.isPresent()) {
            upSection.get().modifyUp(downSection.get().getUpStation());
            upSection.get().modifyDistanceForRemove(downSection.get());
            sections.remove(downSection.get());
        }

    }

    private boolean isExistedStation(Station station) {
        return sections.stream().anyMatch(section -> section.isExistedStation(station));
    }
    private void modifyExistedSection(Section addSection) {
        if (sections.stream().anyMatch(section -> section.isUp(addSection.getUpStation()))) {
            Section section = findSectionByUpStation(addSection.getUpStation())
                    .orElseThrow(() -> new SectionNotFoundException("구간을 찾을 수 없습니다."));
            section.modifyDistanceForAdd(addSection);
            section.modifyUp(addSection.getDownStation());
        }
        if (sections.stream().anyMatch(section -> section.isDown(addSection.getDownStation()))) {
            Section section = findSectionByDownStation(addSection.getDownStation())
                    .orElseThrow(() -> new SectionNotFoundException("구간을 찾을 수 없습니다."));
            section.modifyDistanceForAdd(addSection);
            section.modifyDown(addSection.getUpStation());
        }
    }
    public Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny();
    }
    public Optional<Section> findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny();
    }
}
