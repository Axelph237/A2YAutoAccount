package org.example;

import org.json.simple.JSONObject;

public class MenuItem {

    final public String title;
    final public JSONObject data;

    public MenuItem(String title, JSONObject data )
    {
        this.title = title;
        this.data = data;
    }

    @Override
    public String toString() {
        return title;
    }
}
