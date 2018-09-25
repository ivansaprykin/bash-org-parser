package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BashImReader {

    private static final String URD_ADDRESS = "http://bash.im/random?";
    private static final int MAX_URL_END_NUMBER = 9999;

    public List<Quote> readQuotes() {
        String bashImRandomPageUrlAddress = generateRandomPageUrl();
        String htmlCode = getHTMLCode(bashImRandomPageUrlAddress);
        List<Quote> quoteList = findAllQuotes(htmlCode);

        return quoteList;
    }


    /**
     * Гененрирует url адрес случайной цитаты
     *
     * @return url адрес случайной фитаты
     */
    private String generateRandomPageUrl() {

        StringBuilder randomPageUrlAddress = new StringBuilder(URD_ADDRESS);
        Random random = new Random();
        randomPageUrlAddress.append(random.nextInt(MAX_URL_END_NUMBER));

        return randomPageUrlAddress.toString();
    }

    /**
     * Метод возвращает HTML код страницы
     *
     * @return Строка, содержащая HTML код страницы
     */
    private String getHTMLCode(String URLAddress) {

        StringBuilder HTMLCode = new StringBuilder();

        try {
            /* Class URL represents a Uniform Resource Locator, a pointer to a "resource" on the World Wide Web. */
            URL url = new URL(URLAddress);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "windows-1251"));

            String currentLine;

            while((currentLine = bufferedReader.readLine()) != null) {
                HTMLCode.append(currentLine);
            }

            bufferedReader.close();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return (HTMLCode.toString());
    }

    /**
     * Метод находит среди HTML кода страницы цитаты и возвращает их список
     *
     * @param htmlCode Код страницы в котором будет проводиться поиск цитат
     * @return Список цитат найденных на странице
     */
    private List<Quote> findAllQuotes(String htmlCode) {
        /*
         * найти ретинг, дату(год и месяц) и текст цитаты
         * создать соответствующую цитату
         * добавить её в список
        */

        // class="rating">1973</span></span>
        String regExpForRating = "class=\"rating\">(.*?)</span>";
        Pattern patternForRating = Pattern.compile(regExpForRating);
        Matcher matcherForRating = patternForRating.matcher(htmlCode);

        // <span class="rating-o"><span id="v262614"
        String regExpForId = "<span class=\"rating-o\"><span id=\"v(\\d+)\"";
        Pattern patternForId = Pattern.compile(regExpForId);
        Matcher matcherForId = patternForId.matcher(htmlCode);

        // <span class="date">2011-09-18 08:13</span>
        String regExpForYear = "<span class=\"date\">(\\d+)(.*?)</span>";
        Pattern patternForYear = Pattern.compile(regExpForYear);
        Matcher matcherForYear = patternForYear.matcher(htmlCode);

        // <span class="date">2011-09-18 08:13</span>
        String regExpForMonth = "<span class=\"date\">(\\d+)-(\\d+)(.*?)</span>"; // 2-я группа это месяц
        Pattern patternForMonth = Pattern.compile(regExpForMonth);
        Matcher matcherForMonth = patternForMonth.matcher(htmlCode);

        // <div class="text">текст очень смешной цитаты</div>
        String regExpForText = "<div class=\"text\">(.*?)</div>";
        Pattern patternForText = Pattern.compile(regExpForText);
        Matcher matcherForText = patternForText.matcher(htmlCode);

        List<Quote> listOfQuotes = new ArrayList<Quote>();

        /* Т.к. каждая цитата содержит текст, рейтинг, год и месяц публикации, то условием завершения цикла может быть любой из этих параметров  */
        while(matcherForText.find()) {
            matcherForRating.find();
            matcherForYear.find();
            matcherForMonth.find();
            matcherForId.find();

            // иногда цитаты не содержат рейнинга, в место этого идет ... такие цитаты не рассматриваем
            String rating = matcherForRating.group(1);
            if(rating.equals(" ... ")) {
                continue;
            }

            Quote quote = new Quote();
            quote.setId(Integer.parseInt(matcherForId.group(1)));
            quote.setText(matcherForText.group(1));
            quote.setRating(Integer.parseInt(rating));
            quote.setYear(Integer.parseInt(matcherForYear.group(1)));
            quote.setMonth(Integer.parseInt(matcherForMonth.group(2)));

            listOfQuotes.add(quote);
        }

        return listOfQuotes;

    }

}