package bitman.ay27.watchdog.ui.new_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 15-7-17.
 */
public class MainActivity extends Activity {

    private static final String TAG = "Main_Activity";
    @InjectView(R.id.main__gridview)
    DynamicGridView gridView;

    private List<String> names = Arrays.asList("云端服务", "刷机锁", "近身防盗", "屏幕锁定", "SD卡加密", "应用设置", "USB锁", "DogWatch", "密码设置");
    private List<Integer> drawables = Arrays.asList(
            R.drawable.ic_perm_identity_grey600,
            R.drawable.shuaji,
            R.drawable.ic_nfc_grey600_48dp,
            R.drawable.ic_screen_lock_portrait_grey600_48dp,
            R.drawable.ic_sd_card_grey600_48dp,
            R.drawable.ic_settings_grey600_48dp,
            R.drawable.ic_usb_grey600_48dp,
            R.drawable.ic_watch_black_48dp,
            R.drawable.ic_vpn_key_black_48dp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_);
        ButterKnife.inject(this);

        ArrayList<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 9; i++) {
            items.add(new Item(drawables.get(i), names.get(i), i));
        }

        gridView.setAdapter(new CheeseDynamicAdapter(this, items, 3));

        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.d(TAG, "drag started at position " + position);
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d(TAG, String.format("drag item position changed from %d to %d", oldPosition, newPosition));
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) gridView.getAdapter().getItem(position);
                Intent intent = new Intent();
                switch (item.id) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                }
                Toast.makeText(MainActivity.this, ""+item.id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (gridView.isEditMode()) {
            gridView.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }

    public static class CheeseDynamicAdapter extends BaseDynamicGridAdapter {
        public CheeseDynamicAdapter(Context context, List<?> items, int columnCount) {
            super(context, items, columnCount);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheeseViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, null);
                holder = new CheeseViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheeseViewHolder) convertView.getTag();
            }
            holder.build(position);
            return convertView;
        }

        private class CheeseViewHolder {
            private TextView titleText;
            private ImageView image;

            private CheeseViewHolder(View view) {
                titleText = (TextView) view.findViewById(R.id.main_item_txv);
                image = (ImageView) view.findViewById(R.id.main_item_img);
            }

            void build(int position) {
                Item item = (Item) getItem(position);
                titleText.setText(item.name);
                image.setImageResource(item.drawable);
            }
        }
    }

    private class Item {
        String name;
        int drawable;
        int id;

        public Item(int drawable, String name, int id) {
            this.drawable = drawable;
            this.name = name;
            this.id = id;
        }
    }
}
