package fm.jopp.android.background.obtainer;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import fm.jopp.android.background.util.MessageMap;
import fm.jopp.android.ui.widget.MessageAdapter;

public class MessageObtainer {

    protected final Context context;
    protected final Handler handler;
    protected final HandlerThread thread;
    protected final MessageAdapter adapter;
    protected final Obtainer[] obtainers;

    public MessageObtainer(final Context context, final MessageAdapter adapter) {
        this.context = context;
        this.adapter = adapter;

        thread = new HandlerThread("JOPPFM");
        thread.start();

        handler = new Handler(thread.getLooper());

        obtainers = new Obtainer[3];
        obtainers[0] = new GmailObtainer(context, adapter);
        obtainers[1] = new GtalkObtainer(context, adapter);
        obtainers[2] = new SmsObtainer(context, adapter);
    }

    public void fetchMessages() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                for (final Obtainer obtainer : obtainers) {
                    obtainer.fetch(10);
                }
            }
        });
    }

    public void fetchMessagesForScreenshot() {
        MessageMap map = new MessageMap();
        map.put("message", "Are you there for some chat?");
        map.put("sender", "TomTom");
        map.put("timestamp", "99");
        map.put("color", "#4076AB");
        map.put("uri", "imto://gtalk/jirout.thomas@gmail.com");
        adapter.getList().add(map);

        map = new MessageMap();
        map.put("message", "Hm... Maybe you're reading your mails more frequently?");
        map.put("sender", "TomTom");
        map.put("timestamp", "100");
        map.put("color", "#BF0000");
        map.put("uri", "mailto:tomtasche@gmail.com");
        adapter.getList().add(map);

        map = new MessageMap();
        map.put("message", "Can't believe I signed up for Gmail just for you.");
        map.put("sender", "Girlfriend");
        map.put("timestamp", "103");
        map.put("color", "#BF0000");
        map.put("uri", "mailto:ch.scheer.93@gmail.com");
        adapter.getList().add(map);

        map = new MessageMap();
        map.put("timestamp", "101");
        map.put("message", "Ok, you didn't answer neither on Gtalk, nor on Gmail... What's up!?");
        map.put("color", "#749B27");
        map.put("uri", "smsto:0699128869221");
        map.put("sender", "TomTom");
        adapter.getList().add(map);

        map = new MessageMap();
        map.put("message", "Hey Tom, did you take a look at OpenOffice Document Reader's download statistics lately?");
        map.put("sender", "Andi Wand");
        map.put("timestamp", "102");
        map.put("color", "#4076AB");
        map.put("uri", "imto://gtalk/stefl.andreas@gmail.com");
        adapter.getList().add(map);

        map = new MessageMap();
        map.put("message", "Skyrocketing...!!");
        map.put("sender", "Andi Wand");
        map.put("timestamp", "104");
        map.put("color", "#4076AB");
        map.put("uri", "imto://gtalk/stefl.andreas@gmail.com");
        adapter.getList().add(map);

        map = new MessageMap();
        map.put("message", "Not bad so far. :)");
        map.put("sender", "Andi Wand");
        map.put("timestamp", "103");
        map.put("color", "#4076AB");
        map.put("uri", "imto://gtalk/stefl.andreas@gmail.com");
        adapter.getList().add(map);
    }

    public void registerObservers() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                for (final Obtainer obtainer : obtainers) {
                    obtainer.registerObserver();
                }
            }
        });
    }

    public void unregisterObservers() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                for (final Obtainer obtainer : obtainers) {
                    obtainer.unregisterObserver();
                }
            }
        });
    }
}
