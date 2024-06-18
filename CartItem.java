//Derek Dao

/*
 * A cart item is a product that is placed inside a user's cart.
 * Every time a customer adds an item to their cart, a cart item is
 * created to be placed inside the person's cart.
 */

public class CartItem {
    private Product product;
    private String productOptions;

    public CartItem(Product product, String productOptions)
    {
        this.product = product;
        this.productOptions = productOptions;
    }

    public Product getProduct(){
        return product;
    }

    public void setProduct(Product product){
        this.product = product;
    }

    public Product getProductOptions(){
        return productOptions;
    }

    public void setProductOptions(string productOptions){
        this.productOptions = productOptions;
    }
}
