package org.ostelco.policycontrol

import com.google.protobuf.ByteString
import gyorde.DeviceCheckGrpc.*
import gyorde.Gyorde
import gyorde.Gyorde.CheckDeviceRequest
import gyorde.Gyorde.CheckDeviceResponse
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver


/**
 * A  function type that is intended to be implemented by the implementer of the
 * DeviceCheck service.   It will give all the parameters that are provided by the
 * incoming  DeviceCheck request, calculate a boolean value, that indicates if this
 * request should be permitted or not.
 */
typealias Predicate = (Long, Gyorde.CheckDeviceRequest.IPType, ByteString) -> Boolean


/**
 * Service implementation, implementing a grpc service for
 * device checks.
 */
class DeviceCheckService (val p: Predicate): DeviceCheckImplBase() {

    override fun checkDevice(
        request: CheckDeviceRequest,
        responseObserver: StreamObserver<CheckDeviceResponse>
    ) {
        val r = p(request.imsi, request.ipType , request.ipAddress)
        val result =
            CheckDeviceResponse.newBuilder().setSuccess(r).build()
        responseObserver.onNext(result)
        responseObserver.onCompleted()
    }
}


/**
 * A server for serving DeviceCheck requests.   Will start a sever
 * in the backbround, and print a nice entry on stderr when shutting down.
 */
class DeviceCheckServer(val port: Int, val p: Predicate) {
    val server: Server

    init {
        server = ServerBuilder.forPort(port).addService(
            DeviceCheckService(
                p
            )
        ).build()
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
                this@DeviceCheckServer.stop()
                System.err.println("*** server shut down")
            }
        })
    }
}
