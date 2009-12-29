package vmserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 *
 * @author Rafael
 */
public class VMServer {

    Vector<PhysicalServer> phyServers;
    String URI = "http://vmserver/xsd";
    String PREFIX = "vsm";
    String DATABASE = "data.txt";
    String PS_TOKEN = "|";
    String VM_TOKEN = ";";
    String VM_PROPERTIES_TOKEN = ",";
    /* File Format
     * PHY_SERVER_1_NAME|PHY_SERVER_1_IP|VM_COUNT|VM_1_NAME , VM_1_IP ; VM_2_NAME , VM_2_IP
     * PHY_SERVER_2_NAME|PHY_SERVER_2_IP|VM_COUNT|VM_1_NAME , VM_1_IP ; VM_2_NAME , VM_2_IP
     */

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
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement method = fac.createOMElement("createVMResponse", omNs);
        method.addChild(fac.createOMText(returnText)); 

        return method;
    }

    public OMElement migrateVM(OMElement element){
        element.build();
        element.detach();

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "Virtual Machine migrated\nAttributes:\n";
        while(it.hasNext()){
            ele.add((OMElement) it.next());
            returnText = returnText+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
        }
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement method = fac.createOMElement("migrateVMResponse", omNs);
        method.addChild(fac.createOMText(returnText));

        return method;
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

    /*Method to save the vm on the database (text file)*/
    private void SaveVM(VirtualMachine vm){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private VirtualMachine LoadVM(String vmName){
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void SavePS(PhysicalServer ps){
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private PhysicalServer LoadPS(String psName){
        PhysicalServer ps = null;
        int i = 0;
        String strLine;
        try {
            FileInputStream fstream = new FileInputStream(DATABASE);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            VirtualMachine vm = null;
            // for each physical server
            while ((strLine = br.readLine()) != null)   {
                //look for the desired physical server
                if(strLine.startsWith(psName+"|")){
                    ps = new PhysicalServer();
                    StringTokenizer st = new StringTokenizer(strLine, PS_TOKEN);
                    ps.setName(st.nextToken());
                    ps.setIP(st.nextToken());
                    //String a = st.nextToken();
                    int max = (new Integer(st.nextToken())).intValue();
                    String vmList = st.nextToken();
                    for(i=0;i<max;i++){                        
                        // VM_1_NAME,VM_1_IP;VM_2_NAME,VM_2_IP
                        StringTokenizer st2 = new StringTokenizer(vmList, VM_TOKEN);
                        vm = new VirtualMachine();
                        while(st2.hasMoreTokens()){
                            StringTokenizer st3 = new StringTokenizer(st2.nextToken(), VM_PROPERTIES_TOKEN);
                            vm.setName(st3.nextToken());
                            vm.setIP(st3.nextToken());
                            // aqui talvez pegar dados completos da vm pela XenAPI
                            ps.addVM(vm);
                        }
                    }
                    break;
                }
            }
            //Close the input stream
            in.close();
        } catch (Exception ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
        }


        return ps;
    }

     public static void main(String[] args) {
        PhysicalServer ps = new PhysicalServer();
        VMServer vm = new VMServer();
        ps = vm.LoadPS("PHY_SERVER_1_NAME");
    }

}
