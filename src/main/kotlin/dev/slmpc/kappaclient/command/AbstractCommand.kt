package dev.slmpc.kappaclient.command

abstract class AbstractCommand(
    val key: Array<String>
) {

    abstract fun run(args: Array<String>)

}