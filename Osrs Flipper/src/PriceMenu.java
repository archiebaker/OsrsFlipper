import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class PriceMenu extends OsrsFlipper {
    private JFrame PriceMenuFrame;
    private JButton saveButton;
    private JButton loadButton;
    private JList Results;
    private JButton getLivePricesButton;
    private JTextArea ItemsToSearchTxt;
    private JPanel panelMain;
    private JTextArea resultsText;
    private JButton sortByMostProfitPerUnitButton;
    private JButton sortByMostProfitButton1;
    private JButton sortByMostROIButton;
    private JButton sortByMostROIButton1;
    private JButton sortByMostProfitTotalButton;
    private JTextField CashStackAmountTxt;
    private JRadioButton ThousandsRadioButton;
    private JRadioButton MillionsRadioButton;
    private JRadioButton BillionsRadioButton;
    private JButton sortByTradeScoreButton;

    //Consturction of JFrame (GUI)
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("PriceMenu");
        frame.setContentPane(new PriceMenu().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }





    //static OsrsFlipper flipper = new OsrsFlipper();

    ArrayList<Items> itemsArray = new ArrayList<>();
    double usersCashStack;
    public PriceMenu()
    {
        getLivePricesButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (usersCashStack == 0)
                {
                    JOptionPane.showMessageDialog(PriceMenuFrame, "Please enter the amount of money you have first");
                    return;
                }
                itemsArray.clear();
                resultsText.setText("");
                String allItemsInTextBoxAsString = ItemsToSearchTxt.getText();
                String[] itemNames = allItemsInTextBoxAsString.split("\n");
                try
                {
                    createHashMapOfItemNamesToItemIDs();
                } catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
                try
                {
                    scrapeData(RequestDetails.INSTANT_PRICE);
                    scrapeData(RequestDetails.FIVE_MINUTE_PRICE);
                    scrapeData(RequestDetails.TRADE_LIMIT_AMOUNT);
                } catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
                for (String itemName : itemNames)
                {
                    int lowPriceAverage5m = 0;
                    int highPriceAverage5m = 0;
                    int potentialProfit5m = 0;
                    int lowVolumeAverage5m = 0;
                    int highVolumeAverage5m = 0;
                    int tradeScore = 0;
                    int itemID = grabItemIDS(itemName);
                    //Item is invalid
                    if (itemID == -1)
                    {
                        System.out.println("loop continued");
                        continue;
                    }
                    String itemDataInstant = getSpecificItemDataInstant(itemID);
                    String itemData5m = getSpecificItemData5m(itemID);
                    String tradeLimitData = getTradeLimitData(itemID);
                    int highPriceInstant = getHighPriceInstant(itemDataInstant);
                    int lowPriceInstant = getLowPriceInstant(itemDataInstant);
                    int potentialProfitInstant = highPriceInstant - lowPriceInstant;

                    int tradeLimitAmount = getTradeLimit(tradeLimitData);

                    if (itemData5m !=null) {
                         lowPriceAverage5m = getLowPrice5m(itemData5m);
                         highPriceAverage5m = getHighPrice5m(itemData5m);

                         potentialProfit5m = highPriceAverage5m - lowPriceAverage5m;


                         lowVolumeAverage5m = getLowVolume5m(itemData5m);
                         highVolumeAverage5m = getHighVolume5m(itemData5m);
                    }
                    Date lowTime = getLowTime(itemDataInstant);
                    Date highTime = getHighTime(itemDataInstant);
                    int fullInstantProfit = tradeLimitAmount * potentialProfitInstant;
                    int fullAverageProfit5m = tradeLimitAmount * potentialProfit5m;
                    int fullBuyCostInstant = lowPriceInstant * tradeLimitAmount;
                    int fullBuyCostAverage = lowPriceAverage5m * tradeLimitAmount;

                    double ROIInstant = ((double) fullInstantProfit * 100) / fullBuyCostInstant;
                    double ROIAverage = 0;
                    if (fullBuyCostAverage != 0) {
                        ROIAverage = ((double) fullAverageProfit5m * 100) / fullBuyCostAverage;
                    }
                    if (ROIInstant != 0) {
                        ROIInstant = round(ROIInstant, 2);
                    }
                    if (ROIAverage != 0 && fullBuyCostAverage != 0) {
                        ROIAverage = round(ROIAverage, 2);
                    }
                    tradeScore = 0;
                    Items item = new Items(itemName, lowPriceInstant, highPriceInstant, potentialProfitInstant, ROIInstant, lowTime, highTime, potentialProfit5m, ROIAverage, highPriceAverage5m, lowPriceAverage5m, highVolumeAverage5m, lowVolumeAverage5m, tradeLimitAmount, tradeScore);
                    calculateTradeScore(item);
                    itemsArray.add(item);
                }
                printResults();
            }
        });



        //sort by most profit total
        sortByMostProfitTotalButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Collections.sort(itemsArray, new Comparator<Items>()
                {
                    public int compare(Items p1, Items p2)
                    {
                        return (p1.getProfit() * p1.getTradeLimitAmount()- p2.getProfit() * p2.getTradeLimitAmount()) * -1;
                    }
                });
                printResults();
            }
        });

        //Sort by trade score
        sortByTradeScoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(itemsArray, new Comparator<Items>()
                {
                    public int compare(Items p1, Items p2)
                    {
                        return (int)( Math.round(p1.getTradeScore()) - (int) Math.round(p2.getTradeScore())) * -1;
                    }
                });
                printResults();
            }
        });

        //Sort by most profit per unit
        sortByMostProfitPerUnitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //sort in descending order
                Collections.sort(itemsArray, new Comparator<Items>()
                {
                    public int compare(Items p1, Items p2)
                    {
                        return (p1.getProfit()- p2.getProfit()) * -1;
                    }
                });
                printResults();
            }
        });

        sortByMostProfitButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(itemsArray, new Comparator<Items>(){
                    public int compare(Items p1, Items p2) {
                        return (p1.getAvgProfit5m()- p2.getAvgProfit5m()) * -1;
                    }
                });
                printResults();
            }
        });

        sortByMostROIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(itemsArray, new Comparator<Items>(){
                    public int compare(Items p1, Items p2) {
                        return Double.compare(p1.getInstantROI(), p2.getInstantROI()) * -1;
                    }
                });
                printResults();
            }
        });

        sortByMostROIButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(itemsArray, new Comparator<Items>(){
                    public int compare(Items p1, Items p2) {
                        return Double.compare(p1.getFiveMinROI(), p2.getFiveMinROI()) * -1;
                    }
                });
               printResults();
            }
        });


        CashStackAmountTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double.parseDouble(CashStackAmountTxt.getText());
                    ThousandsRadioButton.setText(CashStackAmountTxt.getText() + "K");
                    MillionsRadioButton.setText(CashStackAmountTxt.getText() + "M");
                    BillionsRadioButton.setText(CashStackAmountTxt.getText() + "B");
                    if (ThousandsRadioButton.isSelected())
                    {
                        usersCashStack = Double.parseDouble(CashStackAmountTxt.getText()) * 1000;
                    }
                    if (MillionsRadioButton.isSelected())
                    {
                        usersCashStack = Double.parseDouble(CashStackAmountTxt.getText()) * 1000000;
                    }
                    if (BillionsRadioButton.isSelected())
                    {
                        usersCashStack = Double.parseDouble(CashStackAmountTxt.getText()) * 1000000000;
                    }
                    System.out.println("You have " + usersCashStack);

                }catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(PriceMenuFrame, "Please enter a number only");
                }

            }
        });


    }

    Date earlierDate;
    public void calculateTradeScore (Items item)
    {
        Date currentDateTime = new Date();
        //+2 added to beat margins
        double currentTradeScore = 0;
        Date lowTime = item.getLowTime();
        Date highTime = item.getHighTime();
        Duration timeDifferenceBetweenBuyAndSell = Duration.between(lowTime.toInstant(), highTime.toInstant());
        //Using Absolute since it doesn't matter which time value is bigger, just want to know the difference
        double timeDifferenceInSecondsBetweenBuyAndSellInSeconds = Math.abs(timeDifferenceBetweenBuyAndSell.toMillis()/1000);
        //System.out.println("Duration is " + timeDifferenceInSecondsBetweenBuyAndSellInSeconds);

        if (lowTime.before(highTime) || lowTime.equals(highTime))
        {
            earlierDate = lowTime;
        }
        else if (highTime.before(lowTime))
        {
            earlierDate = highTime;
        }
        Duration timeDifferenceBetweenEarliestTimeAndNow = Duration.between(currentDateTime.toInstant(), earlierDate.toInstant());
        double timeDifferenceBetweenEarliestTimeAndNowInSeconds = Math.abs(timeDifferenceBetweenEarliestTimeAndNow.toMillis()/1000);

        double numberToDivideByBasedOnTime = (timeDifferenceBetweenEarliestTimeAndNowInSeconds * 0.6) + (timeDifferenceInSecondsBetweenBuyAndSellInSeconds * 1.0);
        int potentialProfit = item.getProfit() * item.getTradeLimitAmount();
        if (timeDifferenceInSecondsBetweenBuyAndSellInSeconds >= 10)
        {
            currentTradeScore = potentialProfit/numberToDivideByBasedOnTime;
        }
        else if (timeDifferenceInSecondsBetweenBuyAndSellInSeconds <10 && timeDifferenceInSecondsBetweenBuyAndSellInSeconds >= 0)
        {
            currentTradeScore =potentialProfit/(timeDifferenceBetweenEarliestTimeAndNowInSeconds + 10);
        }






        long totalBuyCost = (item.getLowPrice() + 1) * item.getTradeLimitAmount();
        //Penalties for using too much of cash stack applied after
        if ((totalBuyCost*100)/usersCashStack > 25)
        {
            //Not worth buying
            if ((totalBuyCost*100)/usersCashStack >= 1000)
            {
                System.out.println(item.getName() + " is very expensive");
                currentTradeScore = 0;
            }
            else if ((totalBuyCost*100)/usersCashStack >= 500)
            {
                currentTradeScore = currentTradeScore*0.1;
            }
            else if ((totalBuyCost*100)/usersCashStack >= 200)
            {
                currentTradeScore = currentTradeScore*0.55;
            }
            else if ((totalBuyCost*100)/usersCashStack >= 100)
            {
                currentTradeScore = currentTradeScore*0.6;
            }
            else if ((totalBuyCost*100)/usersCashStack >= 50)
            {
                currentTradeScore = currentTradeScore*0.7;
            }
           else if ((totalBuyCost*100)/usersCashStack >= 40)
            {
                currentTradeScore = currentTradeScore*0.8;
            }
            else if ((totalBuyCost*100)/usersCashStack >= 25)
            {
                currentTradeScore = currentTradeScore*0.9;
            }
        }
        System.out.println("Trade score of item " + item.getName() + " is " + currentTradeScore);
        item.setTradeScore(currentTradeScore);
    }


    public void printResults()
    {
        resultsText.setText("");
        for (Items item : itemsArray)
        {
            int fullInstantProfit = item.getTradeLimitAmount() * item.getProfit();
            int fullAverageProfit5m = item.getTradeLimitAmount() * item.getAvgProfit5m();
            int fullBuyCostInstant = item.getLowPrice() * item.getTradeLimitAmount();
            int fullBuyCostAverage = item.getLowPriceAverage5m() * item.getTradeLimitAmount();

            double ROIInstant = ((double) fullInstantProfit * 100) / fullBuyCostInstant;
            double ROIAverage = 0;
            if (fullBuyCostAverage != 0)
            {
                ROIAverage = ((double) fullAverageProfit5m * 100) / fullBuyCostAverage;
            }
            if (ROIInstant != 0)
            {
                ROIInstant = round(ROIInstant, 2);
            }
            if (ROIAverage != 0 && fullBuyCostAverage != 0)
            {
                ROIAverage = round(ROIAverage, 2);
            }

            resultsText.setText(resultsText.getText() + "--------------" + item.getName() + "--------------" +
                    "\n Potential profit per unit: " + item.getProfit() +
                    "\n Buy limit: " + item.getTradeLimitAmount() +
                    "\n Potential profit total: " + item.getProfit() * item.getTradeLimitAmount() +
                    "\n TradeScore: " + item.getTradeScore() +
                    "\nInstant ROI: " + ROIInstant + "%" +
                    "\nHigh price: " + item.getHighPrice() +
                    "\n Low price: " + item.getLowPrice() +
                    "\n High time: " + item.getHighTime() +
                    "\n Low time: " + item.getLowTime() +
                    "\n\n Potential Average profit per unit " + item.getAvgProfit5m()  +
                    "\n5m Average ROI: " + ROIAverage + "%" +
                    "\nPotential Max profit: " + item.getAvgProfit5m() * item.getTradeLimitAmount() +
                    "\n High price average " + item.getHighPriceAverage5m() +
                    "\n Low price average " + item.getLowPriceAverage5m() +
                    "\n High price volume " + item.getHighPriceVolume5m() +
                    "\n Low price volume " + item.getLowPriceVolume5m() +
                    "\n" );
            //Stay at top of screen
            resultsText.moveCaretPosition(0);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
