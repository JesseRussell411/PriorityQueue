

import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Min-heap priority queue. output ordered from least to greatest. **Currently not thread-safe**.
 * @author jesse russell
 */
public class PriorityQueue<T extends Comparable<T>> implements Iterable<T>{
    // o=========o
    // | fields: |
    // o=========o
    
    ArrayList<T> data = new ArrayList<T>();
    private BiFunction<T, T, Integer> compare;
    private int size = 0;
    
    // o===============o
    // | Constructors: |
    // o===============o
    public PriorityQueue(boolean reversed){
        compare = reversed ? 
                revCompare :
                stdCompare;
    }
    public PriorityQueue(){
        this(false);
    }
    public PriorityQueue(BiFunction<T, T, Integer> comparisonFunction){
        compare = comparisonFunction;
    }
    
    // o============o
    // | Iteration: |
    // o============o
    public class PQIterator implements Iterator<T>{
        PriorityQueue<T> pq;
        int i = -1;
        PQIterator(PriorityQueue<T> pq){
            this.pq = pq;
        }
        public boolean hasNext() { return pq.size() > i + 1; }
        public T next() { return hasNext() ? pq.data.get(++i) : null; }
    }
    
    /**
     * Returns an iterator over the heap, **but not in proper order**.
     */
    @Override
    public Iterator<T> iterator() { return new PQIterator(this); }
    
    
    // o======================o
    // | Comparison function: |
    // o======================o
    private final BiFunction<T, T, Integer> stdCompare = (ta, tb) -> {
        if (ta == null){
            if (tb == null){
                return 0;
            }
            else{
                return -tb.compareTo(ta);
            }
        }
        else
            return ta.compareTo(tb);
    };
    private final BiFunction<T, T, Integer> revCompare = (ta, tb) -> -stdCompare.apply(ta, tb);
    
    
    //public facing part:
    
    /**
     * Get the function being used to compare the values.
     */
    public BiFunction<T, T, Integer> getCompareFunction() {return compare;}
    /**
     * Set the function being used to compare the values. Complexity: O(n*log(n)). (triggers a rebuild)
     */
    public void                      setCompareFunction(BiFunction<T, T, Integer> func){
        if (compare == func) return; 
        else { compare = func; reBuild(); }
    }
    
    /**
    * Set the comparison to standard (least to greatest). Complexity: O(n*log(n)). (triggers a rebuild)
    */
    public void setCompareToStandard() {
        if (compare == stdCompare) return;
        else { compare = stdCompare; reBuild(); }
    }
    /**
    * Set the comparison to standard reversed (greatest to least). Complexity: O(n*log(n)). (triggers a rebuild)
    */
    public void setCompareToReversedStandard() {
        if (compare == revCompare) return;
        else { compare = revCompare; reBuild(); }
    }
    
    
    
    
        
    // o=======o
    // | data: |
    // o=======o
    /**
     * Ensures that data's size is equal to or greater than the queue's size.
     * Complexity: O(1) - (average: increasing data.size() by one).
     * Complexity: O(n) - (worst case)
     * worst case scenarios:
     *      1: n = size, data.size() = 0                                       *Should never happen with current implementation.
     *      2: size > (data's max capacity) and data needs to be re-allocated  *Probably unlikely? unless data gets REALLY big.
     */
    private void ensureCapacity(){
        while (data.size() < size)
            data.add(null);
    }
    
    
    private int linSearch(T value, int start){
        for(int i = start; i < size; ++i)
            if (compare.apply(value, data.get(i)) == 0) return i;
        return -1;
    }
    private int linSearch(T value) { return linSearch(value, 0); }
    
    // Usually the same or worse than linear search, but it is pretty fast if the value is small and doesn't exist in the queue.
    private int recSearch(T value, int start){
        int cA, cB;
        while(true){
            int cmp = compare.apply(value, data.get(start));

            if (cmp < 0) return -1;
            if (cmp == 0) return start;

            cA = ca(start);
            if (cA >= size) return -1; //no left child means no right child
            cB = cb(start);
            
            if (cB >= size) // no right child, just check left.
                start = cA;
            else if (compare.apply(data.get(cA), data.get(cB)) < 0){ // pick the larger branch for recursion because it might result in less depth
                int searchB = recSearch(value, cB);
                if (searchB != -1) return searchB;

                start = cA; 
            }
            else{
                int searchA = recSearch(value, cA);
                if (searchA != -1) return searchA;

                start = cB;
            }
        }   
    }
    private int recSearch(T value) { return recSearch(value, 0); }
    
    
    /**
     * Searches the queue for a value.
     * @return The index of the value in data or -1 if the value was not found.
     */
    private int search(T value){
            return linSearch(value);
    }
    
    /**
     * Complexity: O(log(n)).
     */
    private void removeAt(int i){
        swap(i, end());
        size--;
        bubbleUp(i);
        bubbleDown(i);
    }
    
    
    // o===========o
    // | indexing: |
    // o===========o
    
    /**
     * Get childA(left) index.
     * Complexity: O(1).
     */
    private static int ca(int i) { return i * 2 + 1; }
    /**
     * Get childB(right) index.
     * Complexity: O(1).
     */
    private static int cb(int i) { return i * 2 + 2; }
    /**
     * Get parent index (root's parent is itself, ie: p(0) == 0).
     * Complexity: O(1).
     */
    private static int p(int i) { return (i - 1) / 2; } // WARNING: '/' must refer to truncating division not floor division, incase you are trying to port this to python, don't use (i - 1) // 2, use this: int((i - 1) / 2.0). using floor division will break bubbleUp because root's parent will be -1 instead of 0.
    /**
     * Get last index.
     * Complexity: O(1).
     */
    private int end() { return size - 1; }
    //
    
    /**
     * Swap the values at indexes a and b.
     * Complexity: O(1).
     */
    private void swap(int a, int b){
        T temp = data.get(a);
        data.set(a, data.get(b));
        data.set(b, temp);
    }
    
    
    
    
    // o===============o
    // | verification: |
    // o===============o
    /**
     * Complexity: O(log(n)).
     * @return the final index of the value at i;
     */
    private int bubbleUp(int i){
        int P;
        // run until i's parent is i, which mean that i is root.
        while ((P = p(i)) != i && compare.apply(data.get(i), data.get(P)) < 0){
            swap(i, P);
            i = P;
        }
        return i;
    }
    
    /**
     * Complexity: O(log(n)).
     * @return the final index of the value at i.
     */
    private int bubbleDown(int i){
        int smallestChild;
        
        // This loop with run until i is iether at a leaf or smaller than both its children.
        while(true){
            int cA = ca(i);// no joke, this improves the performance by about 30%
            int cB = cb(i);
            // find smallest child:
            if (cB >= size){
                if (cA >= size)
/*break*/           break; // both children are null, we have reached a leaf; i is as low as it can be; we are done
                else
                    smallestChild = cA; // i only has one child. One child is null, check if i needs to be swapped with the other child.
            }
            else if (compare.apply(data.get(cB), data.get(cA)) < 0)
                smallestChild = cB;
            else
                smallestChild = cA;

            // sort i and the smallest child:
            if (compare.apply(data.get(i), data.get(smallestChild)) > 0){
                swap(i, smallestChild);
                i = smallestChild;
            }
            else
/*break*/       break; // i is smaller than both it's children, i is as low as it needs to go, we are done
        }
        return i;
    }    
    
    /**
     * Places every value at the appropriate position.
     * Complexity: O(n*log(n)).
     */
    private void reBuild(){
        // Passes test against 1000000 integers:
        for(int i = end(); i >= 0; i--){
            bubbleUp(i);
            bubbleDown(i);
        }
//        ArrayList<T> oldData = data;
//        clear();
//        for(T value : oldData)
//            push(value);
    }
    
    
    
    // o=============================o
    // | Interacting with the queue: |
    // o=============================o
    
    /**
     * Get number of values in the queue.
     * Complexity: O(1).
     */
    public int size() { return size; }
    
    /**
     * Returns whether the queue is empty or not.
     * @return true if empty; false if not.
     */
    public boolean empty() { return size <= 0; }
    
    /**
     * Returns whether the queue is not empty
     * @return true if not empty; false otherwise.
     */
    public boolean notEmpty() { return size > 0; }
    
    /**
     * Add a value to the queue.
     * Complexity: O(1) - (average).
     * Complexity: O(n) - (worst case)
     * worst case scenario:
     *      the heap grows past its max capacity and needs to be re-allocated. Won't happen often if at all and probably not more than once in a row.
     */
    public void add(T value){
        ++size;             // increase size variable of heap by one
        ensureCapacity();     // make sure data is big enough for the new size
        data.set(end(), value); // add value to the end of the heap (lower right corner)
        bubbleUp(end());//O(log(n)// move the newly added value to the right place on the heap
    }
    
    /**
     * Get the next priority value without removing it.
     * Complexity: O(1).
     * @return The next value or null of the queue is empty.
     */
    public T peek() {return size > 0 ? data.get(0) : null;}
    
    /**
     * Get the next priority value and remove it from the queue.
     * Complexity: O(log(n)).
     */
    public T poll(){
        if (size <= 0) return null;
        
        T result = data.get(0);
        swap(0, end());
        size--;
        bubbleDown(0); //O(log(n))
        return result;
    }
    
    /**
     * Delete all values from the queue.
     * Complexity: O(1).
     */
    public void clear(){
        data.clear();
        size = 0;
    }
    
    /**
     * Returns whether the given value is in the queue.
     * Complexity O(n).
     */
    public boolean contains(T value){
        return search(value) != -1;
    }
    
    /**
    * Remove a value from the queue.
    * Complexity: O(n).
    * @return True if the value was removed. False if the value wasn't found.
    */
    public boolean remove(T value){
        int i;
        if ((i = search(value)) != -1){
            removeAt(i);
            return true;
        }
        else
            return false;
    }
}
