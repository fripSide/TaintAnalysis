package soottaint.taintanalysis.base

import soot.*
import soot.Unit
import soot.jimple.*
import soot.jimple.internal.JAssignStmt
import soot.jimple.internal.JIdentityStmt
import soot.jimple.internal.JimpleLocal
import soot.jimple.internal.JimpleLocalBox
import soot.toolkits.graph.Block
import soot.toolkits.graph.BriefUnitGraph
import soot.toolkits.graph.CompleteBlockGraph
import soot.util.Chain
import soottaint.utils.DebugTool
import soottaint.utils.SootTool
import kotlin.system.exitProcess

class SourcePoint(var mtd: SootMethod, var block: Block, var inst: Unit, var value: Value) {

}

class SinkMethod {

}

class SimulateEngine: AbstractStmtSwitch<Unit>() {

	override fun caseBreakpointStmt(stmt: BreakpointStmt?) {
		super.caseBreakpointStmt(stmt)
	}

	override fun defaultCase(obj: Any?) {
		println("unsupported stmt: $obj")
	}
}

class Pass0() {

	val kSourceMtd = "void main(java.lang.String[])"
	val kSourceVarTag = ""

	val sourceList = arrayListOf<SourcePoint>()

	fun startAnalyze() {
		getSourceMethod()
	}

	fun getSourceMethod() {
		val clsList = SootTool.filterClasses("InterTest")
		assert(clsList.size == 1)
		val cls = clsList.first()
		for (mtd in cls.methods) {
			if (mtd.isMain) {
				println(mtd.subSignature)
				var body = mtd.retrieveActiveBody() ?: continue
				var bg = CompleteBlockGraph(body)
				for (b in bg) {
					println(b.head)
					println("pred:" + bg.getPredsOf(b))
					for (u in b) {
						if (u is JIdentityStmt) {
							println(u.rightOpBox)
						}
					    var uu: soot.Unit
						u.apply(SimulateEngine())
//						u.apply()
//						u.useAndDefBoxes?.forEach{ box ->
//							println(box.value)
//							if (box.value is JimpleLocal) {
//								println("jt: " + (box.value as JimpleLocal).type)
//							}
//
//						}
//						println(u.javaClass.toString() + " " + u)
					}
				}
			}
		}
	}


}

private fun Unit.apply(any: Any) {

}
