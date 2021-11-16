import java.util.Date;

public class Items implements Comparable<Items> {
    // constructor
    private String name;
    private int lowPrice;
    private int highPrice;
    private int profit;
    private double instantROI;
    private Date lowTime;
    private Date highTime;
    private int avgProfit5m;
    private double fiveMinROI;
    private int highPriceAverage5m;
    private int lowPriceAverage5m;
    private int highPriceVolume5m;
    private int lowPriceVolume5m;
    private int tradeLimitAmount;
    private double tradeScore;

    public int getAvgProfit5m() {
        return avgProfit5m;
    }
    public int getHighPriceAverage5m() {
        return highPriceAverage5m;
    }
    public int getLowPriceAverage5m() {
        return lowPriceAverage5m;
    }


    public int getHighPriceVolume5m() {
        return highPriceVolume5m;
    }



    public int getLowPriceVolume5m() {
        return lowPriceVolume5m;
    }



    public int getTradeLimitAmount() {
        return tradeLimitAmount;
    }



    public double getInstantROI() {
        return instantROI;
    }

    public double getFiveMinROI() {
        return fiveMinROI;
    }

    public Items(String name, int lowPrice, int highPrice, int profit, double instantROI, Date lowTime, Date highTime, int avgProfit5m, double fiveMinROI, int highPriceAverage5m, int lowPriceAverage5m, int highPriceVolume5m, int lowPriceVolume5m, int tradeLimitAmount, double tradeScore) {
        this.setName(name);
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.profit = profit;
        this.instantROI = instantROI;
        this.lowTime = lowTime;
        this.highTime = highTime;
        this.avgProfit5m = avgProfit5m;
        this.fiveMinROI = fiveMinROI;
        this.highPriceAverage5m = highPriceAverage5m;
        this.lowPriceAverage5m = lowPriceAverage5m;
        this.highPriceVolume5m = highPriceVolume5m;
        this.lowPriceVolume5m = lowPriceVolume5m;
        this.tradeLimitAmount = tradeLimitAmount;
        this.tradeScore = tradeScore;
    }


    public int getLowPrice() {
        return lowPrice;
    }
    public int getHighPrice() {
        return highPrice;
    }
    public int getProfit() {
        return profit;
    }
    public Date getLowTime() {
        return lowTime;
    }
    public Date getHighTime() {
        return highTime;
    }
    public double getTradeScore() {return tradeScore; }
    public void setTradeScore(double tradeScore) { this.tradeScore = tradeScore; }






    @Override
    public int compareTo(Items o) {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
