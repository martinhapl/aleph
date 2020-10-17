package net.hapl.aleph.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="query")
public class QueryDTO {

    @ElementList(inline=true, entry="string")
    private List<String> query;

    public QueryDTO() {
        super();
    }

    public QueryDTO(List<String> queries) {
        this.query = queries;
    }

    public List<String> getQuery() {
        return this.query;
    }

    @Override
    public String toString() {
        return "Dotaz: " + this.query;
    }
}
