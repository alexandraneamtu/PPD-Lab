import java.util.ArrayList;

public class ThreadWorker extends Thread {

    private Polynomial A;
    private Polynomial B;
    private Polynomial result;
    private int j;

    public ThreadWorker(Polynomial A, Polynomial matrix2, Polynomial result, int j) {
        this.A = A;
        this.B = B;
        this.result = result;
        this.j = j;
    }

    @Override
    public void run(){
        for(int i=0;i<A.coeff().size();i++)
            result.coeff().set(i+j,result.coeff().get(i+j)+A.coeff().get(i)*B.coeff().get(j));
    }
}
