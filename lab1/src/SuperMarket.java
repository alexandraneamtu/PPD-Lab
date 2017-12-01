import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by alexandraneamtu on 15/10/2017.
 */

public class SuperMarket {

    ArrayList<Product> products;
    ArrayList<Bill> bills;
    //ArrayList<Product> soldProducts;
    int money;
    Semaphore semaphore;


    public SuperMarket() {
        this.products = new ArrayList<>();
        this.bills = new ArrayList<>();
        this.money = 0;
        this.semaphore = new Semaphore(1);
        //this.soldProducts = new ArrayList<>();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Bill> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        this.bills = bills;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public int findByName(String name){
        for(int i=0;i<products.size();i++)
            if(products.get(i).getName().equals(name))
                return i;
        return -1;
    }



    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void addBill(Bill bill) {
        this.bills.add(bill);
    }



    public Bill createAndValidateBill(List<Product> prods){
        Bill bill = new Bill();
        int index;
        //System.out.println("----"+prods);
        for(int i=0;i<prods.size();i++) {
            index = findByName(prods.get(i).getName());
            if (index != -1) {
                if (prods.get(i).getQuantity() < products.get(index).getQuantity()) {
                    try
                    {
                        semaphore.acquire();
                        products.get(index).setQuantity(products.get(index).getQuantity() - prods.get(i).getQuantity());
                        //System.out.println("before:" + this.money);
                        //System.out.println(prods.get(i));
                        //System.out.println(prods.get(i).getFullPrice());
                        this.money += prods.get(i).getFullPrice();
                        bill.addProduct(prods.get(i));
                        //Product soldProduct = prods.get(i);
                        //System.out.println("after:" + this.money);


                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bill;
    }

    public List<Product> generateBill() {
        int max = products.size();
        int productNo = ThreadLocalRandom.current().nextInt(1, max);
        int randomProd;
        int randomQuantity;
        List<Product> toBill = new ArrayList<>();
        while (productNo != 0) {
            randomProd = ThreadLocalRandom.current().nextInt(max);
            Product prod = products.get(randomProd);
            randomQuantity = ThreadLocalRandom.current().nextInt(10);
            while (randomQuantity == 0)
                randomQuantity = ThreadLocalRandom.current().nextInt(10);
            productNo--;
            if (prod.getQuantity() > randomQuantity) {
                //semaphore.acquire();
                //prod.setQuantity(prod.getQuantity() - randomQuantity);
                Product soldProduct = new Product(prod.getName(), prod.getUnitprice(), randomQuantity);
                //semaphore.release();
                toBill.add(soldProduct);
            }
        }

        return toBill;
    }


    public void finalBill(){
        List<Product> productsOnBill = generateBill();
        //System.out.println("----------" + toBill);
        while(productsOnBill.size()==0) {
            //System.out.println("#####" + toBill);
            productsOnBill = generateBill();
            //System.out.println("*****" + toBill);
        }

        Bill bill;
        bill = createAndValidateBill(productsOnBill);
        if(bill!=null) {
            bills.add(bill);
        }
    }

    public void generateBills(int billsNo) {
        for (int i = 0; i < billsNo; i++)
            finalBill();
    }

    public int findIfSold(Product prod,List<Product> prods){
        for(int i=0;i<prods.size();i++)
            if(prods.get(i).name.equals(prod.name))
                return i;
        return -1;
    }

    public List<Product> getSoldProducts(){
        int i,j,k,br=0;
        List<Product> sold = new ArrayList<>();
        for(i=0;i<this.bills.size();i++)
        {
            for(j=0; j< this.bills.get(i).products.size(); j++)
            {
                int index2 = findIfSold(this.bills.get(i).products.get(j),sold);
                if (index2 != -1)
                    sold.get(index2).setQuantity(sold.get(index2).getQuantity() + this.bills.get(i).products.get(j).getQuantity());
                else
                    sold.add(this.bills.get(i).products.get(j));
            }
        }
        return sold;
    }


    public Long moneyFromBills(){
        long justifiedMoney = 0;
        try {
            semaphore.acquire();
            for(int i=0;i<bills.size();i++)
                justifiedMoney += bills.get(i).totalPrice();
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return justifiedMoney;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Super Market: \n");
        stringBuilder.append("---->Remained products: \n");
        for (int i = 0; i < products.size(); i++)
            stringBuilder.append(products.get(i)).append("\n");
        stringBuilder.append("---->Bills: \n");
        for (int i = 0; i < bills.size(); i++)
            stringBuilder.append(bills.get(i)).append("\n");
        stringBuilder.append("---->Money in the supermarket: " + money);
        return stringBuilder.toString();
    }



}
