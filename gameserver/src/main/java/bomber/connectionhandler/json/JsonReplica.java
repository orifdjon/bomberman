package bomber.connectionhandler.json;


import com.fasterxml.jackson.annotation.JsonGetter;


public class JsonReplica {

    private final Topic topic = Topic.REPLICA;
    private DataReplica data = new DataReplica();

    public JsonReplica() {
    }

    public Topic getTopic() {
        return topic;
    }

    @JsonGetter("data")
    public DataReplica getData() {
        return data;
    }
    
    public void setData(DataReplica data) {
        this.data = data;
    }


}


