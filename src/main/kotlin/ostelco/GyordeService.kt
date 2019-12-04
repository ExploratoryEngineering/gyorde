package ostelco

import gyorde.DeviceCheckGrpc.DeviceCheckImplBase
import gyorde.Gyorde.CheckDeviceRequest
import gyorde.Gyorde.CheckDeviceResponse
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import java.io.IOException


class GyordeService (): DeviceCheckImplBase() {

    override fun checkDevice(
        request: CheckDeviceRequest,
        responseObserver: StreamObserver<CheckDeviceResponse>
    ) {
        super.checkDevice(request, responseObserver)
    }
}


class GyordeServer(val port: Int) {
    val server: Server

    init {
        server = ServerBuilder.forPort(port).addService(GyordeService()).build()
    }



    /** Stop serving requests and shutdown resources.  */
    fun stop() {
        server.shutdown()
    }

    /** Start serving requests. */
    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() { // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down")
                this@GyordeServer.stop()
                System.err.println("*** server shut down")
            }
        })
    }
}


fun main(args: Array<String>) {
    println("Hello World!")
    val server = GyordeServer(8980)
    server.start()
}
