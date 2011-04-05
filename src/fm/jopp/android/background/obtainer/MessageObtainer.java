package fm.jopp.android.background.obtainer;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
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
