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
import org.apache.commons.logging.LogFactory;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainInfo;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

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
    //private String PREFIX = "hxpm";//horizon xen prototype message
    private String PREFIX = "";
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

    public OMElement sanityTest(OMElement in){
        OMElement ret = in.cloneOMElement();
        return ret;
    }

    /**
     * This method creates a virtual machine and instiate it on a physical server
     * of the network. All relevants informations are passed inside the OMElement
     * parameter received by the method.
     *
     * @param element
     * @return an OMElement with the results of the operation
     */
    public OMElement createVirtualMachine(OMElement element){
        element.build();
        element.detach();
        System.err.println("createVM message: "+element.toString());
        System.out.println("createVM message: "+element.toString());
        LogFactory.getLog(getClass());
        

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "SUCESS - Virtual Machine created\nAttributes:\n";
        while(it.hasNext()){
            ele.add((OMElement) it.next());
            returnText = returnText+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
        }

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement method = fac.createOMElement("createVirtualMachineResponse", omNs);
        method.addChild(fac.createOMText(returnText));

        return method;
    }

    /**
     * Migrate a domain (virtual machine) between physical hosts.
     * @param element part of a xml with the parameters for the operation
     * @return an OMElement with the result of the operation
     */

    public OMElement migrateVirtualMachine(OMElement element){
        element.build();
        element.detach();
        System.err.println("MigrateVirtualMachine");
        Logger.getLogger(VMServer.class.getName()).log(Level.ALL, "MigrageVirtualMachine");


        System.out.println("migrateVM message: "+element.toString());

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "SUCESS - Virtual Machine migrated\nAttributes:\n";
        String att = "";
        String sourcePhyServer = "", destPhyServer = "", vmName = "";
        int live = 0;
        while(it.hasNext()){
            ele.add((OMElement) it.next());
            //att = att+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
            if(ele.lastElement().getLocalName().equals("sourcePhyServer")){
                sourcePhyServer = ele.lastElement().getText();
                att = att + "sourcePhyServer: "+sourcePhyServer+"\n";
            }
            if(ele.lastElement().getLocalName().equals("destPhyServer")){
                destPhyServer = ele.lastElement().getText();
                att = att + "destPhyServer: "+destPhyServer+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmName")){
                vmName = ele.lastElement().getText();
                att = att + "vmName: "+vmName+"\n";
            }
            if(ele.lastElement().getLocalName().equals("live")){
                if(ele.lastElement().getText().equals("true")){
                    live = 1;
                    att = att + "live: true\n";
                }
                else
                    att = att + "live: false\n";
            }
        }        
        try {
            Connect sConn = new Connect("xen+ssh://root@"+sourcePhyServer+"/");
            Domain domain = sConn.domainLookupByName(vmName);
            Connect dConn = new Connect("xen+ssh://root@"+destPhyServer+"/");
            //Domain newDomain = domain.migrate(dConn, VIR_MIGRATE_LIVE, null, null, 0);
            Domain newDomain = domain.migrate(dConn, 1, null, null, 0);
            if(newDomain==null)
                returnText = "ERROR - The Domain could not be migrated\nAttributes:\n";
        } catch (LibvirtException ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            returnText = "ERROR - The Domain could not be migrated - Exception: "+ex.getMessage()+"\nAttributes:\n";
        }
        returnText = returnText + att;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement method = fac.createOMElement("migrateVMResponse", omNs);
        method.addChild(fac.createOMText(returnText));
        
        return method;
    }

    /**
     * A service that returns the status of a virtual machine
     * @param element the names of the virtual machine and the physical server
     * @return the status of the virtual machine within a XML element
     */
    public OMElement getVirtualMachineStatus(OMElement element){
        element.build();
        element.detach();
        System.out.println("getVirtualMachineStatus message: "+element.toString());
        Logger.getLogger(VMServer.class.getName()).log(Level.FINE, "DEBUG----DEBUG");
        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        String phyServer = null, vmName = null;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement retElement = fac.createOMElement("getVirtualMachineStatusResponse", omNs);

        while(it.hasNext()){
            ele.add((OMElement) it.next());
            //att = att+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServer = ele.lastElement().getText();
                //att = "phyServer: "+phyServer+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmName")){
                vmName = ele.lastElement().getText();
                //att = "phyServer: "+vmName+"\n";
            }
        }
        try {
            Connect  Conn = new Connect("xen+ssh://root@"+phyServer+"/");
            Domain domain = Conn.domainLookupByName(vmName);
            DomainInfo domainInfo = domain.getInfo();

            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Sucess"));
            retElement.addChild(value);

            value = fac.createOMElement("memory", omNs);
            value.addChild(fac.createOMText(value, (new Long(domainInfo.memory).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("vcpus", omNs);
            value.addChild(fac.createOMText(value, new Integer(domainInfo.nrVirtCpu).toString()));
            retElement.addChild(value);

            value = fac.createOMElement("state", omNs);
            value.addChild(fac.createOMText(value, domainInfo.state.toString()));
            retElement.addChild(value);

            return retElement;
        } catch (LibvirtException ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Error: "+ex.getMessage()));
            retElement.addChild(value);
            return retElement;
        }
    }

    public OMElement getPhysicalServerStatus(OMElement element){
        element.build();
        element.detach();
        System.out.println("getPhysicalServerStatus message: "+element.toString());

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        String phyServer = null;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement retElement = fac.createOMElement("getPhysicalServerStatusResponse", omNs);

        while(it.hasNext()){
            ele.add((OMElement) it.next());
            //att = att+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServer = ele.lastElement().getText();
                //att = "phyServer: "+phyServer+"\n";
            }
        }
        try {
            Connect Conn = new Connect("xen+ssh://root@"+phyServer+"/");
            NodeInfo nodeInfo = Conn.nodeInfo();

            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Sucess"));
            retElement.addChild(value);

            value = fac.createOMElement("cores", omNs);
            value.addChild(fac.createOMText(value, (new Integer(nodeInfo.cores).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("cpus", omNs);
            value.addChild(fac.createOMText(value, new Integer(nodeInfo.cpus).toString()));
            retElement.addChild(value);

            value = fac.createOMElement("memory", omNs);
            value.addChild(fac.createOMText(value, new Long(nodeInfo.memory).toString()));
            retElement.addChild(value);

            value = fac.createOMElement("freeMemory", omNs);
            value.addChild(fac.createOMText(value, new Long(Conn.getFreeMemory()).toString()));
            retElement.addChild(value);

            value = fac.createOMElement("hostName", omNs);
            value.addChild(fac.createOMText(value, Conn.getHostName()));
            retElement.addChild(value);

            int domainCount = Conn.numOfDomains();

            value = fac.createOMElement("domainCount", omNs);
            value.addChild(fac.createOMText(value, new Integer(domainCount).toString()));
            retElement.addChild(value);
            
            int[] domains = Conn.listDomains();

            for(int i=0;i<domainCount;i++){
                value = fac.createOMElement("domain", omNs);
                value.addChild(fac.createOMText(value, Conn.domainLookupByID(domains[i]).getName()));
                retElement.addChild(value);
            }
            Conn.close();

            return retElement;
        } catch (LibvirtException ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Error: "+ex.toString()));
            retElement.addChild(value);
            return retElement;
        }
    }

    public OMElement shutdownPhysicalServer(OMElement element){
        element.build();
        element.detach();
        System.out.println("shutdownPhysicalServer message: "+element.toString());

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        String phyServer = null;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement retElement = fac.createOMElement("shutdownPhysicalServerResponse-ddd", omNs);

        while(it.hasNext()){
            ele.add((OMElement) it.next());
            //att = att+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServer = ele.lastElement().getText();
                //att = "phyServer: "+phyServer+"\n";
            }
        }
        try {
            Connect  Conn = new Connect("xen+ssh://"+phyServer+"/", true);
            //TODO: Servidor não está sendo desligado
   //         NodeInfo nodeInfo = Conn.nodeInfo();

            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Sucess"));
            retElement.addChild(value);


            return retElement;
        } catch (LibvirtException ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Error: "+ex.getMessage()));
            retElement.addChild(value);
            return retElement;
        }
    }

    public OMElement shutdownVirtualMachine(OMElement element){
        element.build();
        element.detach();
        System.out.println("shutdownVirtualMachine message: "+element.toString());

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        String phyServer = null, vmName = null;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement retElement = fac.createOMElement("shutdownVirtualMachineResponse", omNs);

        while(it.hasNext()){
            ele.add((OMElement) it.next());
            //att = att+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServer = ele.lastElement().getText();
                //att = "phyServer: "+phyServer+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmName")){
                vmName = ele.lastElement().getText();
                //att = "phyServer: "+vmName+"\n";
            }
        }
        try {
            Connect  Conn = new Connect("xen+ssh://"+phyServer+"/", true);
            Domain domain = Conn.domainLookupByName(vmName);
            domain.shutdown();
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Sucess"));
            retElement.addChild(value);
            return retElement;
        } catch (LibvirtException ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Error: "+ex.getMessage()));
            retElement.addChild(value);
            return retElement;
        }
    }

    public OMElement destroyVirtualMachine(OMElement element){
        element.build();
        element.detach();
        System.out.println("destroyVirtualMachine message: "+element.toString());
        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        String phyServer = null, vmName = null;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement retElement = fac.createOMElement("destroyVirtualMachineResponse", omNs);

        while(it.hasNext()){
            ele.add((OMElement) it.next());
            //att = att+ele.lastElement().getLocalName()+": "+ele.lastElement().getText()+"\n";
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServer = ele.lastElement().getText();
                //att = "phyServer: "+phyServer+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmName")){
                vmName = ele.lastElement().getText();
                //att = "phyServer: "+vmName+"\n";
            }
        }
        try {
            Connect  Conn = new Connect("xen+ssh://"+phyServer+"/", true);
            Domain domain = Conn.domainLookupByName(vmName);
            domain.destroy();
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Sucess"));
            retElement.addChild(value);
            return retElement;
        } catch (LibvirtException ex) {
            Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            OMElement value = fac.createOMElement("result", omNs);
            value.addChild(fac.createOMText(value, "Error: "+ex.getMessage()));
            retElement.addChild(value);
            return retElement;
        }
    }

    public OMElement createVirtualNetwork(OMElement element){
        element.build();
        element.detach(); 
        System.out.println("createVirtualNetwork message: "+element.toString());
        System.err.println("createVirtualNetwork message: "+element.toString());
        return element;//throw new UnsupportedOperationException("Not yet implemented");
    }

    /*Method to save the vm on the database (text file)*/
//    private void SaveVM(VirtualMachine vm){
  //      throw new UnsupportedOperationException("Not yet implemented");
//    }
//    private VirtualMachine LoadVM(String vmName){
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    private void savePhysicalServer(PhysicalServer ps){
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
    
    private PhysicalServer loadPhysicalServer(String psName){
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

     private static void main(String[] args) {
        PhysicalServer ps = new PhysicalServer();
        VMServer vm = new VMServer();
        ps.setName("PS_TEST_NAME");
        ps.setIP("PS_TEST_IP");
        vm.savePhysicalServer(ps);
 //       ps = vm.LoadPS("PHY_SERVER_1_NAME");
    }
}