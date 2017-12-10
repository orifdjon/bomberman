package bomber.connectionhandler.json;

import bomber.connectionhandler.PlayerAction;

public class DataHandleInputJson {
    PlayerAction.EventType eventType;

    public DataHandleInputJson() {
    }

    public DataHandleInputJson(PlayerAction.EventType eventType) {
        this.eventType = eventType;
    }

    public PlayerAction.EventType getEventType() {
        return eventType;
    }

    public void setEventType(PlayerAction.EventType eventType) {
        this.eventType = eventType;
    }


}
