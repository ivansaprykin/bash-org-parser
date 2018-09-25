package service;

/**
 * Класс представляет цитату с сайта bash.im
 */

public class Quote {

    private String text;
    private int rating;
    private int year;
    private int month;
    private boolean viewed; // просмотрена ли цитата
    private boolean favorite; // является ли цитата избранной
    private int id;

    public Quote(String text, int rating, int year, int month) {
        this.text = text;
        this.rating = rating;
        this.year = year;
        this.month = month;

        rephraseSymbolsInText();
    }

    /**
     * Метод заменяет коды символов кавычек "" <> и переноса строки на сами кавычки и переносы.
     * Вызывается автоматически при добавлении текста цитаты.
     */
    private void rephraseSymbolsInText() {

        text = text.replaceAll("<br>", "\n");      // HTML
        text = text.replaceAll("<br />", "\n");    // XHTML
        text = text.replaceAll("&quot;", "");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");

    }



    public Quote() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        rephraseSymbolsInText();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean notSeen) {
        this.viewed = viewed;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();

        stringRepresentation.append("Рейтинг: ");
        stringRepresentation.append(rating);
        stringRepresentation.append("\t");
        stringRepresentation.append("Год: ");
        stringRepresentation.append(year);
        stringRepresentation.append(" Месяц: ");
        stringRepresentation.append(month);
        stringRepresentation.append("\n");
        stringRepresentation.append(text);
        stringRepresentation.append("\n\n\n");

        return stringRepresentation.toString();
    }

}
