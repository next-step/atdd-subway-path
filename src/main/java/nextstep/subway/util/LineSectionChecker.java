package nextstep.subway.util;

import nextstep.subway.domain.Section;

import java.util.List;

public class LineSectionChecker {
    private List<Section> sections;
    private Section section;

    public LineSectionChecker(List<Section> sections, Section section) {
        this.sections = sections;
        this.section = section;
    }

    //어느 구간에 추가하려고 하는지 확인
    public SectionUpdatePosition checkAddPosition(){
        checkCommonException();
        if(section.getDownStation().equals(sections.get(0).getUpStation())){
            return SectionUpdatePosition.FIRST;
        }
        if(section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())){
            return SectionUpdatePosition.END;
        }
        if(checkMidException()){
            return SectionUpdatePosition.MIDDLE;
        }

        return SectionUpdatePosition.UNKNOWN;
    }

    public void checkCommonException(){
        boolean isContainsUp = sections.stream().anyMatch(sec -> sec.getLine().getStations().contains(section.getUpStation()));
        boolean isContainsDown = sections.stream().anyMatch(sec -> sec.getLine().getStations().contains(section.getDownStation()));


        //둘다 있는 경우
        if(isContainsUp && isContainsDown){
            throw new IllegalArgumentException("STATIONS_ALEADY_REGIST");
        }
        //둘다 없는 경우
        if(!isContainsUp && !isContainsDown){
            throw new IllegalArgumentException("HAS_NOT_STATIONS");
        }
    }

    public boolean checkMidException(){
        Section result = sections.stream().filter(sec-> sec.getUpStation().equals(section.getUpStation())
                || section.getDownStation().equals(section.getDownStation())).findAny().orElseGet(null);

        if(result.getDistance() < section.getDistance()){
            throw new IllegalArgumentException("DISTANCE_TOO_LONG");
        }

        if(result == null){
            return false;
        }
        return true;
    }

}
