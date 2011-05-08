package fm.jopp.android.ui.widget;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fm.jopp.android.R;
import fm.jopp.android.background.util.MessageMap;
import fm.jopp.android.background.util.TimestampComparator;
import fm.jopp.android.background.util.UniqueList;

public class MessageAdapter extends BaseAdapter {

    protected final UniqueList<MessageMap> list;
    protected final LayoutInflater inflater;
    protected final Context context;
    protected final Handler handler;

    public MessageAdapter(final Context context, final LayoutInflater inflater) {
        handler = new Handler();
        this.context = context;
        this.inflater = inflater;
        list = new UniqueList<MessageMap>(context, this, 50);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(final int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = createView();
        }

        final MessageMap map = (MessageMap) getItem(position);
        if ("ad:ad".equals(map.get("uri"))) {
            if (convertView.findViewById(R.id.ad) == null) {
                convertView = createAd();

                final AdView ad = (AdView) convertView.findViewById(R.id.ad);
                ad.setBackgroundColor(Color.WHITE);

                final AdRequest request = new AdRequest();

                ad.loadAd(request);
            }

            return convertView;
        }

        if (convertView.findViewById(R.id.ad) != null) {
            convertView = createView();
        }

        final View container = convertView.findViewById(R.id.container);
        container.setBackgroundColor(Color.parseColor(map.get("color")));

        final TextView message = (TextView) convertView.findViewById(android.R.id.text1);
        message.setText(map.get("message"));
        message.setTextColor(Color.BLACK);

        final TextView sender = (TextView) convertView.findViewById(android.R.id.text2);
        sender.setText(map.get("sender"));
        sender.setTextColor(Color.BLACK);

        return convertView;
    }

    protected View createView() {
        return inflater.inflate(R.layout.list_item, null);
    }

    protected View createAd() {
        return inflater.inflate(R.layout.list_item_ad, null);
    }

    public List<MessageMap> getList() {
        return list;
    }

    public void notifyChange() {
        Collections.sort(list, new TimestampComparator());

        handler.post(new Runnable() {

            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
