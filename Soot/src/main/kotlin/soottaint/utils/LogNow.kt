package soottaint.utils

import java.io.File
import java.io.PrintWriter

abstract class EnumConverter<in V, E : Enum<E>>(
	private val valueMap: Map<V, E>
) {
	fun fromValue(value: V): E? = valueMap[value]
	fun fromValue(value: V?, default: E): E = valueMap[value] ?: default
}

inline fun <V, reified E : Enum<E>> buildValueMap(keySelector: (E) -> V): Map<V, E> =
	enumValues<E>().associateBy(keySelector)

inline fun <V, reified E> buildValueMap(): Map<V, E> where E : Enum<E>, E : HasValue<V> =
	enumValues<E>().associateBy { it.value }

interface HasValue<out T> {
	val value: T
}


enum class LogLev(override val value: Int) : HasValue<Int> {
	Verbose(0),
	Debug(1),
	Info(2),
	Warn(3),
	Error(4),
	Mute(5);

	companion object : EnumConverter<Int, LogLev>(buildValueMap())
}

class LogNow {

	companion object {
		var logLev = LogLev.Info
		private var fileFP = PrintWriter("log.txt")

		fun setLogLevel() {
			val levels = listOf("verbose", "debug", "info", "warn", "error", "mute")
			val lv = levels.indexOf("info")
			logLev = LogLev.fromValue(lv, LogLev.Info)
			LogNow.debug("Current LogLev: $logLev")
		}

		fun debug(msg: String) {
			if (logLev <= LogLev.Debug) {
				val li = DebugTool.getLineInfo(1)
				println("[Debug] $msg $li")
			}
		}

		fun info(msg: String) {
			if (logLev <= LogLev.Info) {
				val li = DebugTool.getLineInfo(1)
				println("[Info] $msg $li")
			}

		}

		fun warn(msg: String) {
			if (logLev <= LogLev.Warn) {
				val li = DebugTool.getLineInfo(1)
				println("[Warning] $msg $li")
			}
		}

		fun toFile(msg: String) {
			fileFP.write(msg + "\n")
			fileFP.flush()
		}

		fun error(msg: String) {
			if (logLev <= LogLev.Error) {
				val li = DebugTool.getLineInfo(1)
				System.err.println("[Error] $msg $li")
			}
		}

		// just print
		fun show(msg: String) {
			println(msg)
		}
	}
}