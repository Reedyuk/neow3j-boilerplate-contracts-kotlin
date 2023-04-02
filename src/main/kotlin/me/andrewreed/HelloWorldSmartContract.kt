package me.andrewreed

import io.neow3j.devpack.ByteString
import io.neow3j.devpack.Hash160
import io.neow3j.devpack.Storage
import io.neow3j.devpack.annotations.DisplayName
import io.neow3j.devpack.annotations.ManifestExtra
import io.neow3j.devpack.annotations.OnDeployment

@DisplayName("HelloWorld")
@ManifestExtra(key = "author", value = "Andrew Reed")
object HelloWorldSmartContract {

    @JvmStatic
    fun getStaticValue(): String = "simple test value"

    @JvmStatic
    fun getOwner(): ByteString? =
        Storage.get(Storage.getReadOnlyContext(), byteArrayOf(0x00))

    @JvmStatic @OnDeployment
    fun deploy(data: Any?, update: Boolean) {
        if (!update) {
            Storage.put(Storage.getStorageContext(), byteArrayOf(0x00), data as Hash160?)
        }
    }
}
