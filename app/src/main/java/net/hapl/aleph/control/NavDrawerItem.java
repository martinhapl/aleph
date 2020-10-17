package net.hapl.aleph.control;

public class NavDrawerItem {

    private String title;
    private int icon;
    private int color;

    public NavDrawerItem(String title, int icon, int color){
        this.title = title;
        this.icon = icon;
        this.color = color;
    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }


    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
