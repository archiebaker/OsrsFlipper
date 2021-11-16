
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import java.io.*;
import java.util.Date;
import java.util.HashMap;


public class OsrsFlipper {

    static String priceDataInstant;
    static String priceData5m;
    static String tradeLimitData;
    //Using ENUMS to give compile error instead of runtime error
    enum RequestDetails
    {
        INSTANT_PRICE, FIVE_MINUTE_PRICE, TRADE_LIMIT_AMOUNT
    }

    Page page;
    public void scrapeData(RequestDetails requestedDetails) throws IOException
    {
        WebClient webClient = new WebClient();

        if (requestedDetails == RequestDetails.INSTANT_PRICE)
        {
            page = webClient.getPage("https://prices.runescape.wiki/api/v1/osrs/latest");
            WebResponse response = page.getWebResponse();
            priceDataInstant = response.getContentAsString();
        }
        if (requestedDetails == RequestDetails.FIVE_MINUTE_PRICE)
        {
            page = webClient.getPage("https://prices.runescape.wiki/api/v1/osrs/5m");
            WebResponse response = page.getWebResponse();
            priceData5m = response.getContentAsString();
        }
        if (requestedDetails == RequestDetails.INSTANT_PRICE)
        {
            page = webClient.getPage("https://prices.runescape.wiki/api/v1/osrs/mapping");
            WebResponse response = page.getWebResponse();
            tradeLimitData = response.getContentAsString();
        }
        webClient.close();


    }




    public static String getSpecificItemDataInstant(int itemID) {
        String [] parts = priceDataInstant.split("\""+itemID+"\"");
        parts = parts[1].split("}");
        return parts[0];
    }






    public static String getSpecificItemData5m(int itemID) {
        String [] parts = priceData5m.split("\""+itemID+"\"");
        if (parts.length > 1) {
            parts = parts[1].split("}");
            return parts[0];
        }

        return null;
    }

    public static String getTradeLimitData(int itemID) {
        String [] parts = tradeLimitData.split("\"id\":"+itemID+",");
        if (parts.length > 1) {
            parts = parts[1].split("}");
            return parts[0];
        }

        return null;
    }




    public static int getHighPriceInstant(String searchQuery) {
        String [] parts = searchQuery.split("\"high\":");
        parts = parts[1].split(",");
        return Integer.parseInt(parts[0]);
    }

    public static int getLowPriceInstant(String searchQuery) {
        String [] parts = searchQuery.split("\"low\":");
        parts = parts[1].split(",");
        return Integer.parseInt(parts[0]);
    }

    public static int getHighPrice5m(String searchQuery) {
        String [] parts = searchQuery.split("\"avgHighPrice\":");
        parts = parts[1].split(",");
        int highPrice = 0;
        //System.out.println(parts[0]);
        if (!parts[0].equals("null")) {
             highPrice = Integer.parseInt(parts[0]);
        }
        return highPrice;
    }

    public static int getLowPrice5m(String searchQuery) {
        String [] parts = searchQuery.split("\"avgLowPrice\":");
        parts = parts[1].split(",");
        int lowPrice =  0;
        //System.out.println(parts[0]);
        if (!parts[0].equals("null")) {
            lowPrice = Integer.parseInt(parts[0]);
        }
        return lowPrice;
    }

    public static int getHighVolume5m(String searchQuery) {
        String [] parts = searchQuery.split("\"highPriceVolume\":");
        parts = parts[1].split(",");
        return Integer.parseInt(parts[0]);
    }

    public static int getLowVolume5m(String searchQuery) {
        String [] parts = searchQuery.split("\"lowPriceVolume\":");
        parts = parts[1].split(",");
        return Integer.parseInt(parts[0]);
    }

    public static Date getHighTime(String searchQuery) {
        String [] parts = searchQuery.split("\"highTime\":");
        parts = parts[1].split(",");
        return new java.util.Date((long)Integer.parseInt(parts[0])*1000);

    }

    public static Date getLowTime(String searchQuery) {
        String [] parts = searchQuery.split("\"lowTime\":");
        parts = parts[1].split("}");
        return new java.util.Date((long)Integer.parseInt(parts[0])*1000);

    }

    public static int getTradeLimit(String searchQuery) {
        String [] parts = searchQuery.split("\"limit\":");
        if (parts.length > 1) {
            parts = parts[1].split(",");
            return Integer.parseInt(parts[0]);
        }
        //5 is the lowest trade limit on any item so its safe to default to 5
        return 5;

    }

    static HashMap<String, Integer> itemsNamesAndIDs = new HashMap<>();
    //Map item names to item ID's for use
    public static void createHashMapOfItemNamesToItemIDs() throws IOException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(false); //ignore ssl certificate
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        String url = "https://oldschool.runescape.wiki/w/Module:GEIDs/data";
        webClient.getPage(url);
        webClient.waitForBackgroundJavaScriptStartingBefore(20000);
        HtmlPage returnedPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
        //XPath found by Chrome dev tools
        DomElement element = returnedPage.getFirstByXPath("/html/body/div[3]/div[2]/div[4]/pre");
        String allItems = element.asXml();
        String [] individualEntries = allItems.split(",");
        for (String row : individualEntries)
        {
            String[] namesAndIDs = row.split(" = ");
            try
            {
                namesAndIDs[0] = namesAndIDs[0].replace("[", "");
                namesAndIDs[0] = namesAndIDs[0].replace("]", "");
                namesAndIDs[0] = namesAndIDs[0].replaceAll("\"", "");
                namesAndIDs[0] = namesAndIDs[0].replaceAll("    ", "");
                itemsNamesAndIDs.put(namesAndIDs[0], Integer.parseInt(namesAndIDs[1]));
            } catch (NumberFormatException e) {
                System.out.println("Line isn't formatted as expected");
            }


        }
    }

    public static int grabItemIDS(String itemName)
    {
        Integer i = -1;
        if (itemsNamesAndIDs.containsKey("\n"+itemName))
        {
            i = itemsNamesAndIDs.get("\n"+itemName);
        }
        return i;
        }
    }