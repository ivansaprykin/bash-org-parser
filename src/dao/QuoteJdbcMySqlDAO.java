package dao;

import exceptions.DBSystemException;
import exceptions.NotAllQuotesWereAddedException;
import service.Quote;
import service.QuoteDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuoteJdbcMySqlDAO implements QuoteDAO {

    static {
        QuoteMySqlJdbcFactory.initDriver();
    }

    @Override
    public void insert(List<Quote> quotes, int numberOfQuotesToInsert) throws DBSystemException {
        //INSERT INTO `quotes`.`quote` (`id`, `text`, `rating`, `year`, `month`, `favorite`, `viewed`) VALUES ('301', 'Ну просто офигеть какая смешная цитата', '15000', '2013', '10', '0', '0');

        Connection connection = QuoteMySqlJdbcFactory.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String insertQuoteIntoTableSql = "INSERT INTO `quotes`.`quote` (`id`, `text`, `rating`, `year`, `month`, `favorite`, `viewed`) VALUES(?,?,?,?,?,?,?)";

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(insertQuoteIntoTableSql);
            //resultSet = preparedStatement.executeQuery();
            int quotesLeftToInsert = numberOfQuotesToInsert;

            for(Quote quote : quotes) {
                try {
                    preparedStatement.setInt(1, quote.getId());
                    preparedStatement.setString(2, quote.getText());
                    preparedStatement.setInt(3, quote.getRating());
                    preparedStatement.setInt(4, quote.getYear());
                    preparedStatement.setInt(5, quote.getMonth());
                    preparedStatement.setBoolean(6, quote.isFavorite());
                    preparedStatement.setBoolean(7, quote.isViewed());

                    preparedStatement.executeUpdate(); // execute insert SQL statement
                    quotesLeftToInsert--;
                    if(quotesLeftToInsert == 0) {
                        break;
                    }
                } catch(SQLException e) {
                    // do nothing - отправляем цитату, ктоторая уже ксть в БД
                }
            }
            if(quotesLeftToInsert != 0) { // отправили не все циатаы
                throw new NotAllQuotesWereAddedException(quotesLeftToInsert);
            }
            connection.commit();
        } catch(SQLException e) {
            rollbackQuietly(connection);
            throw new DBSystemException("Can't execute SQL " + insertQuoteIntoTableSql, e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(preparedStatement);
            closeQuietly(connection);
        }

    }

    @Override
    public void makeFavorite(Quote quote) throws DBSystemException {
        //UPDATE `quotes`.`quote` SET `favorite`='1' WHERE `id`='18159';

        Connection connection = QuoteMySqlJdbcFactory.getConnection();
        Statement statement = null;
        // Создаем запрос
        String query = "UPDATE `quotes`.`quote` SET `favorite`='1' WHERE `id`='" + quote.getId() + " '";

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            /*
             * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement
             * or an SQL statement that returns nothing, such as an SQL DDL statement.
             */
            statement.executeUpdate(query);

            connection.commit();

        } catch(SQLException e) {
            rollbackQuietly(connection);
            throw new DBSystemException("Can't execute SQL " + query, e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    @Override
    public void makeNotFavorite(Quote quote) {

    }

    @Override
    public void makeViewed(Quote quote) throws DBSystemException {
        //UPDATE `quotes`.`quote` SET `viewed`='1' WHERE `id`='18159';

        Connection connection = QuoteMySqlJdbcFactory.getConnection();
        Statement statement = null;
        // Создаем запрос
        String query = "UPDATE `quotes`.`quote` SET `viewed`='1' WHERE `id`='" + quote.getId() + " '";

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            /*
             * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement
             * or an SQL statement that returns nothing, such as an SQL DDL statement.
             */
            statement.executeUpdate(query);

            connection.commit();

        } catch(SQLException e) {
            rollbackQuietly(connection);
            throw new DBSystemException("Can't execute SQL " + query, e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    @Override
    public List<Quote> getAllByParameters(boolean byRating, int year, int month, boolean onlyFavorite, boolean onlyNotViewed) throws DBSystemException {

        Connection connection = QuoteMySqlJdbcFactory.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String query = null;

        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            // Создаем запрос
            query = readQuerySql(byRating, year, month, onlyFavorite, onlyNotViewed);

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            List<Quote> result = new ArrayList<Quote>();
            while(resultSet.next()) {

                Quote quote = new Quote();

                quote.setId(resultSet.getInt("id"));
                quote.setText(resultSet.getString("text"));
                quote.setRating(resultSet.getInt("rating"));
                quote.setYear(resultSet.getInt("year"));
                quote.setMonth(resultSet.getInt("month"));
                quote.setFavorite(resultSet.getBoolean("favorite"));
                quote.setViewed(resultSet.getBoolean("viewed"));

                result.add(quote);
            }

            connection.commit();
            return result;

        } catch(SQLException e) {
            rollbackQuietly(connection);
            throw new DBSystemException("Can't execute SQL " + query, e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    /**
     * Возвращает SQL запрос для поиска цитат, соответствующих переданым параметрам
     * @param byRating      Сортировать ли по убыванию рейтинга
     * @param year          Цитаты за определенный год. 0 - за все года
     * @param month         Цитаты за определенный месяц. 0 - за все месяц
     * @param onlyFavorite  Только избранные цитаты
     * @param onlyNotViewed Только не просмотренные цитаты
     * @return Строка содержащая SQL запрос.
     */
    private String readQuerySql(boolean byRating, int year, int month, boolean onlyFavorite, boolean onlyNotViewed) {

        StringBuilder query = new StringBuilder("select * from quote");

        boolean needSortByYear = (year != 0); // 0 значит за все года
        boolean needSortByMonth = (month != 0); // 0 значит за все месяца

        if(needSortByYear) {
            query.append(" where quote.year = ");
            query.append(year);

            if(needSortByMonth) {
                query.append(" and quote.month = ");
                query.append(month);
            }
            if(onlyFavorite) {
                query.append(" and quote.favorite = true");
            }

            if(onlyNotViewed) {
                query.append(" and quote.viewed = false");
            }
        } else if(needSortByMonth) {
            query.append(" where quote.month = ");
            query.append(month);

            if(needSortByYear) {
                query.append(" and quote.year = ");
                query.append(year);
            }
            if(onlyFavorite) {
                query.append(" and quote.favorite = true");
            }

            if(onlyNotViewed) {
                query.append(" and quote.viewed = false");
            }
        } else {
            if(onlyFavorite) {
                query.append(" where quote.favorite = true");
            }

            if(onlyNotViewed) {
                query.append(" where quote.viewed = false");
            }
        }

        if(byRating) { // сотрировать по рейтингу по убыванию
            query.append(" order by rating desc");
        }

        return query.toString();
    }

    private void rollbackQuietly(Connection connection) {
        if(connection != null) {
            try {
                connection.rollback();
            } catch(SQLException e) {
                // do nothing т.к. более окончательных методов в JDBC нет
            }
        }
    }

    private void closeQuietly(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch(SQLException e) {
                // do nothing т.к. более окончательных методов в JDBC нет
            }
        }
    }

    private void closeQuietly(ResultSet resultSet) {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch(SQLException e) {
                // do nothing т.к. более окончательных методов в JDBC нет
            }
        }
    }

    private void closeQuietly(Statement statement) {
        if(statement != null) {
            try {
                statement.close();
            } catch(SQLException e) {
                // do nothing т.к. более окончательных методов в JDBC нет
            }
        }
    }

}