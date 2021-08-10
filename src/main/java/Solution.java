import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Solution
{
    private static ArrayList<Enrollee> enrollees = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        for (String str : args)
        {
            System.out.println(str);
        }
        final String LIST_URL = args[1];
        final String SPEC_CODE = args[3] + 'b';
        Document DOM = Jsoup.connect(LIST_URL).get();
        Elements articles = DOM.getElementsByTag("article");
        articles.removeIf(Solution::checkIfInternalStudyBachelor);
        System.out.printf("Bachelor articles: %d\n", articles.size());
    }

    private static boolean checkIfInternalStudyBachelor(Element article)
    {
        Element header = article.getElementsByTag("h2").first();
        if (header == null) return false;
        if (header.getElementsByTag("span").isEmpty()) return false;
        if (header.getElementsByTag("span").first() == null) return false;
        String specName = header.getElementsByTag("span").first().ownText();
        if (specName == null || specName.equals("")) return false;
        if (specName.contains("заочная")) return false;
        return specName.contains("бакалавриат");
    }

    private static void fillEnrolleesList(Elements articles)
    {

    }
}
