package fm.jopp.android.background.obtainer;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.lookup.Mail;
import com.announcify.api.background.contact.lookup.Name;

import fm.jopp.android.background.util.HandlerCreator;
import fm.jopp.android.background.util.MessageMap;
import fm.jopp.android.ui.widget.MessageAdapter;

public class GmailObtainer implements Obtainer {

    private List<MessageMap> list;
    private ContentResolver resolver;
    private ContentObserver observer;
    private MessageAdapter adapter;
    // TODO: private String[] addresses;
    private Context context;
    private String address;

    public GmailObtainer(final Context context, final MessageAdapter adapter) {
        resolver = context.getContentResolver();
        observer = new GmailObserver();
        list = adapter.getList();
        this.context = context;
        this.adapter = adapter;

        final AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        final Account[] accounts = manager.getAccountsByType("com.google");

        if (accounts.length == 0) {
            return;
        }

        for (final Account account : accounts) {
            address = account.name;
        }
    }

    @Override
    public void registerObserver() {
        resolver.registerContentObserver(Uri.parse("content://gmail-ls/conversations/" + Uri.encode(address)), true, observer);
    }

    @Override
    public void unregisterObserver() {
        resolver.unregisterContentObserver(observer);
    }

    @Override
    public boolean fetch(final int limit) {
        Cursor conversationCursor = null;
        Cursor messageCursor = null;

        try {
            String[] projection = new String[] { "conversation_id" };
            conversationCursor = context.getContentResolver().query(Uri.parse("content://gmail-ls/conversations/" + address), projection, null, null, null);
            if (conversationCursor == null || !conversationCursor.moveToFirst()) {
                return false;
            }

            final long[] conversationIds = new long[conversationCursor.getCount() > limit ? limit : conversationCursor.getCount()];
            final int conversationIdIndex = conversationCursor.getColumnIndex(projection[0]);
            int i = 0;
            do {
                conversationIds[i] = conversationCursor.getLong(conversationIdIndex);

                i++;
            } while (conversationCursor.moveToNext() && i < conversationIds.length);

            projection = new String[] { "fromAddress", "subject", "snippet", "body", "dateReceivedMs" };
            for (final long conversationId : conversationIds) {
                messageCursor = context.getContentResolver().query(Uri.parse("content://gmail-ls/conversations/" + address + "/" + Uri.parse(String.valueOf(conversationId)) + "/messages"), projection, null, null, null);
                if (!messageCursor.moveToLast()) {
                    continue;
                }

                final Contact contact = new Contact(context, new Mail(context, true), messageCursor.getString(messageCursor.getColumnIndex(projection[0])));
                Name.getFullname(context, contact);

                final MessageMap map = new MessageMap();
                map.put("message", messageCursor.getString(messageCursor.getColumnIndex(projection[1])));
                map.put("sender", contact.getFullname());
                map.put("timestamp", messageCursor.getString(messageCursor.getColumnIndex(projection[4])));
                map.put("color", "#BF0000");
                map.put("uri", "mailto:" + messageCursor.getString(messageCursor.getColumnIndex(projection[0])));

                list.add(map);
            }

            return true;
        } finally {
            if (messageCursor != null) {
                messageCursor.close();
            }
            if (conversationCursor != null) {
                conversationCursor.close();
            }
        }
    }

    class GmailObserver extends ContentObserver {

        public GmailObserver() {
            super(HandlerCreator.create("GMail", context));
        }

        @Override
        public void onChange(final boolean selfChange) {
            fetch(1);
        }
    }
}
