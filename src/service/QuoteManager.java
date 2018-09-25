package service;


import dao.DAOFactory;
import exceptions.DBSystemException;
import exceptions.NotAllQuotesWereAddedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, методы которого напрямую вызываются нажатием кнопок в приложении.
 * Содержит методы реализующие пользовательскую функциональность.
 */
public class QuoteManager {

    private static final int NUMBER_OF_QUOTES_THAT_CAN_BE_READ_AT_ONCE = 50;

    private QuoteDAO quoteDAO; // Для работы с БД

    private List<Quote> quoteList; // Кэшируем цитаты, полученные из БД
    private List<Quote> viewedQuotes; // Цитаты, которые уже показывали польлователю
    private UserSettings userSettings; // Запоминаем предыдущие настройки пользователя.
    private Quote currentQuote; // Цитата, которую читает пользователь. Запоминаем для обновления её в БД как прочитанной и, возможно, избранной.

    public QuoteManager() {

        // Создаем требуемую фабрику
        DAOFactory sqlJdbcFactory = DAOFactory.getFactory(DAOFactory.DAO_MY_SQL_JDBC_FACTORY);
        // Создаем DAO
        quoteDAO = sqlJdbcFactory.createQuoteDAO();

    }

    /**
     * Добавить цитаты в БД
     *
     * @param numOfQuotes Сколько цитат необходимо добавить
     */
    public void writeNewQuotesIntoDB(int numOfQuotes) {
        /**
         * Получить список цитат из сайта
         * Записать весь список в БД
         * Если записались не все циаты(Какие-то уже там были)
         *  Получить еще список цитат из сайта и отправить их в БД
         *
         * При чтении цитат с сайта читаем  цитат больше чем надо, для того, что бы предотвратить 2-ю итерацию
         */

        BashImReader bashImReader = new BashImReader();
        List<Quote> quotes = bashImReader.readQuotes(); // Читаем чуть больше чем надо

        for(int i = 0; i < (numOfQuotes / NUMBER_OF_QUOTES_THAT_CAN_BE_READ_AT_ONCE); i++) {
            quotes.addAll(bashImReader.readQuotes());
        }

        try {
            quoteDAO.insert(quotes, numOfQuotes);  // Добавляем цитаты в БД
        } catch(NotAllQuotesWereAddedException e) {
            // Добавиляем оставшиеся цитаты
            writeNewQuotesIntoDB(numOfQuotes - e.howManyQuotesWereNotAdded());
        } catch(DBSystemException e) {
            e.printStackTrace();
        }
    }


    /**
     * Получить следующую цитату согласно пользовательским  предпочтениям
     *
     * @param userSet Содержит пользовательские настройки для выборки цитат
     * @return Строку содержащую текстовую репрезентацию цитаты
     */
    public String nextQuote(UserSettings userSet) {
        /**
         * Запоминаем пользовательские настройки.
         *
         * Если список цитат пуст или предпочтения пользователя изменились - загружаем цитаты согласно
         *  предпочтеням пользователя и выдаем следующую цитату из списка.
         *
         * Иначе - выдаем следующую цитату из текущего списка. Если цитат в списке осталось меньше 15 - загружаем еще 75
         *
         * Цитату возвращаемую пользователем отмечаем в БД как просмотренную.
         */


        boolean isFirstLaunch = ((quoteList == null) && (this.userSettings == null));

        if(isFirstLaunch) {

            readQuotes(userSet);
            // Проверяем есть ли цитаты, соответствующие запросу
            if(quoteList.size() == 0) {
                userSettings = new UserSettings(userSet); // Запоминаем настройки пользователя
                return "Цитат с выбраными параметрами нет.";
            }
            currentQuote = quoteList.get(0);
            viewedQuotes = new ArrayList<Quote>();

        } else if(! this.userSettings.equals(userSet)) { // настройки пользователя изменились
            readQuotes(userSet);
            // Проверяем есть ли цитаты, соответствующие запросу
            if(quoteList.size() == 0) {
                userSettings = new UserSettings(userSet); // Запоминаем настройки пользователя
                return "Цитат с выбранными параметрами нет :(";
            }
            currentQuote = quoteList.get(0);
            viewedQuotes = new ArrayList<Quote>();
        } else { // настройки пользователя не изменились
            int currentQuoteIndex = quoteList.indexOf(currentQuote);
            int nextQuoteIndex = currentQuoteIndex + 1;
            if(nextQuoteIndex != quoteList.size()) {  // Если в списке есть не просмотренные еще цитаты - выдаем пользователю очередную.
                currentQuote = quoteList.get(nextQuoteIndex);
            } else {
                userSettings = new UserSettings(userSet); // Запоминаем настройки пользователя
                return "Цитаты с выбранными параметрами закончились :(\n\nВы можете изменить параметры показа цитат, или загрузить новые цитаты.";
            }


            /* Если осталось мало цитат для показа пользователю скачаем еще и обновим их список.
            *   Однако необходимо учитывать критерии для циатат, выставленные пользователем.
            *   Так, если выбраны только избранные цитаты, скачивание новых цитат, которые по умолчанию изначально не избранные, бесполезно.
            */
            /*if(! userSet.isOnlyFavorite()) {
                if(quoteList.size() < 15) {
                    writeNewQuotesIntoDB(75);
                    readQuotes(userSet);
                    // Т.к. среди полученных цитат будут те, которые пользователь уже просмотрел, их показывать не будем.
                    quoteList.removeAll(viewedQuotes);
                    if(quoteList.size() == 0) {
                        return "Цитат с выбраными параметрами нет.";
                    }
                    currentQuote = quoteList.get(0);
                }
            }*/

        }

        userSettings = new UserSettings(userSet); // Запоминаем настройки пользователя
        this.viewedQuotes.add(currentQuote); // Запоминаем циату как просмотеренную среди данного списка циатат (полученного по опред)
        makeViewed(); // Отмечаем цитату в БД как просмотенную

        return currentQuote.toString();
    }


    /**
     * Прочитать цитаты с БД
     *
     * @param userSet Содержит пользовательские настройки для выборки цитат
     */
    private void readQuotes(UserSettings userSet) {

        // Получаем все цитаты
        quoteList = null;
        try {
            quoteList = quoteDAO.getAllByParameters(userSet.isByRating(), userSet.getYear(), userSet.getMonth(), userSet.isOnlyFavorite(), userSet.isOnlyNewQuotes());
        } catch(DBSystemException e) {
            e.printStackTrace();
        }
    }


    /**
     * Проверяем если в БД меньше 50 непрочитанных цитат - загружаем еще 125 новых
     * Метод вызывается при старте программы.
     */
    public void addQuotesIntoDbIfThereAreFewNotViewedLeft() {

        // Получаем все непросмотренные цитаты
        List<Quote> quotes = null;
        try {
            quotes = quoteDAO.getAllByParameters(false, 0, 0, false, true); // не сортируем по рейтингу, за все года и за все месяца, не избранные
        } catch(DBSystemException e) {
            e.printStackTrace();
        }

        if(quotes.size() < 50) {
            writeNewQuotesIntoDB(125);
        }
    }

    /**
     * Метод отмечает текущую цитату как избранную в БД.
     */
    public void makeFavorite() {

        try {
            quoteDAO.makeFavorite(currentQuote);
        } catch(DBSystemException e) {
            e.printStackTrace();
        }

    }

    /**
     * Метод отмечает текущую цитату как просмотренную в БД.
     */
    private void makeViewed() {

        try {
            quoteDAO.makeViewed(currentQuote);
        } catch(DBSystemException e) {
            e.printStackTrace();
        }
    }

}
