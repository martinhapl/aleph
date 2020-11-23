package net.hapl.aleph.control;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.model.QueryDTO;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavouriteQueryRepository {

    private List<String> favouriteQuery;

    public FavouriteQueryRepository() {
        favouriteQuery = new ArrayList<>();
    }

    public List<String> getFavouriteQuery() {
        loadFavoritesQuery();
        return this.favouriteQuery;
    }

    public Boolean addFavouriteQuery(String query) {
        loadFavoritesQuery();

        boolean neniVOblibenych;
        neniVOblibenych = true;
        for (int j=0; j < this.favouriteQuery.size(); j++){
            if (this.favouriteQuery.get(j).equals(query)) {
                neniVOblibenych = false;
            }
        }
        if (neniVOblibenych) {
            this.favouriteQuery.add(query);
            saveFavoritesQuery();
        }

        return neniVOblibenych;
    }


    public void removeFavouriteQuery(int position) {
        this.favouriteQuery.remove(position);
        saveFavoritesQuery();
    }

    public void loadFavoritesQuery() {
        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath(), "favouriteQuery.xml");

        List<String> queryList = new ArrayList<>();

        if (source.exists()) {
            try {
                queryList = serializer.read(QueryDTO.class, source).getQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.favouriteQuery = queryList;
    }

    public void saveFavoritesQuery() {
        File result = new File(MainActivity.DIRECTORY.getPath(), "favouriteQuery.xml");

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);

        QueryDTO queryDTO = new QueryDTO(this.favouriteQuery);

        if (result.exists()) {
            try {
                serializer.write(queryDTO, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                result.createNewFile();
                serializer.write(queryDTO, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
