package service;

import exceptions.DBSystemException;
import exceptions.NotAllQuotesWereAddedException;
import service.Quote;

import java.util.List;

public interface QuoteDAO {

    /**
     *
     * @param quotes Список цитат для отправки в БД
     * @param number Сколько цитат из списка отправить в БД
     * @throws NotAllQuotesWereAddedException Если не удалось отправить в БД number цитат из quotes списка (в БД уже есть цитаты с таким id)
     */
    public void insert(List<Quote> quotes, int numberOfQuotesToInsert) throws DBSystemException;

    public void makeFavorite(Quote quote) throws DBSystemException;

    public void makeNotFavorite(Quote quote) throws DBSystemException;

    public void makeViewed(Quote quote) throws DBSystemException;

    /**
     * @param byRating Сортировать ли цитаты по рейтингу
     * @param year За какой год цитаты, если 0 - за все года
     * @param month За какой месяц, если 0 - за все месяца
     * @param onlyFavorite Показывать только избранные цитаты.
     * @param onlyNotViewed Показывать только просмотренные цитаты.
     * @return Список цитат, удовлетворяющих условию поиска.
     * @throws DBSystemException Что-то пошло не так при попытке выполнить запрос.
     */
    public List<Quote> getAllByParameters(boolean byRating, int year, int month, boolean onlyFavorite, boolean onlyNotViewed) throws DBSystemException;

}
