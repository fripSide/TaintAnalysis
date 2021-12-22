package soottaint

import soot.*
import soot.options.Options
import soot.util.Chain
import soottaint.taintanalysis.base.Pass0
import soottaint.taintanalysis.base.Pass1
import soottaint.utils.LogNow
import soottaint.utils.SootTool
import java.io.File
import java.util.*


fun setSootPkt() {
	PackManager.v().getPack("wjtp").add(Transform("wjtp.myTransform", object : SceneTransformer() {
		override fun internalTransform(phaseName: String?, options: MutableMap<String, String>?) {
			// TODO Auto-generated method stub
			val classes: Chain<SootClass> = Scene.v().applicationClasses
			LogNow.info("Application classes analyzed: $classes")
			//you should implement the inter-procedural taint analysis as class InterTaintAnalysis
//			Pass1(classes).startAnalyze()
		}
	}))
}

fun runDemo0() {
	Pass0().startAnalyze()
}

fun runDemo1() {
	val test = listOf<String>("HelloWorld", "InterTest")
	val classes: Chain<SootClass> = Scene.v().applicationClasses
	Pass1(classes).startAnalyze(test)
}

fun runDemo2() {
	val test = listOf<String>("HelloWorld", "IntraTest")
	val classes: Chain<SootClass> = Scene.v().applicationClasses
	Pass1(classes).startAnalyze(test)
}

fun runDemo3() {
	var test = listOf<String>("Test1")
	val classes: Chain<SootClass> = Scene.v().applicationClasses
	Pass1(classes).startAnalyze(test)
	test = listOf<String>("Test2")
	Pass1(classes).startAnalyze(test)
	test = listOf<String>("Test3")
	Pass1(classes).startAnalyze(test)
}

fun sootInit() {
	var fp = "bin/test"
	fp = "bin/cls"
	var cp = "bin/rt.jar;bin/jce.jar"
	cp = "bin"
	val ajar = "bin/android.jar"

//	Options.v().set_src_prec(Options.src_prec_apk);

	Options.v().set_soot_classpath(cp)
//	Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK")
	Options.v().set_process_dir(Collections.singletonList(fp));
//	Options.v().set_src_prec(Options.src_prec_java)
	Options.v().set_src_prec(Options.src_prec_class)
//	Options.v().set_process_multiple_dex(true);
//	Options.v().set_android_api_version(24);
//	Options.v().set_output_format(Options.output_format_none);
	Options.v().set_output_format(Options.output_format_jimple)
	Options.v().set_force_overwrite(true);
	Options.v().set_allow_phantom_refs(true);
	Options.v().set_whole_program(true);
	Options.v().set_prepend_classpath(true);
	Options.v().ignore_resolution_errors();

	Scene.v().loadNecessaryClasses()


//	setSootPkt()
//	PackManager.v().runPacks()
//	PackManager.v().writeOutput()

//	val classes: Chain<SootClass> = Scene.v().applicationClasses
//	LogNow.info("Application classes analyzed: $classes")
//	//you should implement the inter-procedural taint analysis as class InterTaintAnalysis
//	Pass1(classes).startAnalyze()
//	runDemo1()
	runDemo0()
//	SootTool.printSootClasses()
}

fun runTest() {
	val fp = "bin/cls"
	val cp =  "bin/"
	SootTool.initMySoot(fp)
	runDemo0()
}

open class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            sootInit()
			runTest()
        }
    }
}