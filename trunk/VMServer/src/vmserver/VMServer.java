package vmserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    /**
     * TODO: Definir formatos de mensagens estritamente
     */

    private Vector<PhysicalServer> phyServers;
    private String URI = "http://vmserver/xsd";
    private String PREFIX = "vsm";
    private String DATABASE = "data.txt";
    private String PS_TOKEN = "|";
//    private String VM_TOKEN = ";";
//    private String VM_PROPERTIES_TOKEN = ",";
    /* File Format
     * PHY_SERVER_1_NAME|PHY_SERVER_1_IP
     * PHY_SERVER_2_NAME|PHY_SERVER_2_IP
     */


    public VMServer(){
        FileOutputStream fstream;
        try {
            fstream = new FileOutputStream(DATABASE,true);
        } catch (FileNotFoundException ex) {
            System.err.println("The file could not be opened or created");
        }
    }

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
        System.out.println("createVM message: "+element.toString());

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


        System.out.println("migrateVM message: "+element.toString());

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
        element.build();
        element.detach();
        System.out.println("getVMStatus message: "+element.toString());
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
//    private void SaveVM(VirtualMachine vm){
  //      throw new UnsupportedOperationException("Not yet implemented");
//    }
//    private VirtualMachine LoadVM(String vmName){
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    private void SavePS(PhysicalServer ps){
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(DATABASE, true));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(ps.getName()+"|"+ps.getIP()+"\n");
            bw.close();
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private PhysicalServer LoadPS(String psName){
        PhysicalServer ps = null;
        int i = 0;
        String strLine;
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(DATABASE));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // for each physical server
            while ((strLine = br.readLine()) != null){
                //look for the desired physical server
                if(strLine.startsWith(psName+"|")){
                    ps = new PhysicalServer();
                    StringTokenizer st = new StringTokenizer(strLine, PS_TOKEN);
                    ps.setName(st.nextToken());
                    ps.setIP(st.nextToken());
                    // aqui talvez pegar dados completos da vm pela XenAPI
                    break;
                }
            }
            in.close();
        } catch (Exception ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ps;
    }

     public static void main(String[] args) {
        PhysicalServer ps = new PhysicalServer();
        VMServer vm = new VMServer();
        ps.setName("PS_TEST_NAME");
        ps.setIP("PS_TEST_IP");
        vm.SavePS(ps);
 //       ps = vm.LoadPS("PHY_SERVER_1_NAME");
    }

}
