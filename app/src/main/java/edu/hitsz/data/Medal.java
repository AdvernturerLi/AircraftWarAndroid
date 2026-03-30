package edu.hitsz.data;

import java.io.Serializable;

public class Medal implements Serializable {
    private final String name;
    private final String description;
    private final int iconResId;
    private boolean unlocked;

    public Medal(String name, String description, int iconResId, boolean unlocked) {
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
        this.unlocked = unlocked;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
