package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.NfcCard;
import bitman.ay27.watchdog.ui.activity.widget.ReadNfcDialog;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/7.
 */
public class BindNfcActivity extends ActionBarActivity {

    @InjectView(R.id.bind_nfc_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.bind_nfc_list)
    ListView listView;
    @InjectView(R.id.bind_nfc_new_card)
    Button btn;
    @InjectView(R.id.bind_nfc_hint)
    TextView hintTxv;

    private NfcItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_nfc_card);
        ButterKnife.inject(this);

        init();
    }


    private void init() {
        List<NfcCard> tmp = DbManager.getInstance().query(NfcCard.class);
        if (tmp == null || tmp.size() == 0) {
            listView.setVisibility(View.GONE);
            hintTxv.setText(R.string.no_nfc_card);
            return;
        }

        listView.setAdapter(adapter = new NfcItemAdapter(tmp));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(BindNfcActivity.this)
                        .setTitle(R.string.ask_delete_card)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.remove(position);
                                Toast.makeText(BindNfcActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

    }

    @OnClick(R.id.bind_nfc_new_card)
    public void newCardClick(View view) {
        new ReadNfcDialog(this, this, new ReadNfcDialog.FoundNfcCallback() {
            @Override
            public void onNfcFound(NfcCard card) {
                DbManager manager = DbManager.getInstance();
                List<NfcCard> cards = manager.query(NfcCard.class);
                if (cards != null && cards.size() != 0) {
                    for (NfcCard card1 : cards) {
                        if (card.code.equals(card1.code)) {
                            card1.name = card.name;
                            manager.update(NfcCard.class, card1);
                            Toast.makeText(BindNfcActivity.this, R.string.update_ok, Toast.LENGTH_SHORT).show();

                            init();
                            return;
                        }
                    }
                }
                manager.insert(NfcCard.class, card);
                Toast.makeText(BindNfcActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();

                init();
            }
        }).show();
    }

    class NfcItemAdapter extends BaseAdapter {

        private ArrayList<NfcCard> cards;

        public NfcItemAdapter(List<NfcCard> cards) {
            this.cards = new ArrayList<NfcCard>(cards);
        }

        public void remove(int position) {
            DbManager.getInstance().delete(NfcCard.class, cards.get(position));
            cards.remove(position);
            this.notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return cards.size();
        }

        @Override
        public Object getItem(int position) {
            return cards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(BindNfcActivity.this).inflate(R.layout.nfc_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.codeTxv.setText(cards.get(position).code);
            holder.nameTxv.setText(cards.get(position).name);

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.nfc_item_code)
            TextView codeTxv;
            @InjectView(R.id.nfc_item_name)
            TextView nameTxv;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

}
