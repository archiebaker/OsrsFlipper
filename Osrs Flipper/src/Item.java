import java.util.Date;

public class Item implements Comparable<Item> {
    // constructor
    private String ItemName;
    private int LowPrice;
    private int HighPrice;
    private int Profit;
    private double InstantROI;
    private Date LowTime;
    private Date HighTime;
    private int AvgProfit5m;
    private double FiveMinROI;
    private int HighPriceAverage5m;
    private int LowPriceAverage5m;
    private int HighPriceVolume5m;
    private int LowPriceVolume5m;
    private int TradeLimitAmount;
    private double TradeScore;

    public int getAvgProfit5m() {
        return AvgProfit5m;
    }
    public int getHighPriceAverage5m() {
        return HighPriceAverage5m;
    }
    public int getLowPriceAverage5m() {
        return LowPriceAverage5m;
    }


    public int getHighPriceVolume5m() {
        return HighPriceVolume5m;
    }



    public int getLowPriceVolume5m() {
        return LowPriceVolume5m;
    }



    public int getTradeLimitAmount() {
        return TradeLimitAmount;
    }



    public double getInstantROI() {
        return InstantROI;
    }

    public double getFiveMinROI() {
        return FiveMinROI;
    }

    public Item(String name, int lowPrice, int highPrice, int profit, double instantROI, Date lowTime, Date highTime, int avgProfit5m, double fiveMinROI, int highPriceAverage5m, int lowPriceAverage5m, int highPriceVolume5m, int lowPriceVolume5m, int tradeLimitAmount, double tradeScore) {
        this.setItemName(name);
        this.LowPrice = lowPrice;
        this.HighPrice = highPrice;
        this.Profit = profit;
        this.InstantROI = instantROI;
        this.LowTime = lowTime;
        this.HighTime = highTime;
        this.AvgProfit5m = avgProfit5m;
        this.FiveMinROI = fiveMinROI;
        this.HighPriceAverage5m = highPriceAverage5m;
        this.LowPriceAverage5m = lowPriceAverage5m;
        this.HighPriceVolume5m = highPriceVolume5m;
        this.LowPriceVolume5m = lowPriceVolume5m;
        this.TradeLimitAmount = tradeLimitAmount;
        this.TradeScore = tradeScore;
    }


    public int getLowPrice() {
        return LowPrice;
    }
    public int getHighPrice() {
        return HighPrice;
    }
    public int getProfit() {
        return Profit;
    }
    public Date getLowTime() {
        return LowTime;
    }
    public Date getHighTime() {
        return HighTime;
    }
    public double getTradeScore() {return TradeScore; }
    public void setTradeScore(double tradeScore) { this.TradeScore = tradeScore; }






    @Override
    public int compareTo(Item o) {
        return 0;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        this.ItemName = itemName;
    }
}
