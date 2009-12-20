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
    String hardDiskLocation;
    OperatingSystem operatingSystem;

    // Methods
    public boolean start(){
          throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public OMElement status(){
        OMElement element = super.status();

        OMFactory fac = OMAbstractFactory.getOMFactory();
        // Set the namespace of the messages
        OMNamespace omNs = fac.createOMNamespace("http://vmserver/xsd", "vsm");

        OMElement value = fac.createOMElement("hardDiskLocation", omNs);
        value.addChild(fac.createOMText(value, hardDiskLocation));
        element.addChild(value);

        value = fac.createOMElement("operatingSystem", omNs);
        value.addChild(fac.createOMText(value, operatingSystem.toString()));
        element.addChild(value);

        return element;
    }

    public void setHardDiskLocation(String hardDiskLocation){
        this.hardDiskLocation = hardDiskLocation;
    }
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
