package nextstep.subway.common;

public enum AddTypeEnum {
    BACK_ADD_SECTION("0", "back"),
    FRONT_ADD_SECTION("1", "front"),
    MIDDLE_ADD_SECTION("2", "middle");

    String addTypeCd;
    String addTypeName;

    AddTypeEnum(String addTypeCd, String addTypeName) {
        this.addTypeCd = addTypeCd;
        this.addTypeName = addTypeName;
    }

    public String getAddTypeCd() {
        return addTypeCd;
    }

    public String getAddTypeName() {
        return addTypeName;
    }

    public static AddTypeEnum convertAddTypeCd(String addTypeCd){
        if (FRONT_ADD_SECTION.getAddTypeCd().equals(addTypeCd)) {
            return FRONT_ADD_SECTION;
        }

        if (MIDDLE_ADD_SECTION.getAddTypeCd().equals(addTypeCd)) {
            return MIDDLE_ADD_SECTION;
        }

        return BACK_ADD_SECTION;
    }
}
