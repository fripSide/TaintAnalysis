package soottaint.taintanalysis.base

import polyglot.ast.Call
import soot.*
import soot.Unit
import soot.jimple.*
import soot.jimple.internal.*
import soot.toolkits.graph.Block
import soot.toolkits.graph.CompleteBlockGraph
import soottaint.utils.LogNow
import soottaint.utils.SootTool
import java.util.*
import kotlin.collections.LinkedHashSet


/*
算法描述：
每个IntraContext保存一个函数的状态，
先forward扫描,遇到tag变量记录一下

 */

class SourcePoint(var mtd: SootMethod, var block: Block, var inst: Unit, var value: Value) {

}

class SinkMethod(var mtd: SootMethod) {

}

class StmtItem(mtd: SootMethod, block: Block, unit: Unit) {
	var methodLocation: SootMethod = mtd
	var blockLocation: Block = block
	var instructionLocation = unit
	var containSink = false
}

class CallStackItem(mtd: SootMethod, block: Block, unit: Unit, retVal: Value) {
}

class IntraContext(mtd: SootMethod, block: Block, unit: Unit): AbstractStmtSwitch<Unit>() {

	var methodLocation: SootMethod = mtd
	var blockLocation: Block = block
	var instructionLocation = unit

	var isTerminate = false
	var containSink = false

	val blockTrace = arrayListOf<Block>()
	val callStack = arrayListOf<CallStackItem>()
	val instructionTrace = arrayListOf<StmtItem>()

	val interestedValue = hashSetOf<Value>()

	init {

	}

	constructor(ctx: IntraContext): this(ctx.methodLocation, ctx.blockLocation, ctx.instructionLocation) {

	}

	fun process() {
		val currentInst = instructionLocation
		val nextInst = blockLocation.getSuccOf(currentInst)
		if (nextInst == null) {
			val blocks = blockLocation.succs
		} else {
			println(currentInst)
		}
	}

	fun hasProcessed(block: Block): Boolean {
		blockTrace.forEach{ b ->
			if (b == block) return true
		}
		return false
	}

	fun setProcessedBlock(block: Block) {
		blockLocation = block
		blockTrace.add(block)
	}

	fun setContainSink() {
		containSink = true
	}

	override fun hashCode(): Int {
		return instructionLocation.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		if (other is IntraContext) {
			return this.instructionLocation == other.instructionLocation
		}
		return super.equals(other)
	}

}

class InterContextSolver(var qes: TaintQuestion): AbstractStmtSwitch<Unit>() {

//	var instructionTrace =

//	constructor(ctx: InterContextSolver): this(ctx.methodLocation, ctx.blockLocation, ctx.instructionLocation) {
//
//	}

	var preSet = hashSetOf<IntraContext>()
	var genSet = hashSetOf<IntraContext>()
	var killSet = hashSetOf<IntraContext>()

	var currentContext: IntraContext? = null

	fun process(ctx: IntraContext): List<IntraContext> {
		val curInst = ctx.instructionLocation
		val nextInst = ctx.blockLocation.getSuccOf(curInst)
		if (nextInst != null) {
			return oneStepForward(ctx, nextInst)
		} else {
//			println(ctx.blockLocation.toShortString())
			val blocks = getBlocksInMtd(ctx)
			if (blocks.isNotEmpty()) {
				return processBlock(ctx, blocks)
			} else {
				ctx.isTerminate = true
			}
		}
		return listOf()
	}

	fun getBlocksInMtd(ctx: IntraContext): List<Block> {
		val processBlocks = arrayListOf<Block>()
		for (block in ctx.blockLocation.succs) {
			if (!ctx.hasProcessed(block)) {
				processBlocks.add(block)
			}
		}
		return processBlocks
	}

	private fun oneStepForward(ctx: IntraContext, curInst: Unit): List<IntraContext> {
		val ctxList = arrayListOf<IntraContext>()
//		LogNow.info("ProcessInst: $curInst")
		LogNow.toFile(curInst.toString())
		curInst.useAndDefBoxes.forEach{ box ->
//			println(box)
//			println(box.javaClass)
		}

		currentContext = ctx
		curInst.apply(this)

		ctx.instructionLocation = curInst
		return ctxList
	}

	private fun processBlock(ctx: IntraContext, blocks: List<Block>): List<IntraContext>  {
		val ctxList = arrayListOf<IntraContext>()
		for (block in blocks) {
			println(block.toShortString())
			val intraContext = IntraContext(ctx)
			intraContext.setProcessedBlock(block)
//			println(block)
			ctxList.add(intraContext)
			ctxList.addAll(oneStepForward(intraContext, intraContext.blockLocation.head))
		}
		return ctxList
	}

	private fun handleInvokeStmt(assiTo: Value, invokeExpr: InvokeExpr) {
		if (qes.isSink(invokeExpr.method)) {
//			currentContext.
		}
	}


	private fun gen() {

	}


	override fun caseAssignStmt(stmt: AssignStmt?) {
		stmt ?: return defaultCase(stmt)

		val leftOp = stmt.leftOp

		var isTaint = false
		if (stmt.rightOp is InvokeExpr) {

		}
		// assign to left
		if (currentContext!!.interestedValue.contains(stmt.rightOp)) {
			isTaint = true
		}
		if (isTaint) {

		} else {
			currentContext!!.interestedValue.remove(leftOp)
		}
		println("Assign: $stmt")
	}

//	override fun caseInvokeStmt(stmt: InvokeStmt?) {
//
//	}

	fun jump2caller() {

	}

	override fun defaultCase(obj: Any?) {
		println("DefaultCase: $obj")
	}

}

class TaintQuestion(var source: SourcePoint) {
	private val sinks =  arrayListOf<SinkMethod>()

	val interContext = InterContextSolver(this)

	val intraContextSet = hashSetOf<IntraContext>()

	init {
		intraContextSet.add(IntraContext(source.mtd, source.block, source.inst))
	}

	fun addSink(mtd: SootMethod) {
		sinks.add(SinkMethod(mtd))
	}

	fun process() {
	}

	fun isSink(mtd: SootMethod): Boolean {
		for (sink in sinks) {
			if (sink.mtd == mtd) {
				return true
			}
		}
		return false
	}
}

/*
1. 方案1：Inter、Intra自己解析
2. 方案2：用SimulateEngine解析Inter，Intra只保存不解析
 */
class TaintSolver() {

	/*
	算法：
	1. 广度优先遍历
	2. 每次处理一个step
	 */
	fun solve(qes: TaintQuestion) {
		val workList = LinkedList<IntraContext>()
		workList.add(qes.intraContextSet.first())
		while (workList.isNotEmpty()) {
			val startCtx = workList.poll()
			// process one instruction
			val devised = qes.interContext.process(startCtx)
//			LogNow.info("New Blocks: " + devised.size)
			if (!startCtx.isTerminate) {
				workList.add(startCtx)
			}
			for (nCtx in devised) {
				workList.add(nCtx)
			}
		}
	}
}


class SimulateEngine: AbstractStmtSwitch<Unit>() {


	override fun caseBreakpointStmt(stmt: BreakpointStmt?) {
		super.caseBreakpointStmt(stmt)
	}

	override fun defaultCase(obj: Any?) {
		println("unsupported stmt: $obj")
	}
}

object BlockCache {
	private val blockMap = hashMapOf<Body, CompleteBlockGraph>()

	fun getBlockGraph(body: Body): CompleteBlockGraph {
		if (!blockMap.containsKey(body)) {
			blockMap[body] = CompleteBlockGraph(body)
		}
		return blockMap[body]!!
	}
}


class Pass0() {

	val kSourceMtd = "void main(java.lang.String[])"
	val kSourceTag = "@parameter0: java.lang.String[]"
	val kSourceVarTag = ""
	val sinkFunc = listOf("<java.io.PrintStream: void println(java.lang.String)>")

	val taintQuestions = arrayListOf<TaintQuestion>()

	fun startAnalyze() {
		setupTaintQuestions()
		solveTaintQuestion()
	}

	private fun addTaintQuestion(source: SourcePoint) {
		val taintQuestion = TaintQuestion(source)
		for (func in sinkFunc) {
			val mtd = Scene.v().getMethod(func)
			taintQuestion.addSink(mtd)
		}
		taintQuestions.add(taintQuestion)
	}

	private fun setupTaintQuestions() {
		val clsList = SootTool.filterClasses("InterTest")
		assert(clsList.size == 1)
		val cls = clsList.first()
		for (mtd in cls.methods) {
			if (mtd.subSignature == kSourceMtd) {
				assert(mtd.isMain)
				val body = mtd.retrieveActiveBody() ?: continue
				val graph = BlockCache.getBlockGraph(body)
				graph.blocks.forEach{ block ->
					for (u in block) {
						if (u is JIdentityStmt) {
							if (u.rightOp.toString() == kSourceTag) {
								println(u.leftOp)
								val point = SourcePoint(mtd, block, u, u.leftOp)
								addTaintQuestion(point)
							}
						}
					}
				}
			}
		}
	}

	private fun solveTaintQuestion() {
		val solver = TaintSolver()
		for (qes in taintQuestions) {
			solver.solve(qes)
		}
	}

}
