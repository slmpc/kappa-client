package dev.slmpc.kappaclient.event

import dev.slmpc.kappaclient.util.threads.runSafe


inline fun <reified E : Event> Any.safeEventListener(
    noinline function: SafeClientEvent.(E) -> Unit
) = eventListener(this, E::class.java, 0, false) { runSafe { function.invoke(this, it) } }

inline fun <reified E : Event> Any.safeEventListener(
    priority: Int,
    noinline function: SafeClientEvent.(E) -> Unit
) = eventListener(this, E::class.java, priority, false) { runSafe { function.invoke(this, it) } }

inline fun <reified E : Event> Any.safeEventListener(
    priority: Int,
    alwaysListening: Boolean,
    noinline function: SafeClientEvent.(E) -> Unit
) = eventListener(this, E::class.java, priority, alwaysListening) { runSafe { function.invoke(this, it) } }

@JvmOverloads
@JvmSynthetic
inline fun <reified E : Event> Any.eventListener(
        priority: Int = 0,
        alwaysListening: Boolean = false,
        noinline function: (E) -> Unit
) = eventListener(this, E::class.java, priority, alwaysListening, function)

fun <E : Event> Any.eventListener(
        owner: Any,
        eventClass: Class<E>,
        priority: Int,
        alwaysListening: Boolean,
        function: (E) -> Unit
) {
    with(EventListener(owner, eventClass, EventBus.busID, priority, function)) {
        if (alwaysListening) EventBus.subscribe(this)
        else EventBus.register(owner, this)
    }
}

open class EventListener<E : Any>(
        owner: Any,
        val eventClass: Class<E>,
        val eventID: Int,
        val priority: Int,
        val function: (E) -> Unit
) : Comparable<EventListener<*>> {
    override fun compareTo(other: EventListener<*>): Int {
        return other.priority.compareTo(priority)
    }

    override fun equals(other: Any?): Boolean {
        return this === other
                || (other is EventListener<*>
                && other.eventClass == this.eventClass
                && other.eventID == this.eventID)
    }

    override fun hashCode(): Int {
        var result = eventClass.hashCode()
        result = 31 * result + priority
        result = 31 * result + function.hashCode()
        return result
    }
}