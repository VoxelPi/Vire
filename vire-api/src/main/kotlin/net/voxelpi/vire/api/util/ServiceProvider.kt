package net.voxelpi.vire.api.util

class ServiceProvider<T: Any> {

    private var provider: T? = null

    fun get(): T {
        return provider ?: throw IllegalStateException("No service provider is loaded.")
    }

    fun register(provider: T) {
        this.provider = provider
    }

    fun unregister() {
        this.provider = null
    }
}
