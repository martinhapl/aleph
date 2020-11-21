package net.hapl.aleph.control;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.factory.CheckCtenarXMLParser;
import net.hapl.aleph.factory.CtenarXMLParser;
import net.hapl.aleph.factory.HoldReqCancelXMLParser;
import net.hapl.aleph.factory.HoldReqXMLParser;
import net.hapl.aleph.factory.ItemDataXMLParser;
import net.hapl.aleph.factory.RenewXMLParser;
import net.hapl.aleph.model.UserConfigDTO;
import net.hapl.aleph.model.CtenarDTO;
import net.hapl.aleph.model.ItemDataDTO;
import net.hapl.aleph.model.PresentDTO;
import net.hapl.aleph.model.RezervaceDTO;
import net.hapl.aleph.model.ServerConfigDTO;
import net.hapl.aleph.model.VypujckaDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// https://developers.exlibrisgroup.com/aleph/apis/aleph-x-services/

public class AlephControl {

    private static final String TAG = "AlephControl";



    private UserConfigDTO userConfigDTO;
    private ServerConfigDTO serverConfigDTO;

    private CtenarDTO ctenarDTO;

    private List<ItemDataDTO> itemDataDTOs;


    private String hAutor;



    private FavouritesRepository favouritesRepository;
    private FavouriteQueryRepository favouriteQueryRepository;
    private FindRepository findRepository;

    private static AlephControl instance;

    public static AlephControl getInstance() {
        if(instance == null) {
            instance = new AlephControl();
        }
        return instance;
    }

    public AlephControl() {
        super();



        userConfigDTO = new UserConfigDTO();
        serverConfigDTO = getServerConfig();


        ctenarDTO = new CtenarDTO();

        itemDataDTOs = new ArrayList();



        favouritesRepository = new FavouritesRepository();
        favouriteQueryRepository = new FavouriteQueryRepository();
        findRepository = new FindRepository();
    }

    public FavouritesRepository getFavouritesRepository() {
        return favouritesRepository;
    }

    public FavouriteQueryRepository getFavouriteQueryRepository() {
        return favouriteQueryRepository;
    }

    public FindRepository getFindRepository() {
        return findRepository;
    }





     public UserConfigDTO getConfig() {
        //nacteni uzivatelskoho nastaveni
        //TODO: predelat na preference
        return new UserConfigDTO("", "", "", "");
    }

    public ServerConfigDTO getServerConfig() {
      //TODO: predelat na preference
        serverConfigDTO = new ServerConfigDTO("MOBAPP","MOBAPP","https://aleph.techlib.cz/X","stk","hka50");
        return serverConfigDTO;
    }






    public CtenarDTO loadCtenar() {
        getConfig();
        if (isNetworkOnline()) {
            CtenarXMLParser parser = new CtenarXMLParser(userConfigDTO, serverConfigDTO);
            ctenarDTO = parser.getDTOs().get(0);
        }
        return ctenarDTO;
    }

    public CtenarDTO getCtenar() {
        return ctenarDTO;
    }




    public boolean checkUserConfig() {
        if (isNetworkOnline()) {
            CheckCtenarXMLParser parserCheckCtenar = new CheckCtenarXMLParser(userConfigDTO, serverConfigDTO);

            return parserCheckCtenar.getErrorAuth() == null;
        } else {
            return false;
        }
    }



    public  List<ItemDataDTO> loadItemData(PresentDTO document){
        //nacteni dat k dokumetnu (dostupnost)
        ItemDataXMLParser parserItemData = new ItemDataXMLParser(serverConfigDTO, document);
        itemDataDTOs = parserItemData.getItemDataDTOs();
        return itemDataDTOs;

    }

    public boolean createHold(ItemDataDTO holdItem){
        //vytvoreni rezervace
        HoldReqXMLParser parserHoldReq = new HoldReqXMLParser(userConfigDTO, serverConfigDTO, holdItem);
        if (!(parserHoldReq.getReply()==null)){
            return parserHoldReq.getReply().equals("ok");
        } else {
            return false;
        }
    }

    public boolean cancelHold(RezervaceDTO holdItem){
        //zruseni rezervace
        HoldReqCancelXMLParser parserHoldReqCancel = new HoldReqCancelXMLParser(userConfigDTO, serverConfigDTO, holdItem);
        if (!(parserHoldReqCancel.getReply()==null)){
            return parserHoldReqCancel.getReply().equals("ok");
        } else {
            return false;
        }
    }


    public boolean renewLoan(VypujckaDTO pujcenaKniha){
        //obnoveni vypujcky
        RenewXMLParser parserRenew = new RenewXMLParser(userConfigDTO, serverConfigDTO, pujcenaKniha);
        return parserRenew.getReply().equals("ok");
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) MainActivity.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }





    public String createSFX(PresentDTO aktualniKniha) {
        String urlSFX = null;
        String sid = null;
        String genre = null;
        String isbn = null;
        String issn = null;
        String date = null;
        String volume = null;
        String issue = null;
        String spage = null;
        String aulast = null;
        String aufirst = null;
        String title = null;
        String atitle = null;

        urlSFX = "http://sfx.is.cuni.cz/sfxlcl3?";
        //http://sfx.techlib.cz/sfxlcl41
        if (date == null) {
            urlSFX = urlSFX + "&sfx.ignore_date_threshold=1";
        }
        try {

            if (aktualniKniha.getIsbn() != null) {
                isbn = URLEncoder.encode(aktualniKniha.getIsbn(), "UTF-8");
                urlSFX = urlSFX + "&isbn=" + isbn;
            }
            if (aktualniKniha.getImprintRok() != null) {
                date = URLEncoder.encode(aktualniKniha.getImprintRok(), "UTF-8");
                urlSFX = urlSFX + "&date=" + date;
            }
            if (aktualniKniha.getIsbn() != null) {
                issn = URLEncoder.encode(aktualniKniha.getIsbn(), "UTF-8");
                urlSFX = urlSFX + "&issn=" + issn;
            }
            if (aktualniKniha.getNazev() != null) {
                title = URLEncoder.encode(aktualniKniha.getNazev(), "UTF-8");
                urlSFX = urlSFX + "&title=" + title;
            }
            if (aktualniKniha.getAutor() != null) {
                aulast = aktualniKniha.getAutor();
                if (aulast.indexOf(",") > 0) {
                    aulast = aulast.substring(0,aulast.indexOf(","));
                }
                aulast = URLEncoder.encode(aulast, "UTF-8");
                urlSFX = urlSFX + "&aulast=" + aulast;
            }
            urlSFX = urlSFX + "&sid=" + MainActivity.SFX_SID;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return urlSFX;
    }

    public String createMessageToSendByEmail(int recordId, int typeRecord) {
        PresentDTO presentDTO;
        String messageToSend;

        if(typeRecord == MainActivity.DETAIL_SEARCH_STATE) {
            presentDTO = AlephControl.getInstance().getFindRepository().getPresentDTOs().get(recordId);
        } else {
            presentDTO = AlephControl.getInstance().getFavouritesRepository().getFavourite().get(recordId);
        }

        messageToSend = MainActivity.getContext().getString(R.string.email_share_content) + "\n\n";
        if(presentDTO.getNazev() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.titul) + ": " + presentDTO.getNazev() + "\n";
        }
        if(presentDTO.getAutor() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.author) + ": " + presentDTO.getAutor() + "\n";
        }
        if(presentDTO.getImprint() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.nakladatel_udaje) + ": " + presentDTO.getImprint() + "\n";
        }
        if(presentDTO.getVydani() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.vydani) + ": " + presentDTO.getVydani() + "\n";
        }
        if(presentDTO.getPopis() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.fyzicky_popis) + ": " + presentDTO.getPopis() + "\n";
        }
        if(presentDTO.getPoznamka() != null && presentDTO.getPoznamka().size() != 0) {
            messageToSend += MainActivity.getContext().getString(R.string.poznamky);

            for(String poznamka : presentDTO.getPoznamka()) {
                messageToSend += ": " + poznamka + "\n";
            }
        }
        if(presentDTO.getPredmet() != null && presentDTO.getPredmet().size() != 0) {
            messageToSend += MainActivity.getContext().getString(R.string.predmet);

            for(String predmet : presentDTO.getPoznamka()) {
                messageToSend += ": " + predmet + "\n";
            }
        }
        if(presentDTO.getIsbn() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.isbn) + ": " + presentDTO.getIsbn() + "\n";
        }
        if(presentDTO.getJazyk() != null) {
            messageToSend += MainActivity.getContext().getString(R.string.jazyk) + ": " + presentDTO.getJazyk() + "\n";
        }

        return messageToSend;
    }
}