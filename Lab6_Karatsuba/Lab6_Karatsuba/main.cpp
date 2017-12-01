//
//  main.cpp
//  Lab6_Karatsuba
//
//  Created by Alexandra Neamtu on 26/11/2017.
//  Copyright © 2017 Alexandra Neamtu. All rights reserved.
//

#define N 1000
#define T 5000
#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <chrono>
#include <vector>
#include <future>
#include <thread>
#include <mutex>

typedef std::chrono::high_resolution_clock Clock;

int polynomial1[N+1],
    polynomial2[N+1],
    resultLinear[2*N + 1],
    resultNThreads[2*N + 1],
    resultLinearKaratsuba[2*N+1],
    resultNThreadsKaratsuba[2*N+1];

std::mutex mutex[2*N + 1];


void initPolynoms() {
    srand(int(time(NULL)));
    for (int i=0; i<=N; i++){
        polynomial1[i] = rand() % 10;
        polynomial2[i] = rand() % 10;
    }
}

void printPolynom(int *P, int len){
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

void multiplyLinear(){
    for (int i=0; i<=N; i++)
        for (int j=0; j<=N; j++)
            resultLinear[i+j] += (polynomial1[i] * polynomial2[j]);
}

void multiplyNThreads(int tid){
    //for each degree in the resulting polynomial
    for(int s=tid; s<=N; s += T)
        for(int t=0; t<=N; t++){
            mutex[s+t].lock();
            resultNThreads[s+t] += (polynomial1[s] * polynomial2[t]);
            mutex[s+t].unlock();
        }
}

void multiplyLinearKaratsuba(){
    for(int s=0; s<=N-1; s++){
        resultLinearKaratsuba[2*s] += polynomial1[s] * polynomial2[s];
        for(int t=s+1; t<=N; t++){
            resultLinearKaratsuba[s+t] += (((polynomial1[s] + polynomial1[t]) * (polynomial2[s] + polynomial2[t])) - polynomial1[s]*polynomial2[s] - polynomial1[t]*polynomial2[t]);
        }
    }
    resultLinearKaratsuba[0] = polynomial1[0]*polynomial2[0];
    resultLinearKaratsuba[2*N] = polynomial1[N]*polynomial2[N];
}

void multiplyNThreadsKaratsuba(int tid){
    
    for(int s=tid; s<=N-1; s += T)
    {
        resultNThreadsKaratsuba[2*s] += polynomial1[s] * polynomial2[s];
        for(int t=s+1; t<=N; t++)
        {
            mutex[s+t].lock();
            resultNThreadsKaratsuba[s+t] += (((polynomial1[s] + polynomial1[t]) * (polynomial2[s] + polynomial2[t])) - polynomial1[s]*polynomial2[s] - polynomial1[t]*polynomial2[t]);
            mutex[s+t].unlock();
        }
    }
}

int main(int argc, char **argv)
{
    initPolynoms();
    //std::cout<<"First polynomial: ";
    //printPolynom(polynomial1, N);
    
    //std::cout<<"Second polynomial: ";
    //printPolynom(polynomial2, N);
    
    /*
     multiply linear
     */
    
    auto mt_start1 = Clock::now();
    multiplyLinear();
    auto mt_end1 = Clock::now();
    std::cout<<std::endl;
    std::cout<< N <<" degree polynomials on one thread: ";
    std::cout << (std::chrono::duration_cast<std::chrono::nanoseconds>(mt_end1 - mt_start1)).count() << " nanos" << '\n';
    //printPolynom(resultLinear, 2*N);
    
    /*
     multiply linear Karatsuba algorithm
     */
    
    
    auto mt_start2 = Clock::now();
    multiplyLinearKaratsuba();
    auto mt_end2 = Clock::now();
    std::cout<<std::endl;
    std::cout<< N <<" degree polynomials on one thread (Karatsuba): ";
    std::cout << (std::chrono::duration_cast<std::chrono::nanoseconds>(mt_end2 - mt_start2)).count() << " nanos" << '\n';
    //printPolynom(resultLinearKaratsuba, 2*N);
    
    
    /*
     multiply on T threads
     */
    
    std::vector<std::future<void>> multiplyThreads;
    auto mt_start = Clock::now();
    
    for (int i = 0; i < T; i++) {
        multiplyThreads.emplace_back(std::async(multiplyNThreads, i));
    }
    for (auto &&res: multiplyThreads){
        res.get();
    }
    
    auto mt_end = Clock::now();
    std::cout<<std::endl;
    std::cout<< N <<" degree polynomials on " << T << " threads: ";
    std::cout << (std::chrono::duration_cast<std::chrono::nanoseconds>(mt_end - mt_start)).count() << " nanos" << '\n';
    
    //printPolynom(resultNThreads, 2*N);
    
    /*
     multiply on T threads Karatsuba algorithm
     */
    
    
    std::vector<std::future<void>> karatsubaThreads;
    auto mt_start3 = Clock::now();
    for (int i = 0; i < T; i++) {
        karatsubaThreads.emplace_back(std::async(multiplyNThreadsKaratsuba, i));
    }
    for (auto &&res: karatsubaThreads){
        res.get();
    }
    resultNThreadsKaratsuba[2*N] = polynomial1[N]*polynomial2[N];
    auto mt_end3 = Clock::now();
    std::cout<<std::endl;
    std::cout<< N <<" degree polynomials on "<< T <<" threads (Karatsuba): ";
    std::cout << (std::chrono::duration_cast<std::chrono::nanoseconds>(mt_end3 - mt_start3)).count() << " nanos" << '\n';
    //printPolynom(resultNThreadsKaratsuba, 2*N);
    
    return 0;
}
