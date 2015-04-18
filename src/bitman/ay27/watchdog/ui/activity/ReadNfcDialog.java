package bitman.ay27.watchdog.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.*;
import bitman.ay27.watchdog.R;
import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.NfcCard;
import bitman.ay27.watchdog.utils.Utils;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/17.
 */
public class ReadNfcDialog extends Dialog implements NfcAdapter.ReaderCallback {
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    View findingPanel;
    View foundPanel;
    TextView codeTxv;
    EditText nameEdt;
    ProgressBar progressBar;
    Button saveCardBtn;
    private NfcAdapter adapter;
    private Activity activity;
    private String cardCode;
    private FoundNfcCallback callback;
    public interface FoundNfcCallback {
        public void onNfcFound(NfcCard card);
    }

    public ReadNfcDialog(Context context, Activity activity, FoundNfcCallback callback) {
        super(context);
        this.activity = activity;
        this.callback  =callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.read_nfc_dialog);
        findViews();

        foundPanel.setVisibility(View.GONE);
        findingPanel.setVisibility(View.VISIBLE);

        startFinding();

    }

    private void findViews() {
        findingPanel = findViewById(R.id.read_nfc_dialog_finding);
        foundPanel = findViewById(R.id.read_nfc_dialog_found);
        codeTxv = (TextView) findViewById(R.id.read_nfc_dialog_code);
        nameEdt = (EditText) findViewById(R.id.read_nfc_dialog_name);
        saveCardBtn = (Button) findViewById(R.id.read_nfc_dialog_save);
        progressBar = (ProgressBar) findViewById(R.id.read_nfc_dialog_progressbar);
    }

    private void startFinding() {
        adapter = NfcAdapter.getDefaultAdapter(getContext());
        if (adapter == null) {
            Toast.makeText(getContext(), R.string.not_support_nfc, Toast.LENGTH_SHORT).show();
            dismiss();
        }
        adapter.enableReaderMode(activity, this, READER_FLAGS, null);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        cardCode = Utils.ByteArrayToHexString(tag.getId());
        handler.sendMessage(new Message());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            findingPanel.setVisibility(View.GONE);
            foundPanel.setVisibility(View.VISIBLE);

            codeTxv.setText(cardCode);
            saveCardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onNfcFound(new NfcCard(nameEdt.getText().toString(), cardCode));
                    finish();
                }
            });
        }
    };

    private void finish() {
        adapter.disableReaderMode(activity);
        dismiss();
    }
}
