/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vmserver;


/**
 *
 * @author Rafael
 */

import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import java.util.HashMap;
public class StockQuoteService {
    private HashMap map = new HashMap();

    public OMElement getPrice(OMElement element) throws XMLStreamException {
        element.build();
        element.detach();

        OMElement symbolElement = element.getFirstElement();
        String symbol = symbolElement.getText();

        String returnText = "42";
        Double price = (Double) map.get(symbol);
        if(price != null){
            returnText  = "" + price.doubleValue();
        }
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs =
            fac.createOMNamespace("http://vmserver/xsd", "tns");
        OMElement method = fac.createOMElement("getPriceResponse", omNs);
        OMElement value = fac.createOMElement("price", omNs);
        value.addChild(fac.createOMText(value, returnText));
        method.addChild(value);
        System.err.println("DEBUG: "+value);
        return method;
    }

    public void update(OMElement element) throws XMLStreamException {
        element.build();
        element.detach();

        OMElement symbolElement = element.getFirstElement();
        String symbol = symbolElement.getText();

        OMElement priceElement = (OMElement)symbolElement.getNextOMSibling();
        String price = priceElement.getText();

        map.put(symbol, new Double(price));
    }
}


