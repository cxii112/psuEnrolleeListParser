import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Solution
{
    private static Map<String, Enrollee> enrollees = new HashMap<>();

    public static void main(String[] args) throws IOException
    {
        URL listUrl = null;
        String specCode = null;
        File file = null;
        try
        {
            listUrl = new URL(args[0]);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Invalid URL");
            System.exit(1);
        }
        try
        {
            specCode = args[1] + 'b';
        }
        catch (Exception e)
        {
            System.out.println("Invalid speciality code");
            System.exit(1);
        }
        try
        {
            String dirPath = args[2];
            if (dirPath.charAt(dirPath.length() - 1) == '/')
            {
                dirPath += '/';
            }
            file = new File(dirPath + specCode + ".md");
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Invalid path");
        }
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        writer.write("# Summary\n\n");

        Document DOM = Jsoup.connect(listUrl.toString())
                .maxBodySize(0)
                .get();

        Elements articles = DOM.getElementsByTag("article");
        articles.removeIf(article -> !isInternalStudyBachelor(article));

        System.out.printf("Bachelor articles(%s): %d",
                          specCode,
                          articles.size());
        writeToMarkDown(String.format("Bachelor articles(%s): %d\n",
                                      specCode,
                                      articles.size()),
                        writer);

        fillEnrolleesList(articles);
        var resultingTable = extractSpecTable(specCode);
        writeToMarkDown("| # | id | Согласие | Баллы |\n", writer);
        writeToMarkDown("|:---|---|:---:|---:|\n", writer);
        for (var enrolleeData : resultingTable.entrySet())
        {
            Enrollee enrollee = enrolleeData.getValue();
            int index = enrollee.indexAt(specCode);
            String id = enrollee.id;
            boolean hasAgreement = enrollee.hasAgreementTo() != null &&
                    enrollee.hasAgreementTo().equals(specCode);
            int points = enrollee.pointsSum;
            String row = String.format("| %d | %s | %s | %d |\n",
                                       index,
                                       id,
                                       hasAgreement ? "+" : "",
                                       points);
            writeToMarkDown(row, writer);
        }
        System.out.println("Done");
        if (writer != null)
        {
            writer.close();
        }
    }

    private static void writeToMarkDown(String message, BufferedWriter writer)
    {
        try
        {
            writer.write(message);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
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
            Elements h3s = article.getElementsByTag("h3");
            h3s.forEach(h3 -> {
                if (h3.text().toLowerCase().contains("бюджетные"))
                {
                    addEnrolleesFromTable(articleTables.first(), specCode + 'b');
                }
                else if (h3.text().toLowerCase().contains("договорам"))
                {
                    addEnrolleesFromTable(articleTables.last(), specCode + 'p');
                }
            });
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
            String id = cols.get(1).text();
            int index = Integer.parseInt(cols.get(0).text());
            boolean hasAgreement = cols.get(3).ownText().contains("+");
            int points;
            try
            {
                points = Integer.parseInt(cols.get(7).text());
            }
            catch (Exception e)
            {
                points = 0;
            }
            if (enrollees.containsKey(id))
            {
                Enrollee current = enrollees.get(id);
                current.addSpec(specKey, index, hasAgreement);
            }
            else
            {
                Enrollee newEnrollee = new Enrollee(id, points);
                newEnrollee.addSpec(specKey, index, hasAgreement);
                enrollees.put(id, newEnrollee);
            }
        });
    }

    private static Map<String, Enrollee> extractSpecTable(String specKey)
    {
        Map<String, Enrollee> result = new HashMap<>();
        var enrolleesIds = enrollees.keySet();
        for (var enrolleeId : enrolleesIds)
        {
            var enrollee = enrollees.get(enrolleeId);
            if (enrollee.hasSentTo(specKey))
            {
                result.put(enrolleeId, enrollee);
            }
        }
        result.entrySet().removeIf(enrollee -> {
            if (enrollee.getValue().hasAgreementTo() == null) return false;
            if (enrollee.getValue().hasAgreementTo().equals(specKey)) return false;
            return true;
        });
        return result;
    }
}
