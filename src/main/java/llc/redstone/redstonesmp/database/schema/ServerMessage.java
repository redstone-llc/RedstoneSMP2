package llc.redstone.redstonesmp.database.schema;

import eu.pb4.placeholders.api.node.TextNode;

public class ServerMessage {
    public String message;
    public String server;

    public ServerMessage() {
    }

    public ServerMessage(String message, String server) {
        this.message = message;
        this.server = server;
    }

    public String getMessage() {
        return message;
    }

    public String getServer() {
        return server;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
