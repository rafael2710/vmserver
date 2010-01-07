package vmserver;

import java.util.Vector;

/**
 *
 * @author Rafael
 */
public class PhysicalServer extends Computer{
    // Fields
    private Vector<VirtualMachine> vmList;

    PhysicalServer(){
        vmList = new Vector<VirtualMachine>();
    }

    // Methods
    public Vector<VirtualMachine> getVirtualMachineList(){
        return vmList;
    }

    public VirtualMachine getVirtualMachine(String vmName){
        int i = 0;
        for(i=0;i<vmList.size();i++){
            if((vmList.get(i).getName()).equals(vmName)){
                return vmList.get(i);
            }
        }
        return null;
    }

    public void addVirtualMachine(VirtualMachine vm){
        vmList.add(vm);
    }
}
