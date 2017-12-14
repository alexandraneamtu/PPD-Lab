using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Numerics;
using System.Threading;
using System.Threading.Tasks;
using BigMath;

namespace Program
{
    class MainClass
    {

        public static Stopwatch stopwatch = new Stopwatch();
        public static int N = 20;
        public static int K = 10;
        public static List<Int256[]> Numbers = new List<Int256[]>();
        public static Int256[] Result = new Int256[N];
        public static ConcurrentQueue<Tuple<Int256, int>> Queue = new ConcurrentQueue<Tuple<Int256, int>>();
        public static List<Mutex> Mutexes = new List<Mutex>();
        public static List<Task> Tasks = new List<Task>();
        public static void Main(string[] args)
        {


            InitializeNumbers(K);
            foreach (var no in Numbers)
            {
                foreach (var digit in no)
                    Console.Write(digit + "");
                Console.WriteLine("+");
            }

            stopwatch.Start();
            for (int i = 0; i < K; i += 2)
            {
                int newI = i;
                int newJ = i + 1;
                Tasks.Add(Task.Factory.StartNew(() => Process(newI, newJ)));
            }
            Task.WaitAll(Tasks.ToArray());
            while (!Queue.IsEmpty)
            {
                Tuple<Int256, int> enqueuedItem;
                Queue.TryDequeue(out enqueuedItem);
                if (enqueuedItem != null)
                {
                    Result[enqueuedItem.Item2] += enqueuedItem.Item1;
                }
            }
            bool carry = false;
            int carryMagnitude = 0;
            for (int i = N - 1; i >= 0; i--)
            {
                //Console.WriteLine(Result[i]);
                if (carry)
                    Result[i] += carryMagnitude;
                if (Result[i] > 9 && i != 0)
                {
                    //Console.WriteLine("####:" + Result[i]);
                    carry = true;
                    Int256 t = Result[i];
                    var cm = int.Parse(t.ToString()[0].ToString());
                    while (t >= 10)
                    {
                        t /= 10;
                    }

                    //carryMagnitude = t%10;
                    carryMagnitude = cm;
                    //Console.WriteLine("cm:" + carryMagnitude);
                    Result[i] %= 10;
                }
                else
                {
                    carry = false;
                    carryMagnitude = 0;
                }

            }
            stopwatch.Stop();
            for (int i = 0; i < N; i++)
                Console.Write("-");
            Console.WriteLine("=");
            foreach (var r in Result)
                Console.Write(r + "");
            Console.WriteLine("\nTime elapsed: {0}", stopwatch.ElapsedMilliseconds);
            Console.WriteLine();
            Console.WriteLine(Check() ? "OK" : "Not OK");



        }

        public static void InitializeNumbers(int k)
        {
            Random rand = new Random();
            for (int i = 0; i < k; i++)
            {
                Int256[] a = new Int256[N];
                for (int j = 0; j < N; j++)
                {
                    a[j] = rand.Next(1, 9);
                }
                Numbers.Add(a);
            }
            for (int i = 0; i < N; i++)
            {
                Mutexes.Add(new Mutex());
            }
        }

        public static void Process(int i, int j)
        {
            if (j < N)
            {
                for (int d = 0; d < N; d++)
                {
                    Queue.Enqueue(new Tuple<Int256, int>(Numbers[i][d] + Numbers[j][d], d));
                }
            }
            else
            {
                for (int d = 0; d < N; d++)
                {
                    Queue.Enqueue(new Tuple<Int256, int>(Numbers[i][d], d));
                }
            }
        }

        public static bool Check()
        {
            Int256[] numbers_local = new Int256[K];
            for (int k = 0; k < K; k++)
            {
                Int256 no_local = 0;
                for (int d = 0; d < N; d++)
                {
                    no_local = no_local * 10 + Numbers[k][d];
                }
                numbers_local[k] = no_local;

            }
            Int256 sum = 0;
            for (int k = 0; k < K; k++)
            {
                sum += numbers_local[k];
            }
            Int256[] resLocal = new Int256[N + 1];
            int t = N + 1;

            while (sum > 0)
            {
                resLocal[--t] = sum % 10;
                sum /= 10;
            }
            Console.WriteLine($"Local {String.Join("", resLocal)}");
            Console.WriteLine($"Global {String.Join("", Result)}");
            if (!String.Join("", Result).Equals(String.Join("", resLocal)))
                return false;
            return true;
        }
    }
}
