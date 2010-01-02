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
    public Vector<VirtualMachine> getVMList(){
        return vmList;
    }

    public VirtualMachine getVM(String vmName){
        int i = 0;
        for(i=0;i<vmList.size();i++){
            if((vmList.get(i).getName()).equals(vmName)){
                return vmList.get(i);
            }
        }
        return null;
    }

    public void addVM(VirtualMachine vm){
        vmList.add(vm);
    }
}
