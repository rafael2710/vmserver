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
    private String name;
    private String IP;
    private int RAMSize;
    private int hardDiskSize;

    

    // Methods
    /**
     * This method must be called whenever a computer need to be shutdown
     * @return true if the computer was successfully shutdown
     */
    public boolean shutdown(){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * This method must be called whenever a computer need to be restarted
     * @return true if the computer was successfully restarted
     */
    public boolean restart(){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * returns information (details) about computer status
     * @return an xml element with the result
     */
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
    /**
     * set the name for the computer
     * @param name the new name
     */
    public void setName(String name){
        this.name = name;
    }
    /**
     * get the name of the computer
     * @return the name
     */
    public String getName(){
        return name;
    }
    /**
     * set the IP address for the computer
     * @param IP the new IP address
     */
    public void setIP(String IP){
        this.IP = IP;
    }
    /**
     * get the IP address of the computer
     * @return the IP address
     */
    public String getIP(){
        return IP;
    }
    /**
     * set the RAM memory size for the computer
     * @param RAMSize the RAM memory new size
     */
    public void setRAMSize(int RAMSize){
        this.RAMSize = RAMSize;
    }
    /**
     * get the RAM memory size of the computer
     * @return the RAM memory size
     */
    public int getRAMSize(){
        return RAMSize;
    }
    /**
     * set the hard disk size of the computer
     * @param hardDiskSize the new hard disk size
     */
    public void setHardDiskSize(int hardDiskSize){
        this.hardDiskSize = hardDiskSize;
    }
    /**
     * get the hard disk size
     * @return the hard disk size
     */
    public int getHardDiskSize(){
        return hardDiskSize;
    }
}
