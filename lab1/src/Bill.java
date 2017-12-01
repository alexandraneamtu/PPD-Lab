import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandraneamtu on 15/10/2017.
 */
public class Bill {
    List<Product> products = new ArrayList<>();

    public Bill() {
    }

    public Bill(List<Product> prod) {
        for(int i=0; i<prod.size(); i++)
            this.products.add(prod.get(i));
    }

    public int totalPrice(){
        int sum=0;
        for(int i=0; i<products.size(); i++)
            sum += products.get(i).getFullPrice();
        return sum;
    }

    public void addProduct(Product prod){
        this.products.add(prod);
    }




    @Override
    public String toString() {
        return "Bill{" +
                "products=" + products +
                ",total price=" + totalPrice()+
                '}';
    }
}
