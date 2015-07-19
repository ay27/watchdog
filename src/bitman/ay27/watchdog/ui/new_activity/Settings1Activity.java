package bitman.ay27.watchdog.ui.new_activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import bitman.ay27.watchdog.R;
import com.kenumir.materialsettings.MaterialSettingsActivity;
import com.kenumir.materialsettings.items.*;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;

public class Settings1Activity extends MaterialSettingsActivity {

	CheckboxItem item;
	PreferencesStorageInterface storageInterface;

	boolean reEnter;
	private CheckboxItem.OnCheckedChangeListener checkListener = new CheckboxItem.OnCheckedChangeListener() {
		@Override
		public void onCheckedChange(CheckboxItem item, boolean isChecked) {
			if (reEnter) {
				reEnter = false;
				return;
			}
			storageInterface.save("keykey", isChecked);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		addItem(new HeaderItem(getFragment()).setTitle("Sample title 1"));
//		item = new CheckboxItem(getFragment(), "key1");
//		item.setTitle("Checkbox item 1");
//		item.setSubtitle("Subtitle text 1");
//		item.setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
//				Toast.makeText(Settings1Activity.this, "CHECKED: " + isChecked, Toast.LENGTH_SHORT).show();
//			}
//		});
//		addItem(item);
		addItem(new DividerItem(getFragment()));
		addItem(new SwitcherItem(getFragment(), "key1a").setTitle("Switcher item 3 - no subtitle"));
		addItem(new DividerItem(getFragment()));
		addItem(new SwitcherItem(getFragment(), "key1b").setTitle("Switcher item 3").setSubtitle("With subtitle"));
		addItem(new DividerItem(getFragment()));



		storageInterface = new PreferencesStorageInterface(this);
		reEnter = false;


		addItem(item = new CheckboxItem(getFragment(), "key2").setTitle("Checkbox item 2").setSubtitle("Subtitle text 2 with long text and more txt and more and more ;-)").setDefaultValue(false).setOnCheckedChangeListener(checkListener));
		addItem(new DividerItem(getFragment()));
		addItem(new CheckboxItem(getFragment(), "key1c").setTitle("Checkbox item 3 - no subtitle"));
		addItem(new DividerItem(getFragment()));

		addItem(new TextItem(getFragment(), "key3").setTitle("Simple text item 1").setSubtitle("Subtitle of simple text item 1").setOnclick(new TextItem.OnClickListener() {
			@Override
			public void onClick(TextItem v) {
				Toast.makeText(Settings1Activity.this, "Clicked", Toast.LENGTH_SHORT).show();
			}
		}));
		addItem(new HeaderItem(getFragment()).setTitle("Sample title 2"));
		addItem(new TextItem(getFragment(), "key4").setTitle("Simple text item 2").setSubtitle("Subtitle of simple text item 2").setOnclick(new TextItem.OnClickListener() {
			@Override
			public void onClick(TextItem v) {
				Toast.makeText(Settings1Activity.this, "Clicked 2", Toast.LENGTH_SHORT).show();
			}
		}));
		addItem(new DividerItem(getFragment()));
		addItem(new TextItem(getFragment(), "key5").setTitle("Simple text item 3 - no subtitle"));
		addItem(new DividerItem(getFragment()));
		addItem(new TextItem(getFragment(), "key5a").setTitle("Simple text item with icon - no subtitle").setIcon(R.drawable.ic_check_circle_grey600_24dp));
		addItem(new DividerItem(getFragment()));
		addItem(new TextItem(getFragment(), "key5b").setTitle("Simple text item with icon - no subtitle").setSubtitle("Subtitle of item with icon").setIcon(R.drawable.ic_check_circle_grey600_24dp));
		addItem(new HeaderItem(getFragment()).setTitle("Same usage with dialogs"));
//		addItem(new TextItem(getFragment(), "key6").setTitle("Simple message dialog").setSubtitle("Clck to show message and change subtext").setOnclick(new TextItem.OnClickListener() {
//			@Override
//			public void onClick(TextItem item) {
//				SampleDialog.newInstance().show(getSupportFragmentManager(), "dialog1");
//			}
//		}));
	}

	@Override
	protected void onResume() {
		super.onResume();


		item.updateChecked(storageInterface.load("keykey", false));
	}

	@Override
	public StorageInterface initStorageInterface() {
		return new PreferencesStorageInterface(this);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_settings1, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//
//		if (id == R.id.action_settings) {
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	public void onOkClick() {
//		((TextItem)getItem("key6")).updateSubTitle("Updated");
//		getStorageInterface().save("key6", true);
//	}
}
