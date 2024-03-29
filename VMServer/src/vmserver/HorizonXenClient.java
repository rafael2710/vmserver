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

public class HorizonXenClient {

    private String URI = "http://vmserver/xsd";
    //private String URI = "http://meier.gta.ufrj.br:8080/axis2/services/VMServer/xsd";
    private String PREFIX = "";
    //private String PREFIX = "hxpm";//horizon xen prototype message

    /**
     * Define the Endpoint Reference for the default client
     */
    public EndpointReference targetEPR = new EndpointReference("http://localhost:8080/axis2/services/VMServer");
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
    //public static OMElement createVirtualMachinePayload(PhysicalServer phyServer, VitualMachine vm) {
    public OMElement createVirtualMachinePayload(String phyServer, String vmName, String vmIP, String vmRAM, String vmDiskSize) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement element = fac.createOMElement("createVirtualMachine", omNs);
        
        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        element.addChild(value);
        // Virtual Machine Name
        value = fac.createOMElement("vmName", omNs);
        value.addChild(fac.createOMText(value, vmName));
        element.addChild(value);
        // Virtual Machine IP Address
        value = fac.createOMElement("vmIP", omNs);
        value.addChild(fac.createOMText(value, vmIP));
        element.addChild(value);
        // Virtual Machine RAM Memory Size
        value = fac.createOMElement("vmRAM", omNs);
        value.addChild(fac.createOMText(value, vmRAM));
        element.addChild(value);
        // Virtual Machine Hard Disk Size
        value = fac.createOMElement("vmDiskSize", omNs);
        value.addChild(fac.createOMText(value, vmDiskSize));
        element.addChild(value);

        return element;
        
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
    public OMElement migrateVirtualMachinePayload(String sourcePhyServer, String destPhyServer, String vmName, String live){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("migrateVirtualMachine", omNs);

        // Attributes

        // Source Physical Server IP
        OMElement value = fac.createOMElement("sourcePhyServer", omNs);
        value.addChild(fac.createOMText(value, sourcePhyServer));
        method.addChild(value);
        // Destination Physical Server IP
        value = fac.createOMElement("destPhyServer", omNs);
        value.addChild(fac.createOMText(value, destPhyServer));
        method.addChild(value);
        // Virtual Machine Name
        value = fac.createOMElement("vmName", omNs);
        value.addChild(fac.createOMText(value, vmName));
        method.addChild(value);
        // Live Migration Boolean
        value = fac.createOMElement("live", omNs);
        value.addChild(fac.createOMText(value, live));
        method.addChild(value);

        return method;
    }

    /**
     * Creates the payload for the message requesting the status of a virtual machine.
     * @param phyServer the machine that hosts the virtual machine
     * @param VMName the name of the virtual machine
     * @return the element with the requested payload
     */
    public OMElement getVirtualMachineStatusPayload(String phyServer, String VMName){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("getVirtualMachineStatus", omNs);

        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        method.addChild(value);
        // Virtual Machine Name
        value = fac.createOMElement("vmName", omNs);
        value.addChild(fac.createOMText(value, VMName));
        method.addChild(value);

        return method;
    }

    /**
     *
     * @param a
     * @return
     */
    public OMElement sanityTestPayload(String a){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("sanityTest", omNs);
        OMElement value = fac.createOMElement("testString", omNs);
        value.addChild(fac.createOMText(value, a));
        method.addChild(value);
        return method;
    }

    /**
     * Creates the payload for the message requesting the status of a physical machine
     * that hosts virtual domains
     * @param phyServer the name of the physical server
     * @return the OMElement with the payload
     */
    public OMElement getPhysicalServerStatusPayload(String phyServer){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("getPhysicalServerStatus", omNs);

        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        method.addChild(value);

        return method;
    }

    public OMElement shutdownPhysicalServerPayload(String phyServer){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("shutdownPhysicalServer", omNs);

        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        method.addChild(value);

        return method;
    }

    public OMElement shutdownVirtualMachinePayload(String phyServer, String VMName){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("shutdownVirtualMachine", omNs);

        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        method.addChild(value);
        // Virtual Machine Name
        value = fac.createOMElement("VMName", omNs);
        value.addChild(fac.createOMText(value, VMName));
        method.addChild(value);

        return method;
    }

    public OMElement destroyVirtualMachinePayload(String phyServer, String VMName){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("destroyVirtualMachine", omNs);

        // Attributes

        // Physical Server IP
        OMElement value = fac.createOMElement("phyServer", omNs);
        value.addChild(fac.createOMText(value, phyServer));
        method.addChild(value);
        // Virtual Machine Name
        value = fac.createOMElement("VMName", omNs);
        value.addChild(fac.createOMText(value, VMName));
        method.addChild(value);

        return method;
    }

    public OMElement createVirtualNetworkPayload(Vector<String> phyServers, Vector<String> VMNames, Vector<String> IPs, Vector<String> RAMs) throws Exception{
        if(phyServers.size()!=VMNames.size()||phyServers.size()!=IPs.size()||RAMs.size()!=IPs.size()){
            throw new Exception("Vectors sizes do not match!");
        }
        int i = 0;
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("createVirtualNetwork", omNs);

        // Attributes

        OMElement value = fac.createOMElement("nodeCount", omNs);
        value.addChild(fac.createOMText(value, Integer.toString(phyServers.size())));
        method.addChild(value);
        // Physical Servers IP
        for(i=0;i<phyServers.size();i++){
            value = fac.createOMElement("phyServer", omNs);
            value.addChild(fac.createOMText(value, phyServers.elementAt(i)));
            method.addChild(value);
        }
        // Virtual Machine Name
        for(i=0;i<phyServers.size();i++){
            value = fac.createOMElement("VMName", omNs);
            value.addChild(fac.createOMText(value, VMNames.elementAt(i)));
            method.addChild(value);
        }

        for(i=0;i<phyServers.size();i++){
            value = fac.createOMElement("vmIP", omNs);
            value.addChild(fac.createOMText(value, IPs.elementAt(i)));
            method.addChild(value);
        }

        for(i=0;i<phyServers.size();i++){
            value = fac.createOMElement("vmRAM", omNs);
            value.addChild(fac.createOMText(value, RAMs.elementAt(i)));
            method.addChild(value);
        }

        return method;
    }

    public  OMElement getRegisteredNodesPayload(){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("getRegisteredNodes", omNs);
        return method;
    }

    public OMElement topologyDiscoverPayload(){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("topologyDiscover", omNs);
        return method;
    }

    public  OMElement registerNodesPayload(Vector<PhysicalServer> Nodes){
        OMFactory fac = OMAbstractFactory.getOMFactory();

        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        // Set the required operation
        OMElement method = fac.createOMElement("registerNodes", omNs);

        for(int i = 0;i<Nodes.size();i++){
            OMElement value = fac.createOMElement("Node", omNs);
            OMElement  value2 = fac.createOMElement("NodeName", omNs);
            value2.addChild(fac.createOMText(Nodes.elementAt(i).getName()));
            value.addChild(value2);
            value2 = fac.createOMElement("NodePK", omNs);
            value2.addChild(fac.createOMText(Nodes.elementAt(i).getPK()));
            value.addChild(value2);
            method.addChild(value);
        }
        return method;
    }

    


    public static void main(String[] args) {
        try {
            HorizonXenClient hxc = new HorizonXenClient();
            // set options to send message
            Options options = new Options();
            options.setTo(hxc.targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
            // create client
            ServiceClient sender = new ServiceClient();
            sender.setOptions(options);

            // creating message payload
            OMElement messagePayload = hxc.createVirtualMachinePayload("phy","vmname","10.10.0.1","500","5");
            // send message and wait for the server response
            OMElement result = sender.sendReceive(messagePayload);
            // print some output
            System.out.println("result: "+result.getFirstElement().getFirstElement().getText());

 //           messagePayload = hxc.migrateVirtualMachinePayload("engenhao","inga","test_vmserver","true");
   ///         result = sender.sendReceive(messagePayload);
 //           System.out.println("result: "+result.toString());


//            messagePayload = hxc.getPhysicalServerStatusPayload("inga");
  //          result = sender.sendReceive(messagePayload);
    //        System.out.println("result: "+result.toString());
//
//            messagePayload = hxc.shutdownPhysicalServerPayload("phyS");
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());
//
  //          messagePayload = hxc.getVirtualMachineStatusPayload("inga", "test_vmserver");
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());
//
//            messagePayload = hxc.shutdownVirtualMachinePayload("phy", "vmname");
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());
//
//            messagePayload = hxc.destroyVirtualMachinePayload("phy", "vmname");
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());
//
//            Vector<String> phyServers = new Vector<String>();
//            phyServers.add("phyS_1");
//            phyServers.add("phyS_2");
//            phyServers.add("phyS_3");
//            Vector<String> VMNames = new Vector<String>();
//            VMNames.add("vm_1");
//            VMNames.add("vm_2");
//            VMNames.add("vm_3");
//            messagePayload = hxc.createVirtualNetworkPayload(phyServers, VMNames);
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
