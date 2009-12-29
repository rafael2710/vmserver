package vmserver;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 *
 * @author Rafael
 */
public class Computer {
    // Fields
    String name;
    String IP;
    int RAMSize;
    int hardDiskSize;

    // Methods
    public boolean shutdown(){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public boolean restart(){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    // returns information (details) about computer status
    public OMElement status(){
        OMFactory fac = OMAbstractFactory.getOMFactory();
        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace("http://vmserver/xsd", "vsm");
        // Set the required operation
        OMElement method = fac.createOMElement("Computer Informations", omNs);

        OMElement value = fac.createOMElement("name", omNs);
        value.addChild(fac.createOMText(value, name));
        method.addChild(value);

        value = fac.createOMElement("IP", omNs);
        value.addChild(fac.createOMText(value, IP));
        method.addChild(value);

        value = fac.createOMElement("RAMSize", omNs);
        value.addChild(fac.createOMText(value, Integer.toString(RAMSize)));
        method.addChild(value);

        value = fac.createOMElement("hardDiskSize", omNs);
        value.addChild(fac.createOMText(value, Integer.toString(hardDiskSize)));
        method.addChild(value);

        return method;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setIP(String IP){
        this.IP = IP;
    }
    public String getIP(){
        return IP;
    }
    public void setRAMSize(int RAMSize){
        this.RAMSize = RAMSize;
    }
    public int getRAMSize(){
        return RAMSize;
    }
    public void setHardDiskSize(int hardDiskSize){
        this.hardDiskSize = hardDiskSize;
    }
    public int getHardDiskSize(){
        return hardDiskSize;
    }
}
