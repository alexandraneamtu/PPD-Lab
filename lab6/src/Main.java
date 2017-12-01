import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static int THREADSNO = 1;
    private static List<ThreadWorker> threads = new ArrayList<>();


    public static void main(String[] args) {
        // The following array represents polynomial 5 + 10x^2 + 6x^3
        Polynomial A = new Polynomial(4,false);
        // The following array represents polynomial 1 + 2x + 4x^2
        Polynomial B = new Polynomial(4,false);
        int m = A.coeff().size();
        int n = B.coeff().size();

        System.out.println("First polynomial is:");
        printPoly(A, m);
        System.out.println("\n\nSecond polynomial is:");
        printPoly(B, n);

        Polynomial prodSeqUsual;
        Polynomial prodNThreadsUsual;
        //List<Integer> prod2 = new ArrayList<>();

        prodSeqUsual = multiply(A, B, m, n);

        System.out.println("\n\nProduct polynomial usual seq is:");
        printPoly(prodSeqUsual, m+n-1);

        prodNThreadsUsual = multiplyNThreads(A,B);
        System.out.println("\n\nProduct polynomial usual N threads is:");
        printPoly(prodNThreadsUsual, m+n-1);

        //prod2 = karatsubaMultiplyRecursive(A,B,m,n);
        //System.out.println("\n\nProduct karatsuba polynomial is:");
        //printPoly(prod2, m+n-1);

    }

    // A utility function to print a polynomial
    private static void printPoly(Polynomial poly, int n)
    {
        for (int i=0; i<n; i++)
        {
            System.out.print(poly.coeff().get(i));
            if (i != 0)
                System.out.print("*x^"+i);
            if (i != n-1)
                System.out.print(" + ");
        }
    }

    //classic multiply sequential
    private static Polynomial multiply(Polynomial A, Polynomial B, int m, int n)
    {
        Polynomial prod = new Polynomial(m+n-1,true);


        // Multiply two polynomials term by term
        // Take ever term of first polynomial
        for (int i=0; i<m; i++)
        {
            // Multiply the current term of first polynomial
            // with every term of second polynomial.
            for (int j=0; j<n; j++)
                prod.coeff().set(i+j,prod.coeff().get(i+j)+A.coeff().get(i)*B.coeff().get(j));
        }
        return prod;
    }

    public static Polynomial multiplyNThreads(Polynomial A, Polynomial B){
        Polynomial result = new Polynomial(A.coeff().size()+B.coeff().size()-1,true);
        for(int j=0; j< B.coeff().size();j++)
            threads.add(new ThreadWorker(A,B,result,j));



        for(Thread thread : threads)
            thread.start();

        for(Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

        return result;

    }


    private static List<Integer> karatsubaMultiplyRecursive(List<Integer> A, List<Integer> B, int m, int n) {

        List<Integer> product = new ArrayList<>();

        //Handle the base case where the polynomial has only one coefficient
        if (m == 1) {
            product.add(0,A.get(0) * B.get(0));
            return product;
        }

        int halfArraySize = m / 2;

        //Declare arrays to hold halved factors
        List<Integer> multiplicandLow = new ArrayList<>();
        List<Integer> multiplicandHigh = new ArrayList<>();
        List<Integer> multipliplierLow = new ArrayList<>();
        List<Integer> multipliierHigh = new ArrayList<>();

        List<Integer> multiplicandLowHigh = new ArrayList<>();
        List<Integer> multipliplierLowHigh = new ArrayList<>();

        //Fill in the low and high arrays
        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; ++halfSizeIndex) {

            multiplicandLow.add(halfSizeIndex,A.get(halfSizeIndex));
            multiplicandHigh.add(halfSizeIndex,A.get(halfSizeIndex + halfArraySize));
            multiplicandLowHigh.add(halfSizeIndex,multiplicandLow.get(halfSizeIndex) + multiplicandHigh.get(halfSizeIndex));

            multipliplierLow.add(halfSizeIndex,B.get(halfSizeIndex));
            multipliierHigh.add(halfSizeIndex,B.get(halfSizeIndex + halfArraySize));
            multipliplierLowHigh.add(halfSizeIndex,multipliplierLow.get(halfSizeIndex) + multipliierHigh.get(halfSizeIndex));

        }

        //Recursively call method on smaller arrays and construct the low and high parts of the product
        List<Integer> productLow = karatsubaMultiplyRecursive(multiplicandLow, multipliplierLow,multiplicandLow.size(),multipliplierLow.size());
        List<Integer> productHigh = karatsubaMultiplyRecursive(multiplicandHigh, multipliierHigh,multiplicandHigh.size(),multipliierHigh.size());

        List<Integer> productLowHigh = karatsubaMultiplyRecursive(multiplicandLowHigh, multipliplierLowHigh,multiplicandLowHigh.size(),multipliplierLowHigh.size());

        //Construct the middle portion of the product
        List<Integer> productMiddle = new ArrayList<>();
        for (int halfSizeIndex = 0; halfSizeIndex < m; ++halfSizeIndex) {
            productMiddle.add(halfSizeIndex,productLowHigh.get(halfSizeIndex) - productLow.get(halfSizeIndex) - productHigh.get(halfSizeIndex));
        }

        //Assemble the product from the low, middle and high parts. Start with the low and high parts of the product.
        for (int halfSizeIndex = 0, middleOffset = m/2; halfSizeIndex < m; ++halfSizeIndex) {
            product.add(halfSizeIndex,product.get(halfSizeIndex) + productLow.get(halfSizeIndex));
            product.add(halfSizeIndex + m, product.get(halfSizeIndex + m) + productHigh.get(halfSizeIndex));
            product.add(halfSizeIndex + middleOffset, product.get(halfSizeIndex+middleOffset) + productMiddle.get(halfSizeIndex));
        }

        return product;

    }

}
