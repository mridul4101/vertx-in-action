package chapter1.firstapp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxEcho {

//    event handlers are always executed on the same thread, so there is no need for JVM locks or using AtomicInteger
    private static int numberOfConnections = 0;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

//        Creating a TCP server requires passing a callback for each new connection
        vertx.createNetServer()
            .connectHandler(VertxEcho::handleNewClient)
            .listen(3000);

//        defines a periodic task with a callback being executed every five seconds
        vertx.setPeriodic(5000, id -> System.out.println(howMany()));

//        an HTTP server is configured by giving the callback to be executed for each HTTP request
        vertx.createHttpServer()
            .requestHandler(request -> request.response().end(howMany()))
            .listen(8080);
    }

    private static void handleNewClient(NetSocket socket) {
        numberOfConnections++;

//        The buffer handler is invoked every time a buffer is ready for consumption.
        socket.handler(buffer -> {
//            Here we just write it back, and we use a convenient string conversion helper to look for a terminal command
            socket.write(buffer);
            if (buffer.toString().endsWith("/quit\n")) {
                socket.close();
            }
        });

//        when the connection closes we decrement a connections counter that was incremented upon connection
        socket.closeHandler(v -> numberOfConnections--);
    }

    private static String howMany() {
        return "We now have " + numberOfConnections + " connections";
    }
}
