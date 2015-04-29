package bitman.ay27.watchdog.utils;

import bitman.ay27.watchdog.db.DbManager;
import bitman.ay27.watchdog.db.model.WatchCat;
import bitman.s117.libwatchcat.WatchCat_Controller;
import bitman.s117.libwatchcat.WatchCat_Controller_Impl;

import java.util.List;

/**
 * Proudly to user Intellij IDEA.
 * Created by ay27 on 15/4/20.
 */
public class WatchCatWorker {
    private WatchCat_Controller wc_ctl;

    public WatchCatWorker() {
        wc_ctl = new WatchCat_Controller_Impl();
    }

    public void check_load() {
        DbManager manager = DbManager.getInstance();
        List<WatchCat> tmp = manager.query(WatchCat.class);
        if (tmp==null || tmp.size()==0) {
            return;
        }
        WatchCat cat = tmp.get(0);
        if (cat.autoLoadBCPT) {
            wc_ctl.loadBCPT();
        }
        if (cat.autoLoadFSP) {
            wc_ctl.loadFsProtector();
        }

    }

}
