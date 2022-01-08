
实现原则：不用Soot除了Block之外的任何复杂功能。

### TODO
1. 计时，找到Soot最快配置
搞清楚配置含义： 

Options.v().set_validate(false)

Options.v().set_no_bodies_for_excluded(true)

  //        Options.v().set_force_overwrite(true)

  Options.v().set_whole_program(false)

// if the prepend flag is set, append the default classpath
set_prepend_classpath()


2. source sink ->
PASS-0: 按照 InputScope的结构线写完
PASS-1: 改进得更合理

SootPointTo:
https://github.com/soot-oss/soot/blob/master/src/main/java/soot/jimple/toolkits/pointer/LocalMustNotAliasAnalysis.java