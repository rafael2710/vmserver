package vmserver;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 *
 * @author Rafael
 */
enum OperatingSystem {Linux,Windows,MacOS};

public class VirtualMachine extends Computer{
    // Fields
    private String hardDiskLocation;
    private OperatingSystem operatingSystem;
    private String URI = "http://vmserver/xsd";
    private String PREFIX = "vsm";

    // Methods
    public boolean start(){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public OMElement status(){
        OMElement element = super.status();

        OMFactory fac = OMAbstractFactory.getOMFactory();
        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace(URI, PREFIX);

        OMElement value = fac.createOMElement("hardDiskLocation", omNs);
        value.addChild(fac.createOMText(value, hardDiskLocation));
        element.addChild(value);

        value = fac.createOMElement("operatingSystem", omNs);
        value.addChild(fac.createOMText(value, operatingSystem.toString()));
        element.addChild(value);

        return element;
    }
    /**
     * Sets the location the virtual hard disk file.
     * Location can be local or an URI.
     *
     * @param hardDiskLocation the virtual hard disk file URI
     */
    public void setHardDiskLocation(String hardDiskLocation){
        this.hardDiskLocation = hardDiskLocation;
    }
    /**
     * Sets the location the virtual hard disk file.
     * Location can be local or an URI.
     *
     * @return the virtual hard disk file URI
     */
    public String getHardDiskLocation(){
        return hardDiskLocation;
    }
    public void setOperatingSystem(String operatingSystem){
        this.operatingSystem = OperatingSystem.valueOf(operatingSystem);
    }
    public String getOperatingSystem(){
        return operatingSystem.toString();
    }


    public static void main(String[] args){
        VirtualMachine vm = new VirtualMachine();
        vm.operatingSystem = OperatingSystem.Linux;
        System.out.println(vm.operatingSystem.toString());
        System.out.println(vm.status().toString());
        vm.setOperatingSystem("Windows");
         System.out.println(vm.getOperatingSystem());


    }

}
