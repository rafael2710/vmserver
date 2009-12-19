package vmserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 *
 * @author Rafael
 */
public class VMServer {

    public String hello(String name) {
        return "Hello "+name;
    }

    public OMElement createVM(OMElement element){
        element.build();
        element.detach();

        Iterator it = element.getChildElements();

        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "Virtual Machine created\nAttributes:\n";
        while(it.hasNext()){
            ele.add((OMElement) it.next());
 //           System.err.println("DEBUG 4.1: "+ele.lastElement().toString());
 //           System.err.println("DEBUG 4.2: "+ele.lastElement().getLocalName());
            returnText = returnText+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
        }

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://vmserver/xsd", "tns");
        OMElement method = fac.createOMElement("createVMResponse", omNs);
        method.addChild(fac.createOMText(returnText)); 

        return method;
    }

    public OMElement migrateVM(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement VMStatus(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement phyStatus(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement shutdownPhyServer(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement shutdownVM(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement destroyVM(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement createVNet(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

}
