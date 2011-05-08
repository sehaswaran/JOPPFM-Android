package fm.jopp.android.ui.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.announcify.api.background.error.ExceptionHandler;

import fm.jopp.android.R;
import fm.jopp.android.background.obtainer.MessageObtainer;
import fm.jopp.android.background.util.MessageMap;
import fm.jopp.android.background.util.Money;
import fm.jopp.android.ui.widget.MessageAdapter;

public class JOPPFM extends ListActivity {

    protected MessageObtainer obtainer;
    protected MessageAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getBaseContext()));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        adapter = new MessageAdapter(this, getLayoutInflater());
        getListView().setAdapter(adapter);

        obtainer = new MessageObtainer(this, adapter);
        obtainer.fetchMessages();
        // obtainer.fetchMessagesForScreenshot();

        if (!Money.isDonate(this)) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    final MessageMap map = MessageMap.adMap;
                    map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    adapter.getList().add(map);
                }
            }, 10000);
        }

        getListView().setFastScrollEnabled(true);
        getListView().setEmptyView(findViewById(android.R.id.empty));
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final MessageMap temp = (MessageMap) adapter.getItem(position);
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(temp.get("uri")));
        startActivity(Intent.createChooser(intent, "Reply..."));
    }

    @Override
    protected void onStart() {
        obtainer.registerObservers();

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("JOPPFM: Just One Pretty Place For Messaging");
                builder.setIcon(getResources().getDrawable(R.drawable.icon));
                builder.setMessage("JOPPFM helps you to keep up with your friends.\nThere's only one place you need to check in order to see all your messages and conversations.\n\nhttp://joppfm.tomtasche.at/");
                builder.setCancelable(true);
                builder.create().show();

                break;

            case R.id.menu_rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/npjrM")));

                break;

            case R.id.menu_ads:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/p4jH2")));

                break;

            case R.id.menu_ads_temp:
                adapter.getList().remove(MessageMap.adMap);
                adapter.notifyChange();

                break;

            case R.id.menu_feedback:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/QeTJN")));

                break;

            case R.id.menu_share:
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "There's Just One Pretty Place For Messaging. #joppfm");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Spread the word..."));

                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStop() {
        obtainer.unregisterObservers();

        super.onStop();
    }
}