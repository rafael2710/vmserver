package vmserver;

import java.util.Vector;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;


/**
 *
 * @author Rafael
 */

public class VMClient {

    private static EndpointReference targetEPR = new EndpointReference("http://localhost:8080/axis2/services/VMServer");
    //reference need to be changed

    /**
     * This method will be called whenever a new virtual machine need to be created
     * on the network.
     *
     * Payload created:
     * <vsm:createVM xmlns:vsm="http://vmserver/xsd"><vsm:phyServer>phy</vsm:phyServer><vsm:vmName>vmname</vsm:vmName><vsm:vmIP>10.10.0.1</vsm:vmIP><vsm:vmRAM>500</vsm:vmRAM><vsm:vmDiskSize>5</vsm:vmDiskSize></vsm:createVM>
     *
     * @param phyServer ip address of the physical machine that will host the new virtual machine
     * @param vmName Name for the virtual machine
     * @param vmIP desired ip address for the new virtual machine
     * @param vmRAM Size (in MegaBytes) for the RAM memory of the virtual machine
     * @param vmDiskSize Size (in GigaBytes) for the virtual machine disk
     *
     * @return true if the virtual machine was sucessfully created, false otherwise
     */
    public static OMElement createVMPayload(String phyServer, String vmName, String vmIP, String vmRAM, String vmDiskSize) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace("http://vmserver/xsd", "vsm");
        // Set the required operation
        OMElement method = fac.createOMElement("createVM", omNs);
        
        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        method.addChild(value);
        // Virtual Machine Name
        value = fac.createOMElement("vmName", omNs);
        value.addChild(fac.createOMText(value, vmName));
        method.addChild(value);
        // Virtual Machine IP Address
        value = fac.createOMElement("vmIP", omNs);
        value.addChild(fac.createOMText(value, vmIP));
        method.addChild(value);
        // Virtual Machine RAM Memory Size
        value = fac.createOMElement("vmRAM", omNs);
        value.addChild(fac.createOMText(value, vmRAM));
        method.addChild(value);
        // Virtual Machine Hard Disk Size
        value = fac.createOMElement("vmDiskSize", omNs);
        value.addChild(fac.createOMText(value, vmDiskSize));
        method.addChild(value);

        return method;
        
    }

    /**
     * This method will be called whenever a new virtual machine need to be migrated
     * on the network.
     *
     * @param sourcePhyServer ip address of the physical machine from where the virtual machine will be migrated
     * @param destPhyServer ip address of the physical machine the will host the virtual machine
     * @param vmName Name of the virtual machine
     * @param live true if the migration need to be live (without stopping its operation)
     *
     * @return true if the virtual machine was sucessfully migrated, false otherwise
     */
    public static OMElement migrateVMPayload(String sourcePhyServer, String destPhyServer, String vmName, String live){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public static OMElement getVMStatusPayload(String phyServer, String VMName){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public static OMElement getPhyStatusPayload(String phyServer){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public static OMElement shutdownPhyServerPayload(String phyServer){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public static OMElement shutdownVMPayload(String phyServer, String VMName){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public static OMElement destroyVMPayload(String phyServer, String VMName){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    public static OMElement createVNetPayload(Vector<String> phyServers, Vector<String> VMNames){
          throw new UnsupportedOperationException("Not yet implemented");
    }


    public static void main(String[] args) {
        try {
            // creating message payload
            OMElement messagePayload = createVMPayload("phy","vmname","10.10.0.1","500","5");
            // set options to send message
            Options options = new Options();
            options.setTo(targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
            // create client
            ServiceClient sender = new ServiceClient();
            sender.setOptions(options);
            // send message and wait for the server response
            OMElement result = sender.sendReceive(messagePayload);
            // print some output
            System.out.println("result: "+result.getFirstElement().getFirstElement().getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
