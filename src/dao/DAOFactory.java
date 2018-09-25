package dao;

import service.QuoteDAO;

public abstract class DAOFactory {

    // Список типов DAO поддерживаемых фабрикой
    public static final int DAO_MY_SQL_JDBC_FACTORY = 0;

    public static DAOFactory getFactory(int whichFactory) {
        switch(whichFactory) {
            case DAO_MY_SQL_JDBC_FACTORY: return new QuoteMySqlJdbcFactory();

            default: return null;
        }
    }

    // Методы для каждого DAO, который может быть создан.
    // Реализовывать эти методы должны конкретные фабрики.
    public abstract QuoteDAO createQuoteDAO();

}
