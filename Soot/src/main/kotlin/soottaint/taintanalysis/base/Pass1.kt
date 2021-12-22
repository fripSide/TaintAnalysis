package soottaint.taintanalysis.base

import soot.BriefUnitPrinter
import soot.SootClass
import soot.SootMethod
import soot.jimple.IfStmt
import soot.jimple.LookupSwitchStmt
import soot.jimple.TableSwitchStmt
import soot.jimple.internal.*
import soot.toolkits.graph.Block
import soot.toolkits.graph.BriefBlockGraph
import soot.toolkits.graph.BriefUnitGraph
import soot.toolkits.graph.HashMutableEdgeLabelledDirectedGraph
import soot.toolkits.graph.pdg.HashMutablePDG
import soot.toolkits.graph.pdg.IRegion
import soot.toolkits.graph.pdg.PDGNode
import soot.util.Chain
import soot.util.HashChain
import soottaint.utils.DebugTool
import soottaint.utils.LogNow
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class IntraTaintAnalysis(var mtd: SootMethod, var interAna: InterTaintAnalysis) {

	var stmtMap = hashMapOf<Int, soot.Unit>()
	var blockProcessList = arrayListOf<Block>()
	var entrySetMap = hashMapOf<soot.Unit, HashSet<String>>()
	var exitSetMap = hashMapOf<soot.Unit, HashSet<String>>()
	var controllingBlocks = arrayListOf<Block>()
	var directCdg = hashMapOf<soot.Unit, soot.Unit>()
	var indirectCdg = hashMapOf<soot.Unit, HashSet<soot.Unit>>()
	var isInvokedBy_Expr = false

//	var cfg: BriefBlockGraph? = null

	init {
		val cfg = BriefUnitGraph(mtd.retrieveActiveBody())
		stmtMap = hashMapOf<Int, soot.Unit>()

		var unitNo = 0
		for (un in cfg) {
//			println(un)
			stmtMap[unitNo] = un
			entrySetMap[un] = hashSetOf()
			exitSetMap[un] = hashSetOf()
			unitNo++
		}

		val pdg = HashMutablePDG(cfg)
//		val pdgnodes = pdg.get
		for (pg in pdg) {
			if (PDGNode.Type.CFGNODE == pg.type) {
				var blk1 = pg.node as Block
				var blkTail = blk1.tail
				if (blkTail is TableSwitchStmt || blkTail is LookupSwitchStmt || blkTail is IfStmt) {

					for (node in pdg) {
						if (pdg.dependentOn(node, pg)) {
//							println("$node DependOn: $pg")
//							DebugTool.exitHere("printGraph")
							var region = node.node as IRegion
							for (ui in region.units) {
								directCdg[ui] = blkTail
							}
						}
					}

				}
			}
		}

		for (un in directCdg.keys) {
			val tmpU = hashSetOf<soot.Unit>()
			indirectCdg[un] = tmpU
			var u2 = un
			while (directCdg.containsKey(u2)) {
				u2 = directCdg[u2]!!
				indirectCdg[un]!!.add(u2)
			}
		}
	}

	fun stmtAnalysis() {
//		println("stmtAnalysis: " + mtd)
		var bfg = BriefBlockGraph(mtd.retrieveActiveBody())
		var curBlock = bfg.blocks.first()
		blockProcessList.add(curBlock)
		var mtd = curBlock.body.method
		if (mtd.isMain) {
//			interAna
			println("Main Method: " + mtd.parameterTypes[0] + " " + mtd.parameterCount)
		}

		for (blk in bfg.blocks) {
			println(blk)
			println("Finish Block")
		}
//		DebugTool.exitHere("")

		while (blockProcessList.isNotEmpty()) {
			var curBlock = blockProcessList.first()
//			LogNow.info("curblock: " + curBlock)
			isInvokedBy_Expr = false
			var entrySet = entrySetMap[curBlock.head]
			for (prd in curBlock.preds) {
				var tail = prd.tail
				var exitSet = exitSetMap[tail]
				setCopyFn(exitSet, entrySet)
			}
			entrySetMap[curBlock.head] = entrySet!!

			if (curBlock.preds.isNotEmpty()) {
				val controlBlock = curBlock.preds[0]
				if (controllingBlocks.contains(controlBlock) && indirectCdg.containsKey(curBlock.head)) {
					val curlUnit = indirectCdg[curBlock.head]
					if (curlUnit!!.contains(controlBlock.tail)) {
						isInvokedBy_Expr = true
					}
				}
			}

			var stmt: Unit?
			var prevStmt: Unit?
			var prevExit: HashSet<String>?
			for (uni in curBlock) {
				println("$uni in curBlock")
				var stmt = uni
				var entrySet1 = entrySetMap[stmt]
				var exitSet1 = exitSetMap[stmt]

				kill(entrySet1, exitSet1, stmt)
				gen(entrySet1, exitSet1, stmt)
				entrySetMap[stmt] = entrySet1!!
				exitSetMap[stmt] = exitSet1!!
			}

			blockProcessList.remove(curBlock)
		}
	}

	fun kill(entrySet: HashSet<String>?, exitSet: HashSet<String>?, stmt: soot.Unit) {
		setCopyFn(entrySet, exitSet)
		if (!isInvokedBy_Expr) {
			if (stmt is JAssignStmt) {

			} else if (stmt is JReturnStmt) {

			}
		}
	}

	fun gen(entrySet: HashSet<String>?, exitSet: HashSet<String>?, stmt: soot.Unit) {
		if (stmt is JIdentityStmt) {
			LogNow.info("JIdentityStmt： $stmt")
		} else if (stmt is JAssignStmt) {
			LogNow.info("JAssignStmt： $stmt")
		} else if (stmt is JInvokeStmt) {
			LogNow.info("JInvokeStmt： $stmt")
		} else if (stmt is JIfStmt) {
			LogNow.info("JIfStmt： $stmt")
		} else if (stmt is JTableSwitchStmt) {
			LogNow.info("JTableSwitchStmt： $stmt")
		} else if (stmt is JLookupSwitchStmt) {
			LogNow.info("JLookupSwitchStmt： $stmt")
		} else {
			LogNow.info("Gen： $stmt")
		}
	}

	fun setCopyFn(src: HashSet<String>?, dst: HashSet<String>?) {
		if (src == null || dst == null) {
			return
		}
		for (v in src) {
			if (!dst.contains(v)) {
				dst.add(v)
			}
		}
	}

	fun printOut1() {
		var stmtNum = 0
		while (stmtMap.containsKey(stmtNum)) {
//			println()
			stmtNum++
		}
	}
}

class InterTaintAnalysis(var classes: Chain<SootClass>) {

	private var clsList = arrayListOf<SootClass>()
	private var udmtdList = arrayListOf<SootMethod>()
	private var genMap = hashMapOf<SootMethod, HashSet<String>>()
	private var killMap = hashMapOf<SootMethod, HashSet<String>>()
	private var preMap = hashMapOf<SootMethod, HashSet<String>>()

	private var mtdList = arrayListOf<SootMethod>()

	private var pendingBlocks = LinkedBlockingDeque<SootMethod>()

	private var mtdIntraProcMap = hashMapOf<SootMethod, IntraTaintAnalysis>()

	fun run() {
		for (cls in classes) {
			println("fun run() cls: " + cls.name)
			clsList.add(cls)

			for (mtd in cls.methods) {

				udmtdList.add(mtd)
				genMap[mtd] = hashSetOf()
				killMap[mtd] = hashSetOf()
				preMap[mtd] = hashSetOf()

				mtdIntraProcMap[mtd] = IntraTaintAnalysis(mtd, this)
				DebugTool.exitHere("Analysis One Method: " + mtd.signature)
				mtdList.add(mtd)

				if (mtd.isMain) {
					pendingBlocks.add(mtd)
				}
			}
		}

		while (!pendingBlocks.isEmpty() || mtdList.isNotEmpty()) {
			if (pendingBlocks.isEmpty()) {
				pendingBlocks.add(mtdList.first())
			}
			val curMtd = pendingBlocks.remove()
			val intraAna = mtdIntraProcMap[curMtd]
			intraAna?.stmtAnalysis()
			mtdList.remove(curMtd)

		}

//		printOut()
	}


	fun printOut() {
		println("\n\nThe fixed signatures for each method are as follows:")
		println("========================================================\n")

		for (cls in classes) {

			for (mtd in cls.methods) {
				println(mtd.toString())

				println("preSet: " + preMap[mtd])
				println("genSet: " + preMap[mtd])
				println("killSet: " + preMap[mtd])
			}
		}

		println("========================================================")
		println("\n\n\n\nThe taint information within each method is as follows:")
		println("========================================================\n")

		for (cls in clsList) {
			for (mtd in cls.methods) {
				println(mtd.toString())
				println("--------------------------------------")
				mtdIntraProcMap[mtd]!!.printOut1()
//				println("\n\n\n")
			}
		}
		println("\n\n\n========================================================\n\n")
	}


	fun PreMapCreation(arg: String, mtd: SootMethod) {
		var preSet = preMap[mtd]
		if (!preSet!!.contains(arg)) {

		}
	}
}

class Pass1(var classes: Chain<SootClass>) {

	fun startAnalyze(testCls: List<String>) {
		val runCls = HashChain<SootClass>()
		for (c in classes) {
			if (testCls.contains(c.name)) {
				runCls.add(c)
			}
		}
		InterTaintAnalysis(runCls).run()
	}

}