package fm.jopp.android.background.obtainer;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.lookup.Name;

import fm.jopp.android.background.util.HandlerCreator;
import fm.jopp.android.background.util.MessageMap;
import fm.jopp.android.ui.widget.MessageAdapter;

public class SmsObtainer implements Obtainer {

    private final List<MessageMap> list;
    private final ContentResolver resolver;
    private final ContentObserver observer;
    private final MessageAdapter adapter;
    private final Context context;

    public SmsObtainer(final Context context, final MessageAdapter adapter) {
        resolver = context.getContentResolver();
        observer = new SmsObserver();
        list = adapter.getList();
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void registerObserver() {
        resolver.registerContentObserver(Uri.parse("content://sms/all/"), true, observer);
    }

    @Override
    public void unregisterObserver() {
        resolver.unregisterContentObserver(observer);
    }

    @Override
    public boolean fetch(final int limit) {
        Cursor messageCursor = null;
        try {
            final String[] messageProjection = new String[] { "body", "address", "date" };
            messageCursor = resolver.query(Uri.parse("content://sms/inbox/"), messageProjection, null, null, null);
            if (messageCursor == null || !messageCursor.moveToFirst()) {
                return false;
            }

            int i = 0;
            do {
                final MessageMap map = new MessageMap();
                map.put("timestamp", messageCursor.getString(messageCursor.getColumnIndex(messageProjection[2])));
                map.put("message", messageCursor.getString(messageCursor.getColumnIndex(messageProjection[0])));
                map.put("color", "#749B27");
                map.put("uri", "smsto:" + messageCursor.getString(messageCursor.getColumnIndex(messageProjection[1])));

                final Contact contact = new Contact(context, new com.announcify.api.background.contact.lookup.Number(context), messageCursor.getString(messageCursor.getColumnIndex(messageProjection[1])));
                Name.getFullname(context, contact);

                map.put("sender", contact.getFullname());

                list.add(map);

                i++;
            } while (messageCursor.moveToNext() && i < limit);

            return true;
        } finally {
            if (messageCursor != null) {
                messageCursor.close();
            }
        }
    }

    class SmsObserver extends ContentObserver {

        public SmsObserver() {
            super(HandlerCreator.create("SMS", context));
        }

        @Override
        public void onChange(final boolean selfChange) {
            fetch(1);
        }
    }
}
