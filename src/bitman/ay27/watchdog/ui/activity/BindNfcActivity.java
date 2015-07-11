package bitman.ay27.watchdog.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import bitman.ay27.watchdog.PrefUtils;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.model.NfcCard;
import bitman.ay27.watchdog.ui.activity.widget.ReadNfcDialog;
import butterknife.ButterKnife;
import butterknife.InjectView;

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
//    @InjectView(R.id.bind_nfc_new_card)
//    Button btn;
//    @InjectView(R.id.bind_nfc_hint)
//    TextView hintTxv;

    private NfcItemAdapter adapter;
//    private Toolbar.OnMenuItemClickListener menuClickListener = new Toolbar.OnMenuItemClickListener() {
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            new ReadNfcDialog(BindNfcActivity.this, BindNfcActivity.this, new ReadNfcDialog.FoundNfcCallback() {
//                @Override
//                public void onNfcFound(NfcCard card) {
//                    PrefUtils.addNfcCard(card);
//                    adapter.add(card);
//                    Toast.makeText(BindNfcActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();
//                }
//            }).show();
//            return true;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_nfc_card);
        ButterKnife.inject(this);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.current_bind_nfc_card);
        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(menuClickListener);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bind_nfc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bind_new_nfc_card) {
            new ReadNfcDialog(BindNfcActivity.this, BindNfcActivity.this, new ReadNfcDialog.FoundNfcCallback() {
                @Override
                public void onNfcFound(NfcCard card) {
                    PrefUtils.addNfcCard(card);
                    adapter.add(card);
                    Toast.makeText(BindNfcActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();
                }
            }).show();
            return true;
        }
        return false;
    }


    private void init() {
        List<NfcCard> cards = PrefUtils.getNfcCards();
        if (cards == null || cards.isEmpty()) {
            toolbar.setTitle(R.string.no_nfc_card);
            setSupportActionBar(toolbar);
        }

        listView.setAdapter(adapter = new NfcItemAdapter(cards));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(BindNfcActivity.this)
                        .setTitle(R.string.ask_delete_card)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PrefUtils.rmNfcCard(adapter.getItem(position));
                                adapter.remove(position);
                                Toast.makeText(BindNfcActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();

                                if (adapter.getCount() <= 0) {
                                    toolbar.setTitle(R.string.no_nfc_card);
                                    setSupportActionBar(toolbar);
                                }
                            }
                        })
                        .show();
            }
        });

    }

//    @OnClick(R.id.bind_nfc_new_card)
//    public void newCardClick(View view) {
//        new ReadNfcDialog(this, this, new ReadNfcDialog.FoundNfcCallback() {
//            @Override
//            public void onNfcFound(NfcCard card) {
//                PrefUtils.addNfcCard(card);
//                adapter.add(card);
//                Toast.makeText(BindNfcActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();
//            }
//        }).show();
//    }

    class NfcItemAdapter extends BaseAdapter {

        private ArrayList<NfcCard> cards;

        public NfcItemAdapter(List<NfcCard> cards) {
            if (cards == null) {
                this.cards = new ArrayList<NfcCard>();
            } else {
                this.cards = new ArrayList<NfcCard>(cards);
            }
        }

        public void remove(int position) {
            cards.remove(position);
            notifyDataSetChanged();
        }

        public void add(NfcCard card) {
            for (NfcCard card1 : cards) {
                if (card1.code.equals(card.code)) {
                    card1.name = card.name;
                    notifyDataSetChanged();
                    return;
                }
            }
            cards.add(card);
            notifyDataSetChanged();

            if (cards.size() == 1) {
                toolbar.setTitle(R.string.current_bind_nfc_card);
                setSupportActionBar(toolbar);
            }
        }

        @Override
        public int getCount() {
            return cards.size();
        }

        @Override
        public NfcCard getItem(int position) {
            return cards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(BindNfcActivity.this).inflate(R.layout.nfc_item, null);
            TextView codeTxv = (TextView) convertView.findViewById(R.id.nfc_item_code);
            TextView nameTxv = (TextView) convertView.findViewById(R.id.nfc_item_name);

            codeTxv.setText(cards.get(position).code);
            nameTxv.setText(cards.get(position).name);

            return convertView;
        }

    }

}
