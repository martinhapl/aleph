package net.hapl.aleph.control;

public interface FavoriteComm {

    public void sendDataFromFavoriteQueryFragment(String query);

    public void showDetailFromFavoriteRecordFragment(int position);

    public void updateFromFavoriteAdapters();

}
