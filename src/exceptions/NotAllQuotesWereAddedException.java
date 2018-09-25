package exceptions;


public class NotAllQuotesWereAddedException extends DBSystemException {

    private int numberOfNotAddedQuotes;

    public NotAllQuotesWereAddedException(int num) {
        numberOfNotAddedQuotes = num;
    }

    public int howManyQuotesWereNotAdded() {
        return numberOfNotAddedQuotes;
    }

}
