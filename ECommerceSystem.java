// Derek Dao, 501 111 838

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;

/*
 * Models a simple ECommerce system. Keeps track of products for sale, registered customers, product orders and
 * orders that have been shipped to a customer
 */
public class ECommerceSystem
{
	ArrayList<Product>  products = new ArrayList<Product>();
	ArrayList<Customer> customers = new ArrayList<Customer>();	

	ArrayList<ProductOrder> orders   			= new ArrayList<ProductOrder>();
	ArrayList<ProductOrder> shippedOrders = new ArrayList<ProductOrder>();

	// These variables are used to generate order numbers, customer id's, product id's 
	int orderNumber = 500;
	int customerId = 900;
	int productId = 700;

	// General variable used to store an error message when something is invalid (e.g. customer id does not exist)  
	String errMsg = null;

	// Random number generator
	Random random = new Random();

	public ECommerceSystem()
	{
		// NOTE: do not modify or add to these objects!! - the TAs will use for testing
		// If you do the class Shoes bonus, you may add shoe products

		this.products = ReadFile(); // The product arraylist is linked to the readfile() method.

		// Create some customers
		customers.add(new Customer(generateCustomerId(),"Inigo Montoya", "1 SwordMaker Lane, Florin"));
		customers.add(new Customer(generateCustomerId(),"Prince Humperdinck", "The Castle, Florin"));
		customers.add(new Customer(generateCustomerId(),"Andy Dufresne", "Shawshank Prison, Maine"));
		customers.add(new Customer(generateCustomerId(),"Ferris Bueller", "4160 Country Club Drive, Long Beach"));


		
	}

	private ArrayList<Product> ReadFile(){
		File aFile = new File("products.txt"); // opens the file product.txt
		Scanner reader = new Scanner(aFile);			// a scanner that reads each line of product.txt
		int count = 1; 									// an integer used to keep track of the lines position.

		// a while loop that reads every line of the file product.txt
		while(reader.hasNextLine()){
			String Category = "";
			String productName = "";
			String price = "";
			String stock = "";
			String additionalInfo = "";

			if(count == 1){ // The first line is the product category
				Category = reader.nextLine();
			}

			else if(count == 2){ //The second line is the product name
				productName = reader.nextLine();
			}

			else if (count == 3){ // the 3rd line is the price
				price = reader.nextLine();
			}

			else if(count == 4){ //the 4th line contains stock count information
				stock = reader.nextLine();
			}
			
			else if(count == 5){ //  the 5th line contains additional product information
				count = 0;
				additionalInfo = reader.nextLine();
			}

			if(Category.equalsIgnoreCase("BOOK")){
				products.add(new book(productName, generateProductId(), Double.parseDouble(price), Integer.parseInt(stock.substring(0, 1)),Integer.parseInt(stock.substring(1, 2)), additionalInfo.substring(0, additionalInfo.indexOf(":")), additionalInfo.substring(additionalInfo.indexOf(":"))));
			}

			else{
				products.add(new Product(productName,generateProductId(),Double.parseDouble(price), Integer.parseInt(stock), Category));
			}

		}
		reader.close(); //close file once the program finish reading it

		return products;
	}

	private String generateOrderNumber()
	{
		return "" + orderNumber++;
	}

	private String generateCustomerId()
	{
		return "" + customerId++;
	}

	private String generateProductId()
	{
		return "" + productId++;
	}

	public String getErrorMessage()
	{
		return errMsg;
	}

	public void printAllProducts()
	{
		for (Product p : products)
			p.print();
	}

	public void printAllBooks()
	{
		for (Product p : products)
		{
			if (p.getCategory() == Product.Category.BOOKS)
				p.print();
		}
	}

	public ArrayList<Book> booksByAuthor(String author)
	{
		ArrayList<Book> books = new ArrayList<Book>();
		for (Product p : products)
		{
			if (p.getCategory() == Product.Category.BOOKS)
			{
				Book book = (Book) p;
				if (book.getAuthor().equals(author))
					books.add(book);
			}
		}
		return books;
	}

	public void printAllOrders()
	{
		for (ProductOrder o : orders)
			o.print();
	}

	public void printAllShippedOrders()
	{
		for (ProductOrder o : shippedOrders)
			o.print();
	}

	public void printCustomers()
	{
		for (Customer c : customers)
			c.print();
	}

	public class exceptions extends RuntimeException{

	}

	/*
	 * Given a customer id, print all the current orders and shipped orders for them (if any)
	 */
	public void printOrderHistory(String customerId)
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer does not exists");
		}	
		System.out.println("Current Orders of Customer " + customerId);
		for (ProductOrder order: orders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		System.out.println("\nShipped Orders of Customer " + customerId);
		for (ProductOrder order: shippedOrders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
	}

	public String orderProduct(String productId, String customerId, String productOptions)
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer does not exists");
		}
		Customer customer = customers.get(index);

		// Get product 
		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product does not exists");
		}
		Product product = products.get(index);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			throw new InvalidProductOptionsException("Product option is invalid");
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			throw new ProductOutOfStockException("Product is out of stock");
		}
		// Create a ProductOrder
		ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// Add to orders and return
		orders.add(order);

		return order.getOrderNumber();
	}

	/*
	 * Create a new Customer object and add it to the list of customers
	 */

	public void createCustomer(String name, String address)
	{
		// Check to ensure name is valid
		if (name == null || name.equals(""))
		{
			throw new InvalidCustomerNameException("Customer name is invalid");
		}
		// Check to ensure address is valid
		if (address == null || address.equals(""))
		{
			throw new InvalidCustomerAddressException("Customer address is invalid");
		}
		Customer customer = new Customer(generateCustomerId(), name, address);
		customers.add(customer);
	}

	public ProductOrder shipOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Order number does not exist");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		shippedOrders.add(order);
		return order;
	}

	/*
	 * Cancel a specific order based on order number
	 */
	public void cancelOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Order number does not exist");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		
	}

	// Sort products by increasing price
	public void sortByPrice()
	{
		Collections.sort(products, new PriceComparator());
	}

	private class PriceComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			if (a.getPrice() > b.getPrice()) return 1;
			if (a.getPrice() < b.getPrice()) return -1;	
			return 0;
		}
	}

	// Sort products alphabetically by product name
	public void sortByName()
	{
		Collections.sort(products, new NameComparator());
	}

	private class NameComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			return a.getName().compareTo(b.getName());
		}
	}

	// Sort products alphabetically by product name
	public void sortCustomersByName()
	{
		Collections.sort(customers);
	}

	// Adds an item to the customer's cart
	public void addToCart(String productId, String customerId, String productOptions){
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer does not exist");
		}
		Customer customer = customers.get(index);

		// Get product 
		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product does not exist");
		}
		Product product = products.get(index);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			throw new InvalidProductOptionsException("Product option is invalid");
		}

		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			throw new ProductOutOfStockException("Product is out of stock");
		}

		// Generaqte the cart item
		CartItem item = new CartItem(product, productOptions);

		// Put the cart item into the user's cart
		customer.getCart().addItem(item);

	}

	// Removes an item from a customer's cart
	public void remCartItem(String productId, String customerId){
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer does not exist");
		}
		Customer customer = customers.get(index);

		// Get product 
		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product does not exist");
		}
		Product product = products.get(index);

		// Removes the cart item from the user's cart
		customer.getCart().remove(product);
	}

	// Print out all items inside the customer's cart
	public void printCart(String customerId){
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer does not exist");
		}
		Customer customer = customers.get(index);

		System.out.println("Customer " + customerId + "'s cart:");

		customer.getCart().print();
	}

	// Produce a product order for all items in the customer's cart
	public void orderItems(String customerId){
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer does not exist");
		}
		Customer customer = customers.get(index);

	
	}
}

class UnknownCustomerException extends RuntimeException{
	public UnknownCustomerException(){}
	public UnknownCustomerException(String message){
		super(message);
	}
}

class UnknownProductException extends RuntimeException{
	public UnknownProductException(){}
	public UnknownProductException(String message){
		super(message);
	}
}

class InvalidProductOptionsException extends RuntimeException{
	public InvalidProductOptionsException(){}
	public InvalidProductOptionsException(String message){
		super(message);
	}
}

class ProductOutOfStockException extends RuntimeException{
	public ProductOutOfStockException(){}
	public ProductOutOfStockException(String message){
		super(message);
	}
}

class InvalidCustomerNameException extends RuntimeException{
	public InvalidCustomerNameException(){}
	public InvalidCustomerNameException(String message){
		super(message);
	}
}

class InvalidCustomerAddressException extends RuntimeException{
	public InvalidCustomerAddressException(){}
	public InvalidCustomerAddressException(String message){
		super(message);
	}
}

class InvalidOrderNumberException extends RuntimeException{
	public InvalidOrderNumberException(){}
	public InvalidOrderNumberException(String message){
		super(message);
	}
}



