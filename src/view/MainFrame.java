package view;

import service.QuoteManager;
import service.UserSettings;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.HashMap;
import java.util.Map;


/**
 * Окно состоит из 3 панелей. Верхняя содержит кнопку загрузки цитат, и настройки их отображения,
 * на средней будет отображаться цитата, нижняя содержит 2 кнопки: следующая цитата и добавить цитату в избранное.
 */
public class MainFrame extends JFrame {

    private static final int NUMBER_OF_QUOTES_TO_DOWNLOAD = 70;
    private static final String MAKE_FAVORITE = "Добавить в избранное";

    private QuoteManager quoteManager;
    private UserSettings userSettings;

    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel bottomPanel;

    private JTextArea txtArea; // Для отображения цитат
    private Font font;


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch(Exception e)// ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
                {
                    JFrame.setDefaultLookAndFeelDecorated(true); // если ошибка - поставить стандартое отображение (вид)
                } finally {
                    new MainFrame().setVisible(true);
                }
            }
        });
   }



    public MainFrame() {

        super("BashReader");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        quoteManager = new QuoteManager();
        userSettings = new UserSettings();

        font = new Font("Lucida Console", Font.PLAIN, 14);

        createTopPanel();
        getContentPane().add(topPanel, BorderLayout.NORTH);

        createMiddlePanel();
        getContentPane().add(middlePanel, BorderLayout.CENTER);

        createBottomPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setSize(new Dimension(450, 600));
        setLocationRelativeTo(null); // установить окно по центру экрана

        quoteManager.addQuotesIntoDbIfThereAreFewNotViewedLeft();

    }

    private void createTopPanel() {

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Кнопка для загрузки NUMBER_OF_QUOTES_TO_DOWNLOAD новых цитат
        JButton loadQuotesButton = new JButton("Загрузить " + NUMBER_OF_QUOTES_TO_DOWNLOAD + " случайных цитат");
        loadQuotesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                quoteManager.writeNewQuotesIntoDB(NUMBER_OF_QUOTES_TO_DOWNLOAD);
            }
        });
        loadQuotesButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(loadQuotesButton);
        topPanel.add(Box.createVerticalStrut(10));

        // Панель содержит инструменты для выбора пользоватьских предпочтений (год, месяц, по рейтингу, ...)
        JPanel userSettingsPanel = createUserSettingsPanel();
        topPanel.add(userSettingsPanel);

    }

    private JPanel createUserSettingsPanel() {

        JPanel userSettingsPanel = new JPanel();

        // Панель содержащая выпадающие списки для выбора года и месяца
        JPanel datePanel = createMonthAndYearChosePanel();
        userSettingsPanel.add(datePanel, BorderLayout.WEST);

        // Панель содержащая чекбоксы для выбора режима сортировки цитат (по рецтингу) и редима показа(только не просмотренные, только избранные)
        JPanel checkBoxPanel = createCheckBoxPanel();
        userSettingsPanel.add(checkBoxPanel, BorderLayout.EAST);

        return userSettingsPanel;
    }

    private JPanel createCheckBoxPanel() {

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        JCheckBox byRating = new JCheckBox("По рейтингу");
        byRating.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                userSettings.changeByRating();
            }
        });
        checkBoxPanel.add(byRating);

        /*
         * Одновременно может быть включен только 1 чекбокс, т.к. избранные цитаты - те цитаты, которые пользователь
         * просмотрел, и отметил как избранные, не просмотренные не могут быть избранными.
         */

        ButtonGroup buttonGroup = new ButtonGroup();

        final JCheckBox onlyNewQuotes = new JCheckBox("Не прочитанные");
        buttonGroup.add(onlyNewQuotes);
        onlyNewQuotes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                userSettings.changeOnlyNewQuotes();
            }
        });
        checkBoxPanel.add(onlyNewQuotes);

        final JCheckBox onlyFavorite = new JCheckBox("Избранные");
        buttonGroup.add(onlyFavorite);
        onlyFavorite.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                userSettings.changeOnlyFavorite();
            }
        });
        checkBoxPanel.add(onlyFavorite);

        final JCheckBox allQuotes = new JCheckBox("Все цитаты");
        allQuotes.setSelected(true);
        buttonGroup.add(allQuotes);
        allQuotes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                userSettings.setOnlyFavorite(false);
                userSettings.setOnlyNewQuotes(false);
            }
        });
        checkBoxPanel.add(allQuotes);

        return checkBoxPanel;

    }

    private JPanel createMonthAndYearChosePanel() {

        final JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));

        // Выпадаюций список для выбора года
        String[] years = {"Все года", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004"};
        JComboBox<String> yearChooser = new JComboBox<String>(years);
        yearChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox box = (JComboBox) actionEvent.getSource();
                String year = (String) box.getSelectedItem();
                if(year.equals("Все года")) {
                    userSettings.setYear(0);
                } else {
                    userSettings.setYear(Integer.parseInt(year));
                }
            }
        });
        datePanel.add(yearChooser);
        datePanel.add(Box.createHorizontalStrut(30));

        // Выпадаюций список для выбора месяца
        String[] months = {"Все месяца", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Окрябрь", "Ноябрь", "Декабрь"};
        final Map<String, Integer> monthsMap = new HashMap<String, Integer>();
        for(int i = 0; i < months.length; i++) {
            monthsMap.put(months[i], i);
        }

        final JComboBox<String> monthChooser = new JComboBox<String>(months);
        monthChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox box = (JComboBox) actionEvent.getSource();
                String month = (String) box.getSelectedItem();

                userSettings.setMonth(monthsMap.get(month));
            }
        });
        datePanel.add(monthChooser);
        datePanel.add(Box.createHorizontalStrut(50));

        return datePanel;
    }

    private void createMiddlePanel() {

        middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());

        txtArea = new JTextArea();
        txtArea.setLineWrap(true); // enable line wrap to wrap text around
        txtArea.setWrapStyleWord(true); // words will not be cut off when wrapped around
        txtArea.setFont(font);
        txtArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtArea);
        middlePanel.add(scroll, BorderLayout.CENTER);

    }

    private void createBottomPanel() {

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

        JButton makeFavoriteButton = new JButton(MAKE_FAVORITE);
        makeFavoriteButton.setFont(font);
        makeFavoriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                quoteManager.makeFavorite();
            }
        });
        makeFavoriteButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(makeFavoriteButton);
        bottomPanel.add(Box.createVerticalStrut(1));

        JButton nextQuoteButton = new JButton("Следующая цитата");
        nextQuoteButton.setFont(font);
        nextQuoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                txtArea.setText(quoteManager.nextQuote(userSettings));
            }
        });
        nextQuoteButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        bottomPanel.add(nextQuoteButton);

    }

}