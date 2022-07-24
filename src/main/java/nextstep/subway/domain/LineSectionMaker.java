package nextstep.subway.domain;

import java.util.List;
import java.util.stream.IntStream;

public class LineSectionMaker {


    //구간 추가 유형 확인하고 진행하기
    public List<Section> makeSection(SectionUpdatePosition position, List<Section> sections, Section section){

        if(position.equals(SectionUpdatePosition.FIRST)){
            return addSectionFirst(sections, section);
        }
        if(position.equals(SectionUpdatePosition.END)){
            return addSectionEnd(sections, section);
        }
        if(position.equals(SectionUpdatePosition.MIDDLE)){
            return addSectionInMid(sections, section);
        }
        return null;
    }

    //역 사이에 새로운 역을 등록하는 경우
    public List<Section> addSectionInMid(List<Section> sections, Section section) {

        int index = IntStream.range(0,sections.size())
                .filter(idx -> sections.get(idx).getUpStation().equals(section.getUpStation()))
                .findAny().orElse(-1);

        List<Section> currentSections = sections;
        Section currentSection = currentSections.get(index);
        currentSections.add(index, section);

        currentSection.setUpStation(section.getDownStation());
        currentSection.setDistance(currentSection.getDistance() - section.getDistance());
        currentSections.set(index + 1, currentSection);

        return currentSections;

    }

    //새로운 역을 상행 종점으로 등록할 경우
    public List<Section> addSectionFirst(List<Section> currentSections, Section section){
        currentSections.add(0,section);
        return currentSections;
    }

    //새로운 역을 하행 종점으로 등록할 경우
    public List<Section> addSectionEnd(List<Section> currentSections, Section section){
        currentSections.add(section);
        return currentSections;
    }

}
