package net.hapl.aleph.control;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.model.PresentDTO;
import net.hapl.aleph.model.PresentDTOs;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavouritesRepository {

    private List<PresentDTO> favouriteDTOs;

    public FavouritesRepository() {
        favouriteDTOs = new ArrayList<>();
    }

    public List<PresentDTO> getFavourite() {
        loadFavourites();
        return favouriteDTOs;
    }

    public boolean addFavourite(PresentDTO favouriteDTO) {
        loadFavourites();

        //pridani polozky do oblibenych
        boolean neniVOblibenych = true;
        for (int j = 0; j < favouriteDTOs.size(); j++) {
            if (favouriteDTOs.get(j).getDoc_number().equals(favouriteDTO.getDoc_number())) {
                neniVOblibenych = false;
            }
        }
        if (neniVOblibenych) {
            favouriteDTOs.add(favouriteDTO);
            saveFavourites();
        }

        return neniVOblibenych;

    }

    public void removeFavourite(int position) {
        favouriteDTOs.remove(position);
        saveFavourites();
    }


    public void loadFavourites() {
        //nacteni ulozenych oblibenich z favourite.xml do DTOs

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath(), "favourite.xml");

        PresentDTOs presentDTOsTest = new PresentDTOs();

        if (source.exists()) {
            try {
                if (!(source.length() == 0)) {
                    presentDTOsTest = serializer.read(PresentDTOs.class, source);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favouriteDTOs = presentDTOsTest.getPresentDTOs();
    }

    public void saveFavourites() {
        //ulozeni oblibenych do favourite.xml

        File result = new File(MainActivity.DIRECTORY.getPath(), "favourite.xml");

        PresentDTOs presentDTOsTest = new PresentDTOs();
        presentDTOsTest.setPresentDTOs(favouriteDTOs);

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);

        if (result.exists()) {
            try {
                serializer.write(presentDTOsTest, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            try {
                result.createNewFile();
                serializer.write(presentDTOsTest, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean deleteFavourites() {

        //smazani oblibenych

        Format format = new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Serializer serializer = new Persister(format);
        File source = new File(MainActivity.DIRECTORY.getPath()+File.separator+"favourite.xml");

        PresentDTOs presentDTOsTmp = new PresentDTOs();

        if (source.exists()) {
            try {
                if (!(source.length() == 0)) {
                    source.delete();
                    source.createNewFile();
                    serializer.write(presentDTOsTmp, source);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        favouriteDTOs = presentDTOsTmp.getPresentDTOs();
        return true;
    }

}
