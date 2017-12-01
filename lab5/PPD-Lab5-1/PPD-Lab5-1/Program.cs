using System;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;
using System.Text;
using System.Text.RegularExpressions;

namespace PPDLab51
{
    class MainClass
    {
        private const int port = 80;

        // ManualResetEvent instances signal completion.  
        private static ManualResetEvent connectDone = new ManualResetEvent(false);
        private static ManualResetEvent sendDone = new ManualResetEvent(false);
        private static ManualResetEvent receiveDone = new ManualResetEvent(false);

        // The response from the remote device.  
        private static String response = String.Empty;

        //stack used for the Task version (to get in the continueWith the value)
       // static ConcurrentStack<string> lengths = new ConcurrentStack<string>();



        public static void Main(string[] args)
        {
           
            List<String> URLs = new List<String>() { "/~rlupsa/edu/pdp/lab-1-noncooperative-mt.html", "/~rlupsa/edu/pdp/lab-2-parallel-simple.html", "/~rlupsa/edu/pdp/lab-3-async.html" };
            //TPL parallel version
            Parallel.ForEach(URLs,(url) => StartClient(url));

            //Tasks with continuation version
            //Task t1 = Task.Factory.StartNew(() => StartClient("/~rlupsa/edu/pdp/lab-1-noncooperative-mt.html"));
            //Task t2 = Task.Factory.StartNew(() => StartClient("/~rlupsa/edu/pdp/lab-2-parallel-simple.html"));
            //Task t3 = Task.Factory.StartNew(() => StartClient("/~rlupsa/edu/pdp/lab-3-async.html"));
            //Task.WaitAll(new Task[]{t1,t2,t3});

            //Async/Await version
            //Parallel.ForEach(URLs, (url) => StartClientAsync(url));


            Console.ReadLine();
        }

        private static void StartClient(string url)
        {
            //Connect to a remote device
            try
            {
                // Establish the remote endpoint for the socket.
                IPHostEntry ipHostInfo = Dns.GetHostEntry("www.cs.ubbcluj.ro");
                IPAddress ipAddress = ipHostInfo.AddressList[0];
                IPEndPoint remoteEndPoint = new IPEndPoint(ipAddress, port);

                //Create a TCP/IP socket
                Socket client = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

                //Connect to the remote endpoint
                client.BeginConnect(remoteEndPoint, new AsyncCallback(ConnectCallBack), client);
                connectDone.WaitOne();

                //Send test data to the remote device
                Send(client, url);
                sendDone.WaitOne();

                //Receive the response from the remote device
                Receive(client);
                receiveDone.WaitOne();

                //Release the socket
                client.Shutdown(SocketShutdown.Both);
                client.Close();

            }
            catch(Exception e){
                Console.WriteLine(e.ToString());
            }
        }

        private static async Task StartClientAsync(string url)
        {
            try
            {
                IPHostEntry ipHostInfo = Dns.GetHostEntry("www.cs.ubbcluj.ro");
                IPAddress ipAddress = ipHostInfo.AddressList[0];
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, port);

                Socket client = new Socket(AddressFamily.InterNetwork,
                    SocketType.Stream, ProtocolType.Tcp);

                var connectResult = await Task.Run(() =>
                    client.BeginConnect(remoteEP, new AsyncCallback(ConnectCallBack), client)
                );
                connectDone.WaitOne();

                await Task.Run(() => Send(client, url));
                sendDone.WaitOne();

                await Task.Run(() =>
                    Receive(client)
                );
                receiveDone.WaitOne();

                //Console.WriteLine("Response received : {0}", response);

                client.Shutdown(SocketShutdown.Both);
                client.Close();

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }




        private static void ConnectCallBack(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.  
                Socket client = (Socket)ar.AsyncState;

                // Complete the connection.  
                client.EndConnect(ar);

                Console.WriteLine("Socket connected to {0}",
                    client.RemoteEndPoint.ToString());

                // Signal that the connection has been made.  
                connectDone.Set();
            }
            catch(Exception e)
            {
                Console.WriteLine(e.ToString());  
            }
        }

        private static void Send(Socket client, string v)
        {
            try
            {
                string ip = ((IPEndPoint)(client.RemoteEndPoint)).Address.ToString();
                //string fullURL = ip ;



                string GETrequest = "GET " + v + " HTTP/1.1\r\nHost:" + ip + "\r\nConnection: keep-alive\r\nAccept: text/html\r\nUser-Agent: CSharpTests\r\n\r\n";
                Console.WriteLine(GETrequest);
                var byteData = Encoding.ASCII.GetBytes(GETrequest);
                client.BeginSend(byteData, 0, byteData.Length, 0,
                    new AsyncCallback(SendCallback), client);
            }
            catch(Exception e)
            {
                Console.WriteLine(e.ToString());
            }
            
        }

        private static void SendCallback(IAsyncResult ar)
        {
            try
            {
                // Retrieve the socket from the state object.  
                Socket client = (Socket)ar.AsyncState;

                // Complete sending the data to the remote device.  
                int bytesSent = client.EndSend(ar);
                Console.WriteLine("Sent {0} bytes to server.", bytesSent);

                // Signal that all bytes have been sent.  
                sendDone.Set();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        private static void Receive(Socket client)
        {
            try
            {
                // Create the state object.  
                StateObject state = new StateObject();
                state.workSocket = client;

                // Begin receiving the data from the remote device.  
                client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
                    new AsyncCallback(ReceiveCallback), state);

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());

            }
        }

        private static void ReceiveCallback(IAsyncResult ar)
        {
            try
            {
            // Retrieve the state object and the client socket   
            // from the asynchronous state object.  
            StateObject state = (StateObject)ar.AsyncState;
            Socket client = state.workSocket;
            

            // Read data from the remote device.  
            int bytesRead = client.EndReceive(ar);
            if (bytesRead > 0)
            {
                // There might be more data, so store the data received so far.  
                state.sb.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));
             

                // Get the rest of the data.  
                client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0,
                  new AsyncCallback(ReceiveCallback), state);
            }
            else
            {
                // All the data has arrived; put it in response.  
                if (state.sb.Length > 1)
                {
                        response = state.sb.ToString();
                        
                        //Console.WriteLine("----------------" + response);
                        Console.WriteLine(response.Split('\n')[6]);
                        //Regex reg = new Regex("\\\r\nContent-Length: (.*?)\\\r\n");
                        //Match m = reg.Match(response);
                        //int contentLength;
                        //int.TryParse(m.Groups[1].ToString(), out contentLength);

                        //string contentLength = response.Split('\n')[6];
                        //int length = Int32.Parse(contentLength.Split(' ')[1]);
                       //lengths.Push(contentLength);
                }
                // Signal that all bytes have been received.  
                receiveDone.Set();
               
            }

        }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
               
            }
        }
    }
}
