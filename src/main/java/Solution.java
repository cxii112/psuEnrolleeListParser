import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Solution
{
    private static Map<String, Enrollee> enrollees = new HashMap<>();

    public static void main(String[] args) throws IOException
    {
        for (String str : args)
        {
            System.out.println(str);
        }
        String listUrl = args[0];
        String specCode = args[1] + 'b';

        Document DOM = Jsoup.connect(listUrl)
                .maxBodySize(0)
                .get();
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
        rawRows.forEach(rawRow -> {
            Elements cols = rawRow.getElementsByTag("td");
            Long id = Long.parseLong(cols.get(1).text());
            int points = Integer.parseInt(cols.get(7).text());
            int index = Integer.parseInt(cols.get(0).text());
            boolean hasAgreement = cols.get(3).ownText().contains("+");
            if (enrollees.containsKey(id.toString()))
            {
                Enrollee current = enrollees.get(id.toString());
                current.addSpec(specKey, index, hasAgreement);
            }
            else
            {
                Enrollee newEnrollee = new Enrollee(id, points);
                newEnrollee.addSpec(specKey, index, hasAgreement);
                enrollees.put(id.toString(), newEnrollee);
            }
        });
    }
}
