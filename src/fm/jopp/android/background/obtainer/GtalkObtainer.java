package fm.jopp.android.background.obtainer;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.lookup.Chat;
import com.announcify.api.background.contact.lookup.Name;

import fm.jopp.android.background.util.HandlerCreator;
import fm.jopp.android.background.util.MessageMap;
import fm.jopp.android.ui.widget.MessageAdapter;

public class GtalkObtainer implements Obtainer {

    private final List<MessageMap> list;
    private final ContentResolver resolver;
    private final ContentObserver observer;
    private final MessageAdapter adapter;
    private final Context context;

    public GtalkObtainer(final Context context, final MessageAdapter adapter) {
        resolver = context.getContentResolver();
        observer = new GtalkObserver();
        list = adapter.getList();
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void registerObserver() {
        resolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "messages"), true, observer);
    }

    @Override
    public void unregisterObserver() {
        resolver.unregisterContentObserver(observer);
    }

    @Override
    public boolean fetch(final int limit) {
        Cursor messageCursor = null;
        Cursor conversationCursor = null;
        Cursor contactCursor = null;
        try {
            final String[] messageProjection = new String[] { "body", "date", "type" };
            messageCursor = resolver.query(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "messages"), messageProjection, "err_code = 0", null, "date DESC");
            if (messageCursor == null || !messageCursor.moveToFirst()) {
                return false;
            }

            final String[] conversationProjection = new String[] { "last_unread_message", "last_message_date" };
            conversationCursor = resolver.query(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "chats"), conversationProjection, null, null, "last_message_date DESC");
            if (!conversationCursor.moveToFirst()) {
                return false;
            }

            do {
                if (messageCursor.getInt(messageCursor.getColumnIndex(messageProjection[2])) != 1) {
                    return false;
                }

                final MessageMap map = new MessageMap();
                map.put("timestamp", conversationCursor.getString(conversationCursor.getColumnIndex(conversationProjection[1])));
                map.put("message", messageCursor.getString(messageCursor.getColumnIndex(messageProjection[0])));

                final String[] contactProjection = new String[] { "username" };
                contactCursor = resolver.query(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "contacts"), contactProjection, "last_message_date = " + conversationCursor.getLong(conversationCursor.getColumnIndex("last_message_date")), null, null);
                if (!contactCursor.moveToFirst()) {
                    continue;
                }

                final Contact contact = new Contact(context, new Chat(context), contactCursor.getString(contactCursor.getColumnIndex(contactProjection[0])));
                Name.getFullname(context, contact);

                map.put("sender", contact.getFullname());
                map.put("color", "#4076AB");
                map.put("uri", "imto://gtalk/" + contactCursor.getString(contactCursor.getColumnIndex(contactProjection[0])));

                final MessageMap temp = list.get(0);
                if (messageCursor.getString(messageCursor.getColumnIndex(messageProjection[0])).startsWith(temp.get("message")) && temp.get("sender").equals(contact.getFullname())) {
                    list.remove(temp);
                } else if (messageCursor.getString(messageCursor.getColumnIndex(messageProjection[0])).endsWith(temp.get("message")) && temp.get("sender").equals(contact.getFullname())) {
                    list.remove(temp);
                }

                list.add(map);
            } while (messageCursor.moveToNext() && conversationCursor.moveToNext());

            return true;
        } finally {
            if (contactCursor != null) {
                contactCursor.close();
            }
            if (conversationCursor != null) {
                conversationCursor.close();
            }
            if (messageCursor != null) {
                messageCursor.close();
            }
        }
    }

    class GtalkObserver extends ContentObserver {

        public GtalkObserver() {
            super(HandlerCreator.create("GTalk", context));
        }

        @Override
        public void onChange(final boolean selfChange) {
            fetch(1);
        }
    }
}
