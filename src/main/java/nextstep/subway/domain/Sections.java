package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSections){
        sections.add(newSections);
    }

    public void deleteSectionByIndex(Integer index){
        sections.remove(index);
    }

    public Integer getSectionSize(){
        return sections.size();
    }

    public List<Section> getSections(){
        return sections;
    }
}