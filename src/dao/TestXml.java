package dao;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import service.Quote;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

public class TestXml {

    public static void main(String[] args) {

        /*Quote testQuote = new Quote();
        testQuote.setId(18236);
        testQuote.setFavorite(false);
        testQuote.setMonth(1);
        testQuote.setYear(2014);
        testQuote.setViewed(false);
        testQuote.setRating(24589);
        testQuote.setText("Колобок повесился...(");

        XStream xStream = new XStream(new JettisonMappedXmlDriver()); //new StaxDriver()
        xStream.alias("Quote", Quote.class);

        String xml = xStream.toXML(testQuote);
        System.out.println(xml);

        StringBuilder stringBuilder = new StringBuilder(xml);
        int start = stringBuilder.indexOf("Колобок повесился...(");
        int  end = stringBuilder.lastIndexOf("Колобок повесился...(");
        stringBuilder.replace(start, end, "Колобок повесился...( И так ему и надо!! Обманщик хренов!!");

        System.out.println(stringBuilder.toString() + "\n\n");

        Quote newTestQuote = (Quote)xStream.fromXML(stringBuilder.toString());
        System.out.println(newTestQuote);

*/


        /*DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document = null;
        try {
            if(documentBuilder != null) {
                document = documentBuilder.parse(new File("F:\\Java programming\\Database Management System\\3 XML DATA\\Bookstore1.xml"));
            }
        } catch(SAXException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println(document); */

    }

}
