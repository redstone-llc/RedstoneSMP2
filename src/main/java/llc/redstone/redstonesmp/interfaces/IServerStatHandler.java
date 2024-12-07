package llc.redstone.redstonesmp.interfaces;

public interface IServerStatHandler {
    void writeStatData(String statData);
    String readStatData();
}
