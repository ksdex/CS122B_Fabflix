package funcScripts.xmlParserHandler;

import dataClass.StarsInMoviesRecordClass;
import funcScripts.HelperFunc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class StarsInMoviesParserHandler extends DefaultHandler  {
    private List<StarsInMoviesRecordClass> starsInMoviesRecord = new ArrayList<>();

    //to maintain context
    private String tempVal;
    private String currentMovieId;
    private StarsInMoviesRecordClass tempStarsInMovieRecord;
    private int starsInMoviesRecordCount;
    private boolean recordIsValid;

    public List<StarsInMoviesRecordClass> getStarsInMovieRecordList(){
        return starsInMoviesRecord;
    }


    //Event Handlers
    public void startDocument() throws SAXException {
        HelperFunc.initializeLogFile("StarInMoviesXMLParser");
        starsInMoviesRecordCount = 0;
        HelperFunc.xmlHandlerLog("Start parsing StarInMoviesXML.");
    }


    public void endDocument() throws SAXException {
        HelperFunc.printToConsole(starsInMoviesRecordCount);
        HelperFunc.xmlHandlerLog("Finish parsing StarInMoviesXML. Data count: " + Integer.toString(starsInMoviesRecordCount) + ".");
        HelperFunc.closeLogFile();
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            //create a new instance of employee
            HelperFunc.printToConsole(tempStarsInMovieRecord);
            tempStarsInMovieRecord = new StarsInMoviesRecordClass();
            recordIsValid = true;
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
    }



    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("f")){
            HelperFunc.printToConsole("film: " + tempVal);
            tempStarsInMovieRecord.movieId = tempVal;
        }
        else if (qName.equalsIgnoreCase("t")){
            HelperFunc.printToConsole("title: " + tempVal);
            tempStarsInMovieRecord.movieTitle = tempVal;
        }
        else if (qName.equalsIgnoreCase("a")) {
            //add it to the list
            if(!tempVal.equalsIgnoreCase("s a") ) {
                HelperFunc.printToConsole("Actor: " + tempVal);
                tempStarsInMovieRecord.add(tempVal);
            }
        }
        else if (qName.equalsIgnoreCase("filmc")) {
            //add it to the list
            HelperFunc.printToConsole("End of filmc: " + tempVal);
            if(recordIsValid) {
                starsInMoviesRecord.add(tempStarsInMovieRecord);
                starsInMoviesRecordCount++;
            }
        }
    }
}
