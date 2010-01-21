package vmserver;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.EndpointReference;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

/**
 *
 * @author santos
 */
public class client {
    public static void main(String[] args) {
        try {


            VMClient vmc = new VMClient();
            // set options to send message
            Options options = new Options();
            vmc.targetEPR.setAddress("http://meier:8080/axis2/services/VMServer");
            vmc.targetEPR.setAddress("http://localhost:8080/axis2/services/VMServer");
            options.setTo(vmc.targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
            // create client
            ServiceClient sender = new ServiceClient();
            sender.setOptions(options);

            // creating message payload
            OMElement messagePayload = vmc.createVirtualMachinePayload("phy","vmname","10.10.0.1","500","5");
            // send message and wait for the server response

               messagePayload = vmc.testPayload("abcdef");

            OMElement result = sender.sendReceive(messagePayload);
            // print some output
            System.out.println("result: "+result.getFirstElement().getFirstElement().getText());

 //           messagePayload = vmc.migrateVirtualMachinePayload("engenhao","inga","test_vmserver","true");
   ///         result = sender.sendReceive(messagePayload);
 //           System.out.println("result: "+result.toString());


//            messagePayload = vmc.getPhysicalServerStatusPayload("inga");
  //          result = sender.sendReceive(messagePayload);
    //        System.out.println("result: "+result.toString());
//
//            messagePayload = vmc.shutdownPhysicalServerPayload("phyS");
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());
//
 //           messagePayload = vmc.getVirtualMachineStatusPayload("inga", "test_vmserver");
   //         result = sender.sendReceive(messagePayload);
     //       System.out.println("result: "+result.toString());
//
//            messagePayload = vmc.shutdownVirtualMachinePayload("phy", "vmname");
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());
//
//            messagePayload = vmc.destroyVirtualMachinePayload("phy", "vmname");
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
//            messagePayload = vmc.createVirtualNetworkPayload(phyServers, VMNames);
//            result = sender.sendReceive(messagePayload);
//            System.out.println("result: "+result.toString());

        } catch (org.apache.axis2.AxisFault e) {
            
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
         catch (Exception ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
