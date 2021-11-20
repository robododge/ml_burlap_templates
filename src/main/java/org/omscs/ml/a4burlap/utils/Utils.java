package org.omscs.ml.a4burlap.utils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Utils {


    public static String uniqueDirName(){
        LocalDateTime ldt = LocalDateTime.now();
        return String.format("%02d%02d%02d%02d", ldt.getDayOfMonth(),ldt.getHour(),ldt.getMinute(),ldt.getMinute());
    }


}
