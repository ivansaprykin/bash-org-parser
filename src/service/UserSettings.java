package service;

/**
 * В этом классе содержатся настройки отображения цитат, выбранные пользователем.
 * Объект этого класса передается от MainFrame к QuoteManager
 */
public class UserSettings {

    private int year;
    private int month;
    private boolean byRating;
    private boolean onlyNewQuotes;
    private boolean onlyFavorite;

    private boolean makeFavorite;

    public UserSettings() {
        year = 0;
        month = 0;
        byRating = false;
        onlyFavorite = false;
        onlyNewQuotes = false;
    }

    public UserSettings(UserSettings userSet) {
        this.year = userSet.getYear();
        this.month = userSet.getMonth();
        this.byRating = userSet.isByRating();
        this.onlyFavorite = userSet.isOnlyFavorite();
        this.onlyNewQuotes = userSet.isOnlyNewQuotes();
        this.makeFavorite = userSet.isMakeFavorite();
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

    public boolean isByRating() {
        return byRating;
    }

    /**
     * Инвертирует значение
     */
    public void changeByRating() {
        this.byRating = (! byRating);
    }

    public boolean isOnlyNewQuotes() {
        return onlyNewQuotes;
    }

    /**
     * Инвертирует значение
     */
    public void changeOnlyNewQuotes() {
        this.onlyNewQuotes = (! onlyNewQuotes);
    }

    public void setOnlyNewQuotes(boolean onlyNewQuotes) {
        this.onlyNewQuotes = onlyNewQuotes;
    }

    public boolean isOnlyFavorite() {
        return onlyFavorite;
    }

    /**
     * Инвертирует значение
     */
    public void changeOnlyFavorite() {
        this.onlyFavorite = (! onlyFavorite);
    }

    public void setOnlyFavorite(boolean onlyFavorite) {
        this.onlyFavorite = onlyFavorite;
    }

    public boolean isMakeFavorite() {
        return makeFavorite;
    }

    public void setMakeFavorite(boolean makeFavorite) {
        this.makeFavorite = makeFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        UserSettings that = (UserSettings) o;

        if(byRating != that.byRating) return false;
        if(makeFavorite != that.makeFavorite) return false;
        if(month != that.month) return false;
        if(onlyFavorite != that.onlyFavorite) return false;
        if(onlyNewQuotes != that.onlyNewQuotes) return false;
        if(year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + (byRating ? 1 : 0);
        result = 31 * result + (onlyNewQuotes ? 1 : 0);
        result = 31 * result + (onlyFavorite ? 1 : 0);
        result = 31 * result + (makeFavorite ? 1 : 0);
        return result;
    }
}
