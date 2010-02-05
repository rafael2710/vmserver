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

    /**
     * A simple sanity test for the server of virutal machines
     * 
     * @param in a xml element
     * @return copy of the only input
     */
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
        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "SUCESS - Virtual Machine created\nAttributes:\n";
        String att = "";
        String phyServer="", vmName="", vmRAM ="", vmIP="";
        while(it.hasNext()){
            ele.add((OMElement) it.next());
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServer = ele.lastElement().getText();
                att = att + "phyServer: "+phyServer+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmName")){
                vmName = ele.lastElement().getText();
                att = att + "vmName: "+vmName+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmIP")){
                vmIP = ele.lastElement().getText();
                att = att + "vmIP: "+vmIP+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmRAM")){
                vmRAM = ele.lastElement().getText();
                att = att + "vmRAM: "+vmRAM+"\n";
            }
            //TODO: pegar outros parametros da maquina virtual
        }
        try {
            if(!createVirtualMachine(phyServer, vmName, vmRAM, vmIP)){
                returnText = "ERROR - The Domain could not be created - \nAttributes:\n";
            }
        } catch (LibvirtException ex) {
            returnText = "ERROR - The Domain could not be created - Exception: "+ex.getMessage()+"\nAttributes:\n";
        }
        returnText = returnText + att;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement method = fac.createOMElement("createVirtualMachineResponse", omNs);
        method.addChild(fac.createOMText(returnText));
        return method;
    }

    /**
     * 
     * @param phyServer
     * @param vmName
     * @param vmRAM
     * @param vmIP
     * @return
     * @throws LibvirtException
     */
    private boolean createVirtualMachine(String phyServer, String vmName, String vmRAM, String vmIP) throws LibvirtException{
        Connect Conn = new Connect("xen+ssh://root@" + phyServer + "/");
        Domain newdomain = Conn.domainCreateXML(
            "<domain type='xen'>" +
                "<name>"+vmName+"</name>" +
                "<os>" +
                    "<type>linux</type>" +
                    "<kernel>/boot/vmlinuz-2.6.26-2-xen-686</kernel>" +
                    "<initrd>/boot/initrd.img-2.6.26-2-xen-686</initrd>" +
                    "<cmdline>root=/dev/xvda1 ro</cmdline>" +
//                      "<cmdline>root=/dev/xvda1 ro console=hvc0</cmdline>" +
                "</os>" +
                "<memory>"+vmRAM+"</memory>" +
                "<vcpu>1</vcpu>" +
                "<on_poweroff>destroy</on_poweroff>" +
                "<on_reboot>restart</on_reboot>" +
                "<on_crash>restart</on_crash>" +
                "<devices>" +
                    "<disk type='file' device='disk'>" +
                        "<source file='/home/xen/domains/default/disk.img' />" +
                        "<target dev='xvda1'/>" +
                        "<shareable/>" +
                    "</disk>"+
                    "<disk type='file' device='disk'>" +
                        "<source file='/home/xen/domains/default/swap.img'/>" +
                        "<target dev='xvda2'/>" +
                        "<shareable/>" +
                    "</disk>"+
                    "<interface type='bridge'>" +
                        "<source bridge='xenbr0'/>" +
                        "<ip address='"+ vmIP +"' netmask='255.255.255.0' />" +
                        "<mac address='aa:00:00:00:00:11'/>" + // TODO: alterar automaticamente endereco MAC
                        "<script path='/etc/xen/scripts/vif-bridge'/>" +
                    "</interface>" +
//                   "<console tty='/dev/hvc0'/>"+
 //                   "<console type='pty'>" +
   //                     "<target port='0'/>" +
     //               "</console>" +
       //             "<input type='mouse' bus='xen'/>" +
  //                  "<graphics type='vnc' port='-1' autoport='yes' listen='0.0.0.0'/>" +
                "</devices>" +
            "</domain>", 0);
        if(newdomain==null){
            return false;
        }
        return true;
    }

    /**
     * Migrate a domain (virtual machine) between physical hosts.
     * @param element part of a xml with the parameters for the operation
     * @return an OMElement with the result of the operation
     */
    public OMElement migrateVirtualMachine(OMElement element){
        element.build();
        element.detach();
        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "SUCESS - Virtual Machine migrated\nAttributes:\n";
        String att = "";
        String sourcePhyServer = "", destPhyServer = "", vmName = "";
        int live = 0;
        while(it.hasNext()){
            ele.add((OMElement) it.next());
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
            if(!migrateVirtualMachine(sourcePhyServer, destPhyServer, vmName, live))
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

    private boolean migrateVirtualMachine(String sourcePhyServer, String destPhyServer, String vmName, int live) throws LibvirtException{
        Connect sConn = new Connect("xen+ssh://root@"+sourcePhyServer+"/");
        Domain domain = sConn.domainLookupByName(vmName);
        Connect dConn = new Connect("xen+ssh://root@"+destPhyServer+"/");
        //Domain newDomain = domain.migrate(dConn, VIR_MIGRATE_LIVE, null, null, 0);
        Domain newDomain = domain.migrate(dConn, 1, null, null, 0);
        if(newDomain==null)
            return false;
        return true;
    }

    /**
     * A service that returns the status of a virtual machine
     * @param element the names of the virtual machine and the physical server
     * @return the status of the virtual machine within a XML element
     */
    public OMElement getVirtualMachineStatus(OMElement element){
        element.build();
        element.detach();
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

            value = fac.createOMElement("getName", omNs);
            value.addChild(fac.createOMText(value, domain.getName()));
            retElement.addChild(value);

            value = fac.createOMElement("memory", omNs);
            value.addChild(fac.createOMText(value, (new Long(domainInfo.memory).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("maxMem", omNs);
            value.addChild(fac.createOMText(value, (new Long(domainInfo.maxMem).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("getMaxMemory", omNs);
            value.addChild(fac.createOMText(value, (new Long(domain.getMaxMemory()).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("vcpus", omNs);
            value.addChild(fac.createOMText(value, new Integer(domainInfo.nrVirtCpu).toString()));
            retElement.addChild(value);

            value = fac.createOMElement("getMaxVcpus", omNs);
            value.addChild(fac.createOMText(value, (new Long(domain.getMaxVcpus()).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("cpuTime", omNs);
            value.addChild(fac.createOMText(value, (new Long(domainInfo.cpuTime).toString())));
            retElement.addChild(value);

            value = fac.createOMElement("state", omNs);
            value.addChild(fac.createOMText(value, domainInfo.state.toString()));
            retElement.addChild(value);

     //       value = fac.createOMElement("getSchedulerParameters", omNs);
   //         value.addChild(fac.createOMText(value, domain.getSchedulerParameters().toString()));
 //           retElement.addChild(value);

//            value = fac.createOMElement("getSchedulerType", omNs);
//            value.addChild(fac.createOMText(value, domain.getSchedulerType().toString()));
//            retElement.addChild(value);

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

        //<vsm:createVNet xmlns:vsm="http://vmserver/xsd"><vsm:nodeCount>3</vsm:nodeCount>
        //<vsm:phyServer>phyS_1</vsm:phyServer><vsm:phyServer>phyS_2</vsm:phyServer>
        //<vsm:phyServer>phyS_3</vsm:phyServer><vsm:VMName>vm_1</vsm:VMName>
        //<vsm:VMName>vm_2</vsm:VMName><vsm:VMName>vm_3</vsm:VMName></vsm:createVNet>

        Iterator it = element.getChildElements();
        Vector <OMElement> ele = new Vector();
        ele.clear();
        String returnText = "SUCESS - Virtual Network created\nAttributes:\n";
        String att = "";
        //String phyServer="", vmName="", vmRAM ="", vmIP="";
        int nodeCount=0;
        Vector<String> phyServerList = new Vector();
        Vector<String> vmNameList = new Vector();
        Vector<String> IPList = new Vector();
        Vector<String> RAMszList = new Vector();
        while(it.hasNext()){
            ele.add((OMElement) it.next());
            if(ele.lastElement().getLocalName().equals("nodeCount")){
                nodeCount = new Integer(ele.lastElement().getText());
                att = att + "nodeCount: "+nodeCount+"\n";
            }
            if(ele.lastElement().getLocalName().equals("phyServer")){
                phyServerList.add(ele.lastElement().getText());
                att = att + "phyServerList: "+phyServerList.lastElement()+"\n";
            }
            if(ele.lastElement().getLocalName().equals("VMName")){
                vmNameList.add(ele.lastElement().getText());
                att = att + "vmNameList: "+vmNameList.lastElement()+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmIP")){
                IPList.add(ele.lastElement().getText());
                att = att + "IPList: "+IPList.lastElement()+"\n";
            }
            if(ele.lastElement().getLocalName().equals("vmRAM")){
                RAMszList.add(ele.lastElement().getText());
                att = att + "vRAMszList: "+RAMszList.lastElement()+"\n";
            }
        }
        int i;
        for(i=0;i<nodeCount;i++){
            try {
                if (!createVirtualMachine(phyServerList.elementAt(i), vmNameList.elementAt(i), RAMszList.elementAt(i), IPList.elementAt(i))) { //TODO: autogenerate IPs
                    returnText = "ERROR - Virtual Network could not be created\nAttributes:\n";
                }
            } catch (LibvirtException ex) {
                returnText = "ERROR - Virtual Network could not be created - Exception: "+ex.getMessage()+"\nAttributes:\n";
                Logger.getLogger(VMServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //virtual machines created
        //TODO: Configurar rede virtual

        returnText = returnText + att;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);
        OMElement method = fac.createOMElement("createVirtualNetworkResponse", omNs);
        method.addChild(fac.createOMText(returnText));
        return method;
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