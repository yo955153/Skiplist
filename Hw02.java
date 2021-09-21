// Yoseph Hassan, Hw02 CS2, Spring 2020


// Java libraries
import java.util.*;
import java.io.*;
import java.lang.*;


public class Hw02
{
    public static void complexityIndicator()
    {
        System.err.println("yo955153;5;25");
    }

    public static void main (String[] args) throws FileNotFoundException
    {
        SkipList SL = new SkipList();
        File file = new File(args[0]);
        Scanner scnr = new Scanner(file);
        Random R = new Random();
        int RNG_seed = 42;
        R.setSeed(RNG_seed);

        complexityIndicator();
        
        if(args.length == 2)
        {
            if((args[1].charAt(0) == 'r') || (args[1].charAt(0) == 'R'))
            {
                Scanner RNGval = new Scanner(System.in);

                System.out.println("Enter a seed");
                RNG_seed = RNGval.nextInt();
                System.out.println("For the input file named " + args[0]);
                System.out.print("With the RNG seeded,\n");
                R.setSeed(RNG_seed);

                RNGval.close();
            }

            else
            {
                System.out.println("For the input file named " + args[0]);
                System.out.print("With the RNG unseeded,\n");
            }
        }
        else
        {
            System.out.println("For the input file named " + args[0]);
            System.out.print("With the RNG unseeded,\n");
        }

        while(scnr.hasNext())
        {
            String function = scnr.next();

            if(function.charAt(0) == 'i')
                SL.insert(scnr.nextInt(), SL, R);
                

            else if(function.charAt(0) == 's')
            {
                int searchval = scnr.nextInt();

                if( SL.search(searchval, SL.head).val == searchval)
                    System.out.println(searchval + " found");

                else
                    System.out.println(searchval + " NOT FOUND");

            }
                

            else if(function.charAt(0) == 'd')
                SL.delete(SL, scnr.nextInt());
            
            else if(function.charAt(0) == 'q')
            {
                scnr.close();
                break;
            }
                
            else if(function.charAt(0) == 'p')
            {
                SL.printAll(SL);
            }
        }

    }


}
// Class which creates default skiplist
class SkipList
{
    Node head, tail;
    int length_list, height_list;

    public SkipList()
    {
        // Default values for Head and Tail Nodes
        // -1 and 5001 are out of the bounds of the restrictions
        // For all intents and purposes these are positive and negative infinity
        this.tail = new Node(5001);
        this.head = new Node(-1);

        // Linking head and tail together doubly
        this.tail.west = this.head;
        this.head.east = this.tail;


        // Setting default height and length of skiplist
        this.length_list = 0;
        this.height_list = 1;
        
    }

    // finished
    // Helper function which creates addition levels for the SkipList
    public void create_level()
    {
        // Next higher level head and tail nodes
        Node nextlevel_h = new Node(-1);
        Node nextlevel_t = new Node(5001);

        // Link new level heads and tails to previous level heads/tails
        nextlevel_h.south = head;
        head.north = nextlevel_h;
        nextlevel_t.south = tail;
        tail.north = nextlevel_t;

        // Link new level heads and tails to eachother
        nextlevel_h.east = nextlevel_t;
        nextlevel_t.west = nextlevel_h;

        // Set the highest level head and tail to new main head and tail of the skiplist
        height_list = height_list + 1;
        head = nextlevel_h;
        tail = nextlevel_t;
    }

    //Finished
    // Search function which finds node with value <= desired node
    public Node search(int searchval, Node iterator)
    {
        
        while(iterator.east.val <= searchval)
            iterator = iterator.east;
        
        if(iterator.east.val > searchval)
        {
            if(iterator.south != null)
                return search(searchval, iterator.south);
        }
        
        //Always returns value greatest Node <= to node which is being searched for
        return iterator;

    }

    // Insertion method
    //finished
    public void insert(int insert_val, SkipList SL, Random R)
    {
        int height_of_nodes;
        Node nn = new Node(insert_val);// New Node which is being added
        Node c_node = search(insert_val, SL.head); // Current Node which is iterating to find correct position

        // Value already found in Skiplist - do nothing
        if(c_node.val == insert_val)
            return;

        else
        {
            // Reassign pointers
            Node tempR = c_node.east;
            c_node.east = nn;
            nn.west = c_node;
            nn.east = tempR;
            tempR.west = nn;

            height_of_nodes =  promote(c_node, R, SL); 
            

            SL.length_list++;
            if(height_of_nodes > SL.height_list)
                SL.height_list = height_of_nodes;
        }
    }

    // finished
    public void delete(SkipList SL, int searchval)
    {
        // Search for node with specified value in list
        Node c_node = search(searchval, SL.head);

        if(c_node.val != searchval)
            System.out.println(searchval + " integer not found - delete not successful");

        else
        {
            while(c_node != null)
            {
                Node temp = c_node.east;
                c_node.east = null;
                c_node.west.east = temp;
                temp.west = c_node.west;
                c_node.west = null;
                c_node = c_node.north;
            }
            SL.length_list = SL.length_list - 1;
            System.out.println(searchval + " deleted");
        }
        
    }

    //finished
    public void printAll(SkipList SL)
    {
        Node curr_node = SL.head;

        while(curr_node.south != null)
            curr_node = curr_node.south;
        
        System.out.println("the current Skip List is shown below: \n---infinity");

        while(curr_node.val != 5001)
        {
            curr_node = curr_node.east;

            if(curr_node.val != 5001)
            {
                Node tempNode = curr_node;
                System.out.print(" " + tempNode.val + "; ");

                if(tempNode.north == null)
                    System.out.print("\n");
                
                while( tempNode.north != null)
                {
                    tempNode = tempNode.north;
                    System.out.print(" " + tempNode.val + "; ");
                    if(tempNode.north == null)
                    {
                        System.out.print("\n");
                        break;
                    }
                }

            }
        }
        System.out.println("+++infinity\n---End of Skip List---");
    }

    //finished
    public int promote(Node c_node, Random R, SkipList SL)
    {
        int height_of_nodes = 1;
        Node nn = c_node.east;

        while((R.nextInt() % 2) == 1)
        {
            if(height_of_nodes >= SL.height_list)
                SL.create_level();

            while(c_node.north == null)
                c_node = c_node.west;
            
            c_node = c_node.north;

     
            Node promoted = new Node(nn.val);
            promoted.west = c_node;
            promoted.east = c_node.east;
            c_node.east.west = promoted;
            c_node.east = promoted;
            promoted.south = nn;
            nn.north = promoted;

            nn = promoted;

            height_of_nodes++;
        }

        return height_of_nodes;
    }

}


// Node Class which include a value and 
// west, east, north, and south pointers associated with each node
class Node
{
    int val;
    Node west, east, north, south;

    public Node(int val)
    {
        this.val = val;
        this.west = null;
        this.east = null;
        this.south = null;
        this.north = null;
    }

}
