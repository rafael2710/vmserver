package vmserver;

import java.util.Vector;

/**
 *
 * @author Rafael
 */
public class PhysicalServer extends Computer{
    // Fields
   Vector<VirtualMachine> vmList;

    PhysicalServer(){
        vmList = new Vector<VirtualMachine>();
    }

    // Methods
    public Vector<VirtualMachine> getVMList(){
          return vmList;
    }
    
}
