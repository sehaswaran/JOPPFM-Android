package fm.jopp.android.background.util;

import java.util.HashMap;

@SuppressWarnings("serial")
public class MessageMap extends HashMap<String, String> {

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
