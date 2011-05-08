package fm.jopp.android.background.util;

import java.util.ArrayList;

import android.content.Context;
import fm.jopp.android.ui.widget.MessageAdapter;

@SuppressWarnings("serial")
public class UniqueList<E> extends ArrayList<MessageMap> {

    private final MessageAdapter adapter;
    private final Context context;

    public UniqueList(final Context context, final MessageAdapter adapter) {
        this.adapter = adapter;
        this.context = context;
    }

    public UniqueList(final Context context, final MessageAdapter adapter, final int capacity) {
        super(capacity);

        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public boolean add(final MessageMap object) {
        if ("ad:ad".equals(object.get("uri")) || !contains(object)) {
            if (super.add(object)) {
                adapter.notifyChange();

                if (size() >= 50 && !Money.isDonate(context)) {
                    final MessageMap map = MessageMap.adMap;
                    map.put("timestamp", String.valueOf(System.currentTimeMillis() + 3000));
                    super.add(map);
                }

                return true;
            }
        }

        return false;
    }
}
