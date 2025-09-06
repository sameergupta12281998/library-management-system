package serivces;

import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private final Map<String, Map<String,Integer>> totalCounts  = new HashMap<>();
    private final Map<String, Map<String,Integer>> availableCounts  = new HashMap<>();

    int getAvailabeCount(String branchId, String isbn) {
        return availableCounts.getOrDefault(branchId, Map.of()).getOrDefault(isbn, 0);
    }
    int getTotalCount(String branchId, String isbn) {
        return totalCounts.getOrDefault(branchId, Map.of()).getOrDefault(isbn, 0);
    }
    void addCopies(String branchId, String isbn, int copies) {
        totalCounts.computeIfAbsent(branchId, k -> new HashMap<>());
        availableCounts.computeIfAbsent(branchId, k -> new HashMap<>());
        totalCounts.get(branchId).merge(isbn, copies, Integer::sum);
        availableCounts.get(branchId).merge(isbn, copies, Integer::sum);
    }

    void removeCopies(String branchId, String isbn, int copies) {
        totalCounts.computeIfAbsent(branchId, k -> new HashMap<>());
        availableCounts.computeIfAbsent(branchId, k -> new HashMap<>());
        
        int total = Math.max(0,getTotalCount(branchId, isbn) - copies);
        int avail = Math.max(0,getAvailabeCount(branchId, isbn) - Math.min(getAvailabeCount(branchId, isbn), copies));
       
        totalCounts.get(branchId).put(isbn, total);
        availableCounts.get(branchId).put(isbn, avail);
    }

    boolean checkout(String branchId, String isbn) {
        int avail = getAvailabeCount(branchId, isbn);
        if (avail > 0) {
            availableCounts.get(branchId).put(isbn, avail - 1);
            return true;
        }
        return false;
    }

    void checkin(String branchId, String isbn) {
        int avail = getAvailabeCount(branchId, isbn);
        availableCounts.computeIfAbsent(isbn, k -> new HashMap<>());
        availableCounts.get(branchId).put(isbn, avail + 1);
    }

    void transfer(String fromBranchId,String toBranchId, String isbn, int copies) {
        int availFrom = getAvailabeCount(fromBranchId, isbn);
        int totalFrom = getTotalCount(fromBranchId, isbn);

        if(copies <= 0 || availFrom < copies || totalFrom < copies) {
            throw new IllegalArgumentException("Not enough copies to transfer");
        }

        //deduct 
        availableCounts.get(fromBranchId).put(isbn, availFrom - copies);
        totalCounts.get(fromBranchId).put(isbn, totalFrom - copies);
        //add 
        addCopies(toBranchId, isbn, copies);
        
    }

    Map<String, Map<String, Integer>> snapshotAvailable(){ return availableCounts; }
    Map<String, Map<String, Integer>> snapshotTotal(){ return totalCounts; }

    
}
