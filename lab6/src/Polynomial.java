import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polynomial {
    private List<Integer> coefficients;

    public Polynomial(Integer n,Boolean zero) {
        this.coefficients = new ArrayList<>();
        if(zero == false) {
            Random rand = new Random();
            for (int i = 0; i < n; i++) {
                this.coefficients.add(rand.nextInt(10) + 1);
            }
        }
        else
        {
            for (int i = 0; i < n; i++) {
                this.coefficients.add(0);
            }
        }
    }

    public List<Integer> coeff() {
        return coefficients;
    }



}
