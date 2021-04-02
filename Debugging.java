/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jesse
 */
import java.util.HashMap;
import java.util.Random;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
public class Debugging{
    public static void main(String[] args){
        PriorityQueue<Integer> q = new PriorityQueue<>();
        Random rand = new Random();
        
        for(int i = 0; i < 100; ++i)
            q.add(rand.nextInt());
        
        System.out.println("Accending:\n--------------------");
        while(q.notEmpty())
            System.out.println(q.poll());
        
        q.setCompareToReversedStandard();
        
        for(int i = 0; i < 100; ++i)
            q.add(rand.nextInt());
        System.out.println();
        
        System.out.println("Decending:\n--------------------");
        while(q.notEmpty())
            System.out.println(q.poll());
        System.out.println();
    }
}
