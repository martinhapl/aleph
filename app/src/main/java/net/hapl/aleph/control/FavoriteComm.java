package net.hapl.aleph.control;

public interface FavoriteComm {

    void sendDataFromFavoriteQueryFragment(String query);

    void showDetailFromFavoriteRecordFragment(int position);

    void updateFromFavoriteAdapters();

}
