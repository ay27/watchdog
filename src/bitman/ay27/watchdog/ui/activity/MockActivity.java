package bitman.ay27.watchdog.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bitman.ay27.watchdog.R;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/5/2.
 */
public class MockActivity extends ActionBarActivity {


    String[] data = new String[]{
            "我的图片",
            "我的视频",
            "我的文档",
            "file_pack",
            "backup",
            "file.txt                       30B",
            "test.txt                      1KB",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyguard_img);
//        setContentView(R.layout.file);
//
//        ListView listView = (ListView) findViewById(R.id.file_list);
//
//        listView.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return data.length;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return data[position];
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                convertView = LayoutInflater.from(MockActivity.this).inflate(R.layout.file_item, null);
//                TextView tv = (TextView)convertView.findViewById(R.id.file_item_name);
//                ImageView iv = (ImageView)convertView.findViewById(R.id.file_item_img);
//                tv.setText(data[position]);
//                if (position >= 5) {
//                    iv.setImageResource(R.drawable.ic_action_view_as_list);
//                }
//
//                return convertView;
//            }
//        });


//        Toolbar bar = (Toolbar)findViewById(R.id.trace_toolbar);
//        bar.setTitle("追踪");
//        bar.setTitleTextColor(Color.WHITE);
//        bar.setBackgroundColor(Color.argb(255, 30,30,30));
//        setSupportActionBar(bar);
//
//        AttitudeView view = (AttitudeView)findViewById(R.id.attitude_view);
//        view.SetAttitudeDeg(30, 50, 20);
////
////        final View view = findViewById(R.id.mock_panel);
//
//        View v = findViewById(R.id.laptop_enter_danger);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MockActivity.this, DangerActivity.class));
//            }
//        });
    }
}
