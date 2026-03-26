package edu.hitsz.data;

import java.io.Serializable;

public class Medal implements Serializable {
    private final String name;
    private final String description;
    private final int iconResId;

    public Medal(String name, String description, int iconResId) {
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
}
