//package bitman.ay27.watchdog.ui.activity;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.*;
//import bitman.ay27.watchdog.PrefUtils;
//import bitman.ay27.watchdog.R;
//import bitman.ay27.watchdog.db.model.NfcCard;
//import bitman.ay27.watchdog.ui.activity.widget.ReadNfcDialog;
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Proudly to user Intellij IDEA.
// * Created by ay27 on 15/4/7.
// */
//public class NfcManageActivity extends ActionBarActivity {
//
//    @InjectView(R.id.nfc_manager_list)
//    ListView listView;
////    @InjectView(R.id.bind_nfc_new_card)
////    Button btn;
////    @InjectView(R.id.bind_nfc_hint)
////    TextView hintTxv;
//
//    private NfcItemAdapter adapter;
////    private Toolbar.OnMenuItemClickListener menuClickListener = new Toolbar.OnMenuItemClickListener() {
////        @Override
////        public boolean onMenuItemClick(MenuItem menuItem) {
////            new ReadNfcDialog(BindNfcActivity.this, BindNfcActivity.this, new ReadNfcDialog.FoundNfcCallback() {
////                @Override
////                public void onNfcFound(NfcCard card) {
////                    PrefUtils.addNfcCard(card);
////                    adapter.add(card);
////                    Toast.makeText(BindNfcActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();
////                }
////            }).show();
////            return true;
////        }
////    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.nfc_manager);
//        ButterKnife.inject(this);
//
//        init();
//    }
//
//    @OnClick(R.id.nfc_manager_new_card_btn)
//    public void newCardClick(View view) {
//        new ReadNfcDialog(NfcManageActivity.this, NfcManageActivity.this, new ReadNfcDialog.FoundNfcCallback() {
//            @Override
//            public void onNfcFound(NfcCard card) {
//                PrefUtils.addNfcCard(card);
//                adapter.add(card);
//                Toast.makeText(NfcManageActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();
//            }
//        }).show();
//    }
//
//
//    private void init() {
//        List<NfcCard> cards = PrefUtils.getNfcCards();
//
//        listView.setAdapter(adapter = new NfcItemAdapter(cards));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                new AlertDialog.Builder(NfcManageActivity.this)
//                        .setMessage(R.string.ask_delete_card)
//                        .setNegativeButton(R.string.cancel, null)
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                PrefUtils.rmNfcCard(adapter.getItem(position));
//                                adapter.remove(position);
//                                Toast.makeText(NfcManageActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
//
//                            }
//                        })
//                        .show();
//            }
//        });
//
//    }
//
////    @OnClick(R.id.bind_nfc_new_card)
////    public void newCardClick(View view) {
////        new ReadNfcDialog(this, this, new ReadNfcDialog.FoundNfcCallback() {
////            @Override
////            public void onNfcFound(NfcCard card) {
////                PrefUtils.addNfcCard(card);
////                adapter.add(card);
////                Toast.makeText(BindNfcActivity.this, R.string.save_ok, Toast.LENGTH_SHORT).show();
////            }
////        }).show();
////    }
//
//    class NfcItemAdapter extends BaseAdapter {
//
//        private ArrayList<NfcCard> cards;
//
//        public NfcItemAdapter(List<NfcCard> cards) {
//            if (cards == null) {
//                this.cards = new ArrayList<NfcCard>();
//            } else {
//                this.cards = new ArrayList<NfcCard>(cards);
//            }
//        }
//
//        public void remove(int position) {
//            cards.remove(position);
//            notifyDataSetChanged();
//        }
//
//        public void add(NfcCard card) {
//            for (NfcCard card1 : cards) {
//                if (card1.code.equals(card.code)) {
//                    card1.name = card.name;
//                    notifyDataSetChanged();
//                    return;
//                }
//            }
//            cards.add(card);
//            notifyDataSetChanged();
//
//        }
//
//        @Override
//        public int getCount() {
//            return cards.size();
//        }
//
//        @Override
//        public NfcCard getItem(int position) {
//            return cards.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            convertView = LayoutInflater.from(NfcManageActivity.this).inflate(R.layout.nfc_item, null);
//            TextView codeTxv = (TextView) convertView.findViewById(R.id.nfc_item_code);
//            TextView nameTxv = (TextView) convertView.findViewById(R.id.nfc_item_name);
//
//            codeTxv.setText(cards.get(position).code);
//            nameTxv.setText(cards.get(position).name);
//
//            return convertView;
//        }
//
//    }
//
//}
