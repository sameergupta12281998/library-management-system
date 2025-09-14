package serivces;

import java.util.logging.Logger;

public class TransferService {
   
    private static final Logger log = Logger.getLogger(TransferService.class.getName());
    private final Inventory inventory;

    public TransferService(Inventory inventory) {
        this.inventory = inventory;
    }

    public void transfer(String fromBranchId, String toBranchId, String isbn, int copies){
        inventory.transfer(fromBranchId, toBranchId, isbn, copies);
        log.info(() -> "Transferred " + copies + " copies of " + isbn + " from " + fromBranchId + " to " + toBranchId);
    }
}
