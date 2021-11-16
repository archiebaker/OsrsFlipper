import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PriceMenu extends OsrsFlipper {
    private JButton saveButton;
    private JButton loadButton;
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

    ArrayList<Item> itemArray = new ArrayList<>();

    private double usersCashAmount;

    public PriceMenu()
    {
        getLivePricesButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (usersCashAmount == 0)
                {
                    JOptionPane.showMessageDialog(panelMain, "Please enter the amount of money you have first");
                    return;
                }
                itemArray.clear();
                resultsText.setText("");
                String allItemsInTextBoxAsString = ItemsToSearchTxt.getText();
                String[] itemNames = allItemsInTextBoxAsString.split("\n");
                try
                {
                    createHashMapOfItemNamesToItemIDs();
                } catch (IOException ioException)
                {
                    JOptionPane.showMessageDialog(panelMain, "Hashmap couldn't be created, has the wiki changed?");
                    ioException.printStackTrace();
                }
                try
                {
                    scrapeData(RequestDetails.INSTANT_PRICE);
                    scrapeData(RequestDetails.FIVE_MINUTE_PRICE);
                    scrapeData(RequestDetails.TRADE_LIMIT_AMOUNT);
                } catch (IOException ioException)
                {
                    JOptionPane.showMessageDialog(panelMain, "Item data could not be retrieved from the wiki");
                    ioException.printStackTrace();
                }
                TradeScoreCalculation tradeScoreCalculation = new TradeScoreCalculation();
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
                    if (fullBuyCostAverage != 0)
                    {
                        ROIAverage = ((double) fullAverageProfit5m * 100) / fullBuyCostAverage;
                    }
                    if (ROIInstant != 0)
                    {
                        ROIInstant = round(ROIInstant, 2);
                    }
                    Item item = new Item(itemName, lowPriceInstant, highPriceInstant, potentialProfitInstant, ROIInstant, lowTime, highTime, potentialProfit5m, ROIAverage, highPriceAverage5m, lowPriceAverage5m, highVolumeAverage5m, lowVolumeAverage5m, tradeLimitAmount, tradeScore);
                    tradeScoreCalculation.calculateTradeScore(item, usersCashAmount);
                    itemArray.add(item);
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
                Collections.sort(itemArray, new Comparator<Item>()
                {
                    public int compare(Item p1, Item p2)
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
                Collections.sort(itemArray, new Comparator<Item>()
                {
                    public int compare(Item p1, Item p2)
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
                Collections.sort(itemArray, new Comparator<Item>()
                {
                    public int compare(Item p1, Item p2)
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
                Collections.sort(itemArray, new Comparator<Item>(){
                    public int compare(Item p1, Item p2) {
                        return (p1.getAvgProfit5m()- p2.getAvgProfit5m()) * -1;
                    }
                });
                printResults();
            }
        });

        sortByMostROIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(itemArray, new Comparator<Item>(){
                    public int compare(Item p1, Item p2) {
                        return Double.compare(p1.getInstantROI(), p2.getInstantROI()) * -1;
                    }
                });
                printResults();
            }
        });

        sortByMostROIButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.sort(itemArray, new Comparator<Item>(){
                    public int compare(Item p1, Item p2) {
                        return Double.compare(p1.getFiveMinROI(), p2.getFiveMinROI()) * -1;
                    }
                });
               printResults();
            }
        });

        final int THOUSAND_MULTIPLIER = 1000;
        final int MILLION_MULTIPLIER = 1000000;
        final int BILLION_MULTIPLIER = 1000000000;
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
                        usersCashAmount = ((Double.parseDouble(CashStackAmountTxt.getText()) * THOUSAND_MULTIPLIER));
                    }
                    if (MillionsRadioButton.isSelected())
                    {
                        usersCashAmount = ((Double.parseDouble(CashStackAmountTxt.getText()) * MILLION_MULTIPLIER));
                    }
                    if (BillionsRadioButton.isSelected())
                    {
                        usersCashAmount = ((Double.parseDouble(CashStackAmountTxt.getText()) * BILLION_MULTIPLIER));
                    }
                    System.out.println("You have " + usersCashAmount);

                }catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(panelMain, "Please enter a number only");
                }

            }
        });


    }




    public void printResults()
    {
        resultsText.setText("");
        for (Item item : itemArray)
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
            resultsText.setText(resultsText.getText() + "--------------" + item.getItemName() + "--------------" +
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
