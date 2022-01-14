package soottaint.common

object JDKHelper {

	private val methodSet = hashSetOf("<java.lang.Integer: int parseInt(java.lang.String)>")

	fun isMethodTaint(sign: String): Boolean {
		return methodSet.contains(sign)
	}
}