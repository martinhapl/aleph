package net.hapl.aleph.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(strict = false, name="presents")
public class PresentDTOs {

    @ElementList(inline = true)
    private List<PresentDTO> presentDTOs;

    public PresentDTOs() {
        super();
        presentDTOs = new ArrayList<PresentDTO>();
    }

    public List<PresentDTO> getPresentDTOs() {
        return presentDTOs;
    }

    public void setPresentDTOs(List<PresentDTO> presentDTOs) {
        this.presentDTOs = presentDTOs;
    }
}
