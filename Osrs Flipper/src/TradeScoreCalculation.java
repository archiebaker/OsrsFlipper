import java.time.Duration;
import java.util.Date;

public class TradeScoreCalculation {

    final int CONVERT_MILLISECONDS_TO_SECONDS = 1000;
    final double TIME_DIFFERENCE_BETWEEN_ITEM_LONGEST_TRADE_AND_NOW_MULTIPLIER = 0.6;
    final double TIME_DIFFERENCE_BETWEEN_ITEM_LONGEST_BUY_AND_SELL_MULTIPLIER = 1;
    final int DISREGARD_TIME_DIFFERENCE_UNDER_THIS_AMOUNT = 10;
    final int ADD_TO_PRICE_TO_BEAT_MARGIN = 1;

    final int NO_SCORE_PENALTY_IF_ITEM_TOTAL_COST_UNDER_THIS_PERCENT_OF_CASH_STACK = 25;
    final int ITEM_TOO_EXPENSIVE_IF_TOTAL_COST_OVER_THIS_PERCENT_OVER_CASH_STACK = 1000;

    final int ITEM_EXTREMELY_HEAVY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK = 500;
    final int ITEM_VERY_HEAVY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK = 200;
    final int ITEM_HEAVY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK = 100;
    final int ITEM_MEDIUM_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK = 50;
    final int ITEM_SMALL_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK = 40;
    final int ITEM_TINY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK = 25;

    final double EXTREMELY_HEAVY_MULTIPLIER_PENALTY_AMOUNT = 0.1;
    final double VERY_HEAVY_MULTIPLIER_PENALTY_AMOUNT = 0.55;
    final double HEAVY_MULTIPLIER_PENALTY_AMOUNT = 0.6;
    final double MEDIUM_MULTIPLIER_PENALTY_AMOUNT = 0.7;
    final double SMALL_MULTIPLIER_PENALTY_AMOUNT = 0.8;
    final double TINY_MULTIPLIER_PENALTY_AMOUNT = 0.9;

    public void calculateTradeScore (Item item, double usersCashAmount)
    {


        double currentTradeScore = 0;
        Date lowTime = item.getLowTime();
        Date highTime = item.getHighTime();
        Duration timeDifferenceBetweenBuyAndSell = Duration.between(lowTime.toInstant(), highTime.toInstant());
        //Using Absolute since it doesn't matter which time value is bigger, just want to know the difference
        double timeDifferenceInSecondsBetweenBuyAndSellInSeconds = Math.abs(timeDifferenceBetweenBuyAndSell.toMillis()/1000);

        Date earlierDate = new Date();
        Date currentDateTime = new Date();
        if (lowTime.before(highTime) || lowTime.equals(highTime))
        {
            earlierDate = lowTime;
        }
        else if (highTime.before(lowTime))
        {
            earlierDate = highTime;
        }
        Duration timeDifferenceBetweenEarliestTimeAndNow = Duration.between(currentDateTime.toInstant(), earlierDate.toInstant());
        double timeDifferenceBetweenEarliestTimeAndNowInSeconds = Math.abs(timeDifferenceBetweenEarliestTimeAndNow.toMillis()/CONVERT_MILLISECONDS_TO_SECONDS);

        double numberToDivideByBasedOnTime = (timeDifferenceBetweenEarliestTimeAndNowInSeconds * TIME_DIFFERENCE_BETWEEN_ITEM_LONGEST_TRADE_AND_NOW_MULTIPLIER) + (timeDifferenceInSecondsBetweenBuyAndSellInSeconds * TIME_DIFFERENCE_BETWEEN_ITEM_LONGEST_BUY_AND_SELL_MULTIPLIER);
        int potentialProfit = item.getProfit() * item.getTradeLimitAmount();
        if (timeDifferenceInSecondsBetweenBuyAndSellInSeconds >= DISREGARD_TIME_DIFFERENCE_UNDER_THIS_AMOUNT)
        {
            currentTradeScore = potentialProfit/numberToDivideByBasedOnTime;
        }
        else if (timeDifferenceInSecondsBetweenBuyAndSellInSeconds < DISREGARD_TIME_DIFFERENCE_UNDER_THIS_AMOUNT && timeDifferenceInSecondsBetweenBuyAndSellInSeconds >= 0)
        {
            currentTradeScore =potentialProfit/(timeDifferenceBetweenEarliestTimeAndNowInSeconds + DISREGARD_TIME_DIFFERENCE_UNDER_THIS_AMOUNT);
        }
        long totalBuyCost = (item.getLowPrice() + ADD_TO_PRICE_TO_BEAT_MARGIN) * item.getTradeLimitAmount();
        //Penalties for using too much of cash stack applied after


        if ((totalBuyCost*100)/usersCashAmount < NO_SCORE_PENALTY_IF_ITEM_TOTAL_COST_UNDER_THIS_PERCENT_OF_CASH_STACK)
        {
            item.setTradeScore(currentTradeScore);
            return;
        }
        //Not worth buying
        if ((totalBuyCost*100)/usersCashAmount >= ITEM_TOO_EXPENSIVE_IF_TOTAL_COST_OVER_THIS_PERCENT_OVER_CASH_STACK)
        {
            System.out.println(item.getItemName() + " is very expensive");
            currentTradeScore = 0;
        }
        else if ((totalBuyCost*100)/usersCashAmount >= ITEM_EXTREMELY_HEAVY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK)
        {
            currentTradeScore = currentTradeScore * EXTREMELY_HEAVY_MULTIPLIER_PENALTY_AMOUNT;
        }
        else if ((totalBuyCost*100)/usersCashAmount >= ITEM_VERY_HEAVY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK)
        {
            currentTradeScore = currentTradeScore * VERY_HEAVY_MULTIPLIER_PENALTY_AMOUNT;
        }
        else if ((totalBuyCost*100)/usersCashAmount >= ITEM_HEAVY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK)
        {
            currentTradeScore = currentTradeScore * HEAVY_MULTIPLIER_PENALTY_AMOUNT;
        }
        else if ((totalBuyCost*100)/usersCashAmount >= ITEM_MEDIUM_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK)
        {
            currentTradeScore = currentTradeScore * MEDIUM_MULTIPLIER_PENALTY_AMOUNT;
        }
        else if ((totalBuyCost*100)/usersCashAmount >= ITEM_SMALL_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK)
        {
            currentTradeScore = currentTradeScore * SMALL_MULTIPLIER_PENALTY_AMOUNT;
        }
        else if ((totalBuyCost*100)/usersCashAmount >= ITEM_TINY_PENALTY_IF_THIS_PERCENT_OVER_CASH_STACK)
        {
            currentTradeScore = currentTradeScore * TINY_MULTIPLIER_PENALTY_AMOUNT;
        }

        System.out.println("Trade score of item " + item.getItemName() + " is " + currentTradeScore);
        item.setTradeScore(currentTradeScore);
    }
}
