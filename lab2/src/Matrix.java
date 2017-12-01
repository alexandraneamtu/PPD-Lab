import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Matrix extends Thread
{
    private int rowNo;
    private int columnNo;
    private int[][] values;
    private boolean nonzero;

    public Matrix(){
    }

    public Matrix(String filename) {
        readFromFile(filename);
    }

    public Matrix(int rowNo,int columnNo, boolean nonzero){
        this.rowNo = rowNo;
        this.columnNo = columnNo;
        this.values = new int[this.rowNo][this.columnNo];

        if(nonzero==true){
            Random random = new Random();
            for(int i = 0; i < rowNo; i++)
                for(int j = 0; j < columnNo; j++)
                    values[i][j] = random.nextInt(20);
        }
    }

    public Matrix(int rowNo, int columnNo, int[][] values) {
        this.rowNo = rowNo;
        this.columnNo = columnNo;
        this.values = values;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public void setColumnNo(int columnNo) {
        this.columnNo = columnNo;
    }

    public int[][] getValues() {
        return values;
    }

    public void setValues(int i, int j, int value) {
        this.values[i][j] += value;
    }

    private void readFromFile(String filename){
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(filename));
            rowNo = Integer.parseInt(bf.readLine());
            columnNo = Integer.parseInt(bf.readLine());
            values = new int[rowNo][columnNo];
            for(int i=0; i< rowNo; i++){
                String[] line = bf.readLine().split(" ");
                for(int j=0;j<columnNo;j++) {
                    //String[] line = bf.readLine().split(" ");
                    values[i][j] = Integer.parseInt(line[j]);
                }}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sum(int i, int j, Matrix a, Matrix b) {

        this.setValues(i,j,a.getValues()[i][j] + b.getValues()[i][j]);
    }


    public void prod(int i, int j, Matrix a, Matrix b) {

        for (int k = 0; k < a.getColumnNo(); k++) // aColumn
            this.setValues(i,j, a.getValues()[i][k] * b.getValues()[k][j]);
    }

}
