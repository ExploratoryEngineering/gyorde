package org.ostelco.policycontrol

import com.google.protobuf.ByteString
import gyorde.DeviceCheckGrpc
import gyorde.Gyorde
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit

/**
 *  Set up a DeviceCheck client that will sends reqets towards a host/port.
 */
class DeviceCheckClient(val host: String, val port: Int) {

    val blockingStub: DeviceCheckGrpc.DeviceCheckBlockingStub

    val asyncStub: DeviceCheckGrpc.DeviceCheckStub

    val channel: ManagedChannel

    init {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
        blockingStub = DeviceCheckGrpc.newBlockingStub(channel)
        asyncStub = DeviceCheckGrpc.newStub(channel);
    }

    @Throws(InterruptedException::class)
    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    /**
     * Invoke a gRPC call towards the server, return the response
     * value.  The request is sent as a blocking request.
     */
    fun checkDevice(
        imsi: Long,
        ipType: Gyorde.CheckDeviceRequest.IPType,
        ipAddress: ByteString
    ): gyorde.Gyorde.CheckDeviceResponse {

        val request = Gyorde.CheckDeviceRequest.newBuilder()
            .setImsi(imsi)
            .setIpAddress(ipAddress)
            .setIpType(ipType).build()
        return blockingStub.checkDevice(request)
    }
}