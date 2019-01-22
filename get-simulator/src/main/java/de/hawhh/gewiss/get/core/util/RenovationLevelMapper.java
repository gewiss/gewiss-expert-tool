package de.hawhh.gewiss.get.core.util;

import de.hawhh.gewiss.get.core.model.RenovationLevel;

public class RenovationLevelMapper {

    public static RenovationLevel fromInteger(Integer renLevel) {
        if (renLevel == 0) {
            return RenovationLevel.NO_RENOVATION;
        } else if (renLevel == 1) {
            return RenovationLevel.BASIC_RENOVATION;
        } else if (renLevel == 2) {
            return RenovationLevel.GOOD_RENOVATION;
        }

        return null;
    }

    public static Integer fromRenovationLevel(RenovationLevel renovationLevel) {
        if (renovationLevel.equals(RenovationLevel.NO_RENOVATION)) {
            return 0;
        } else if (renovationLevel.equals(RenovationLevel.BASIC_RENOVATION)) {
            return 1;
        } else if (renovationLevel.equals(RenovationLevel.GOOD_RENOVATION)) {
            return 2;
        }

        return -1;
    }
}
