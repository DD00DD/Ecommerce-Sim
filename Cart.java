import java.util.LinkedList;
import java.util.ListIterator;

//Derek Dao, 501 111 838

/*
 * A cart is a personal storage that carries the items the user intends to purchase
 * 
 * It will add, remove, print, and order the items that are inside the user's cart.
 */
public class Cart{
    private LinkedList<CartItem> items;

    public Cart(){
        this.items = new LinkedList<CartItem>();
    }

    // Method used to add an item to the user's cart
    public void addItem(CartItem item){
        items.add(item);
    }

    // Method used to remove item from the user's cart
    public void remove(Product product){
        
        // Loops through the elements in items and finds a match to remove the specified cart item.
        for(CartItem i : items){
            if(i.getProduct().equals(product)){
                items.remove(i);
            }
        }
    }

    // A method used to print out all the items inside the user's cart
    public void print(){
        for(CartItem i : items){
           i.getProduct().print();
        }
    }

    public void order(){

    }

}