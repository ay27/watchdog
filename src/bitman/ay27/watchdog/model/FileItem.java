package bitman.ay27.watchdog.model;

import java.util.ArrayList;

/**
 * Created by ay27 on 15-7-9.
 */
public class FileItem {
    public String path;
    public String name;
    public boolean isFolder;
    private ArrayList<FileItem> items = new ArrayList<FileItem>();

    public FileItem(String path, String name, boolean isFolder) {
        this.path = path;
        this.name = name;
        this.isFolder = isFolder;
    }

    public void add(String path, String name, boolean isFolder) {
        items.add(new FileItem(path, name, isFolder));
    }

    public void remove(String path, String name) {
        for (FileItem item : items) {
            if (item.name.equals(name) && item.path.equals(path)) {
                items.remove(item);
                return;
            }
        }
    }

    public void add(FileItem item) {
        items.add(item);
    }
}
