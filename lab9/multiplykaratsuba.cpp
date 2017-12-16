#include<mpi.h>
#include<iostream>
#include<chrono>
#include<vector>
#include <time.h>


using namespace std;


void printPolynom(vector<int> const P, int len)
{
    for (int i=0; i<=len; i++)
    {
        std::cout<<(P[i]);
        if (i != 0)
            std::cout<<"*x^"<<i;
        if (i != len)
            std::cout<<" + ";
    }
    std::cout<<'\n';
}

void generate(vector<int>& v, int n)
{
    v.clear();
    v.reserve(n);
    for(int i=0; i<n; i++)
        v.push_back(rand()%5);

}

vector<int> multiplyRegular(vector<int> const &polynomial1, vector<int> const &polynomial2, size_t processes)
{
    int n=polynomial1.size();

    for(size_t i = 1; i<processes; i++)
    {
        int begin = (i*n) / processes;
        //cout<<"BEGIN: "<<begin<<endl;
        int end = ((i+1)*n) / processes;
        //cout<<"END: "<<end<<endl;
        int nrElems = end - begin;
        MPI_Ssend(&end, 1 , MPI_INT, i, 2, MPI_COMM_WORLD);
        MPI_Ssend(&n, 1, MPI_INT, i, 3, MPI_COMM_WORLD);
        MPI_Ssend(&begin, 1, MPI_INT,i ,4, MPI_COMM_WORLD);
        MPI_Ssend(polynomial1.data(), n , MPI_INT, i, 5, MPI_COMM_WORLD);
        MPI_Ssend(polynomial2.data(), n , MPI_INT, i, 6, MPI_COMM_WORLD);
    }

    vector<int> result(2*n,0);
    for (int i=0; i < n/processes; i++){
        result[2 * i] += polynomial1[i] * polynomial2[i];
        for (int j=i+1; j < n; j++)
            result[i+j] += (((polynomial1[i] + polynomial1[j]) * (polynomial2[i] + polynomial2[j])) - polynomial1[i]*polynomial2[i] - polynomial1[j]*polynomial2[j]);
    }



    MPI_Status status;
    for(size_t i = 1; i < processes; i++)
    {
        vector<int> partial(2*n);
        MPI_Recv(partial.data(),2*n,MPI_INT, i, 7, MPI_COMM_WORLD, &status);
        for(int j=0; j<2*n;j++)
            result[j] += partial[j];
    }

    result[2*n - 2] = polynomial1[n-1]*polynomial2[n-1];
    return result;
}

void worker()
{
    int n=0;
    int begin = 0 ,end =0;

    MPI_Status status;
    MPI_Recv(&end, 1, MPI_INT, 0, 2, MPI_COMM_WORLD, &status);
    MPI_Recv(&n, 1, MPI_INT, 0, 3, MPI_COMM_WORLD, &status);
    MPI_Recv(&begin, 1, MPI_INT, 0 ,4, MPI_COMM_WORLD, &status);


    vector<int> polynomial1(n);
    vector<int> polynomial2(n);
    vector<int> res(2*n,0);

    MPI_Recv(polynomial1.data(),n,MPI_INT,0,5, MPI_COMM_WORLD, &status);
    MPI_Recv(polynomial2.data(),n,MPI_INT,0,6,MPI_COMM_WORLD, &status);

    for (int i=begin; i<end; i++){
        res[2 * i] += polynomial1[i] * polynomial2[i];
        for (int j=i+1; j<n; j++)
            res[i+j] += (((polynomial1[i] + polynomial1[j]) * (polynomial2[i] + polynomial2[j])) - polynomial1[i]*polynomial2[i] - polynomial1[j]*polynomial2[j]);
    }



    MPI_Ssend(res.data(), 2*n, MPI_INT, 0 ,7, MPI_COMM_WORLD);
}

bool check(vector<int> const polynomial1, vector<int> const polynomial2,int n, vector<int> const result){
    vector<int> resultLinear(2*n+1,0);

    for (int i=0; i<=n; i++)
        for (int j=0; j<=n; j++)
            resultLinear[i+j] += (polynomial1[i] * polynomial2[j]);

    //printPolynom(resultLinear,2*n);

    for (int i=0; i<=n; i++)
        for (int j=0; j<=n; j++)
            if( resultLinear[i+j] != result[i+j])
                return false;



    return true;

}

int main()
{
    srand(int(time(NULL)));
    int degree = 10;
    MPI_Init(0,0);
    int rank;
    int size;
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);


    if(rank == 0){
        vector<int> polynomial1;
        vector<int> polynomial2;
        vector<int> result(2*degree+1,0);

        generate(polynomial1, degree+1);
        generate(polynomial2, degree+1);

        //cout<<"polynomial1"<<endl;
        //printPolynom(polynomial1,degree);
        //cout<<"polynomial2"<<endl;
        //printPolynom(polynomial2,degree);
        //std::cout<<'\n';


        chrono::high_resolution_clock::time_point const beginTime = chrono::high_resolution_clock::now();
        result = multiplyRegular(polynomial1,polynomial2,size);
        chrono::high_resolution_clock::time_point const endTime = chrono::high_resolution_clock::now();


        //cout<<"RESULT:"<<endl;
        //printPolynom(result,2*degree);
        printf("Result %s, time=%ldnanosec\n", (check(polynomial1, polynomial2,degree,result) ? "ok" : "FAIL"),
            (chrono::duration_cast<chrono::nanoseconds>(endTime-beginTime)).count());

    }
    else{
        worker();
    }

    MPI_Finalize();
}