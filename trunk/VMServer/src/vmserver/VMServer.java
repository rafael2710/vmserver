package vmserver;

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

 //   public String hello(String name) {
 //       return "Hello "+name;
 //   }

    /**
     * This method creates a virtual machine and instiate it on a physical server
     * of the network. All relevants informations are passed inside the OMElement
     * parameter received by the method.
     *
     * @param element
     * @return an OMElement with the results of the operation
     */
    public OMElement createVM(OMElement element){
        element.build();
        element.detach();

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "Virtual Machine created\nAttributes:\n";
        while(it.hasNext()){
            ele.add((OMElement) it.next());
            returnText = returnText+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
        }

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://vmserver/xsd", "vsm");
        OMElement method = fac.createOMElement("createVMResponse", omNs);
        method.addChild(fac.createOMText(returnText)); 

        return method;
    }

    public OMElement migrateVM(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement getVMStatus(OMElement element){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public OMElement getPhyStatus(OMElement element){
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
