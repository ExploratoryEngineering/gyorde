package ostelco

import com.google.protobuf.ByteString
import gyorde.DeviceCheckGrpc
import gyorde.DeviceCheckGrpc.*
import gyorde.Gyorde.CheckDeviceRequest
import gyorde.Gyorde.CheckDeviceResponse
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit





class GyordeService (): DeviceCheckImplBase() {

    override fun checkDevice(
        request: CheckDeviceRequest,
        responseObserver: StreamObserver<CheckDeviceResponse>
    ) {
        // super.checkDevice(request, responseObserver)
        val result = CheckDeviceResponse.newBuilder().setSuccess(true).build()
        responseObserver.onNext(result)
        responseObserver.onCompleted()
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

class GyordeClient(val host: String, val port: Int) {

    val blockingStub: DeviceCheckBlockingStub

    val asyncStub: DeviceCheckStub

    val channel: ManagedChannel

    init {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
        blockingStub = newBlockingStub(channel)
        asyncStub = newStub(channel);
    }

    @Throws(InterruptedException::class)
    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    fun checkDevice(imsi: String, ipAddress: String): gyorde.Gyorde.CheckDeviceResponse {
        // TODO:   IMSI should not be numerical, too dangerous, make it a string.
        val request = CheckDeviceRequest.newBuilder()
            .setImsi(123L)
            .setIpAddress(ByteString.EMPTY).build()
        return blockingStub.checkDevice(request)
    }
}