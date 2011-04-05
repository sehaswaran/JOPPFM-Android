package fm.jopp.android.background.util;

import java.util.Comparator;
import java.util.HashMap;

public class TimestampComparator implements Comparator<HashMap<String, String>> {

    @Override
    public int compare(final HashMap<String, String> object1, final HashMap<String, String> object2) {
        if (Long.valueOf(object1.get("timestamp")) < Long.valueOf(object2.get("timestamp"))) {
            return 1;
        }

        if (Long.valueOf(object1.get("timestamp")) > Long.valueOf(object2.get("timestamp"))) {
            return -1;
        }

        return 0;
    }
}
