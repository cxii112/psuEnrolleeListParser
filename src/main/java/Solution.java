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
        String listUrl = args[0];
//        if (listUrl.charAt(listUrl.length() - 1) != '/')
//            listUrl += '/';
//        listUrl += "#380402-07-12";
        String specCode = args[1] + 'b';
        Document DOM = Jsoup.connect(listUrl).get();
        Elements articles = DOM.getElementsByTag("article");
        articles.removeIf(article -> !isInternalStudyBachelor(article));
        System.out.printf("Bachelor articles: %d\n", articles.size());
        fillEnrolleesList(articles);
    }

    private static boolean isInternalStudyBachelor(Element article)
    {
        try
        {
            String specName = article.getElementsByTag("span").first()
                    .ownText().toLowerCase();
            if (specName.contains("заочная")) return false;
            if (specName.contains("бакалавриат")) return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static void fillEnrolleesList(Elements articles)
    {
        articles.forEach(article -> {
            Elements articleTables = article.getElementsByTag("table");
            String specCode = article.getElementsByTag("a").first()
                    .attr("name");
            addEnrolleesFromTable(articleTables.first(), specCode + 'b');
            addEnrolleesFromTable(articleTables.last(), specCode + 'p');
        });
    }

    private static void addEnrolleesFromTable(Element table, String specKey)
    {
        Elements rawRows = table.getElementsByTag("tr");
        rawRows.removeIf(row -> {
            if (row.attr("bgcolor").equals("#EEEEEE")) return true;
            var col = row.getElementsByTag("td").first();
            if (col.attr("colspan").equals("8")) return true;
            return false;
        });
    }
}
