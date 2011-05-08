package fm.jopp.android.background.util;

import java.util.HashMap;

@SuppressWarnings("serial")
public class MessageMap extends HashMap<String, String> {

    public static final MessageMap adMap;

    static {
        adMap = new MessageMap();
        adMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        adMap.put("message", "Ads");
        adMap.put("color", "#FFFFFF");
        adMap.put("uri", "ad:ad");
        adMap.put("sender", "Tom Tasche");
    }

    @Override
    public boolean equals(final Object object) {
        final MessageMap map = (MessageMap) object;
        if (get("timestamp").equals(map.get("timestamp")) && get("message").equals(map.get("message")) && get("sender").equals(map.get("sender"))) {
            return true;
        } else {
            return false;
        }
    }
}
