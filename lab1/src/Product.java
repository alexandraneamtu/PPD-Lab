/**
 * Created by alexandraneamtu on 15/10/2017.
 */

public class Product{
    int unitprice;
    int quantity;
    String name;

    public Product(String name,int unitprice, int quantity) {
        this.unitprice = unitprice;
        this.quantity = quantity;
        this.name = name;
    }

    public int getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(int unitprice) {
        this.unitprice = unitprice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFullPrice(){
        return this.getQuantity()*this.getUnitprice();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", unitprice=" + unitprice +
                ", quantity=" + quantity +
                '}';
    }
}
