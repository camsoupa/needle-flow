needle-flow
==========

## TO USE

1. Add the exported jar as an external archive to your analysis project.
2. Get the android-sdk as described below.


The following program could be run with arguments:
  ../android-sdk/platforms test/1.apk

```
package com.ucombinator.needle.main;

import java.util.Iterator;

import soot.SootClass;
import soot.SootMethod;

import com.ucombinator.needle.SootAndroidApp;

public class AnalysisRunner {
	public static void main(String[] args) {
		printRunInfo(args);
		if (args.length < 2) {
		  printUsage(args);
		}
		
		String androidJars = args[0];
		String apk = args[1];
		
		SootAndroidApp.init(androidJars, apk);

        /* get the in-app caller methods from the callgraph (excludes library callers ) */
		Iterator<SootMethod> callers = SootAndroidApp.getInternalCallers();
		while(callers.hasNext()) {
			System.out.println(callers.next());
		}
		
		for(SootClass clazz :  SootAndroidApp.getAst()) {
			System.out.println(clazz);
		}
		
		System.out.println(SootAndroidApp.getCallGraph());
	}

	private static void printRunInfo(String[] args) {
		System.out.println("ran from: " + System.getProperty("user.dir") + " with args: [" + String.join(", ", args) + "]");
	}

	private static void printUsage(String[] args) {
		String cmd = "  java -classpath ... com.ucombinator.needle.AnalysisRunner";
		System.out.println("USAGE: ");
		System.out.println(cmd + "<path/to/android/platforms> <path/to/apk>");
		System.out.println("EXAMPLE: ");
		System.out.println(cmd + " ../android-sdk/platforms ../needle/data/apps/source/AppName/AppName.apk");
		System.out.println("(In eclipse, create a run configuration with arguments for convenience)");
	}
}
```

## TO BUILD

See: https://github.com/Sable/soot/wiki/Building-Soot-with-Eclipse for instructions on how to get soot working in eclipse

## CLONE THIS REPO

## GET JAVA

- java-8-oracle is best (other versions may work, but have not tested)

## GET ANT (TO BUILD ANDROID APPS)

- Run cmd: sudo apt-get install ant (or equivalent for your OS)

## GET THE ANDROID SDK

- Go to:    http://developer.android.com/sdk/index.html
- Click:    VIEW ALL DOWNLOADS AND SIZES
- Download: In the "SDK Tools Only" table, choose the distro for your OS

## ADD ANDROID EXECUTABLES TO PATH 

- Add <android-sdk>/tools & <android-sdk>/platform-tools to your path
- source your .bashrc (or equivalent) so that you can run android from command line

## GET ANDROID PLATFORM VERSIONS AND PLATFORM TOOLS USING SDK MANAGER

- Run cmd: android sdk (This will start the Android SDK Manager GUI.)
- Select:  the tools and platforms to download (All tools and platforms 15 & 19 should do.
- Click:   Install # packages

## GET ECLIPSE FOR JAVA (FOR SOOT DEVELOPEMENT)

- Go to: https://eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1
- Download: On the right under "Download Links" choose the distro for your OS

## GET SOOT

- Go to: https://github.com/Sable/soot/wiki/Building-Soot-with-Eclipse
- Follow step 2+ to get soot+dependencies the fast way

## IMPORT needle-flow ECLIPSE PROJECT INTO YOUR ECLIPSE WORKSPACE

- In eclipse, choose File -> Import -> General -> Existing Project Into Workspace
- Browse to and select needle-flow directory and select OK.
- You should then see a needle-flow project in the "Projects" list (it should be checked).
- Ensure "Copy into Workspace" below the Projects list is unchecked.

## CHECK FOLDER STRUCTURE

Target folder structure:
```
android-sdk/
  tools/ 
	platforms/
    android-15/
    android-19/
    ...
  platform-tools/
  ...

needle-flow/ (this java project will produce a .jar file to be invoked from needle/server.js)
  ...
  
needle/
  server.js.
  ...
```

## CHECK ECLIPSE WORKSPACE

Your workspace should include these projects:
```
needle-flow/
heros/
jasmin/
soot/
```

## Run needle-flow from eclipse

- Create a run configuration by clickong on the small down arrow to the right green play arrow.
- Select "Run Configrations..." 
- Create a run configuration with arguments for: ```<path/to/android-platforms> <path/to/apk>```

## Hacking on the code

The Soot Survivor's guide may be helpful: http://www.brics.dk/SootGuide/sootsurvivorsguide.pdf

- Here are some nice examples of flow analyses that may be useful: https://github.com/luminousfennell/soot-miniworkshop

## Soot API overview:

```
A Unit is a base class for a Stmt.
A Value is a base class for a Local/Constant/Expr.
An Expr is a base class for the 15 jimple expressions (BinopExpr, InvokeExpr, etc.)
A Box is a base class for a UnitBox/ValueBox.
```
From a Unit, you can get Values it defines (left-hand side of =) and Values it uses (right-hand side of =).
```
unit.getUseBoxes();
unit.getDefBoxes();
```
A SootMethod and a SootClass are representations of code methods and classes.  

From a method, you can get the body statements and build a graph of the statement relationships:
```
Body b = m.retrieveActiveBody();
UnitGraph g = new BriefUnitGraph(b);
```
A FlowAnalysis (BackwardFlowAnalysis or ForwardFlowAnalysis) can accept a UnitGraph (DirectedGraph) from which to perform a flow analysis.

A FlowAnalysis propogates flow in the flowsThrough method from Stmt to Stmt.

We will need to perform a flow analysis per method, then combine the results somehow for the whole program.

Soot runs things called packs.  Don't worry about these for now.  

## The current code
- For now, the code just gets the callers in the call graph and performs a SourceSinkFlowAnalysis on the method body.  

- The analysis is just placeholder and really just checks for stmts in branches (See ```SourceSinkFlowAnalysis.generateFlow```), but it is an attempt to discover how the flow is propogated from the in and out sets (see ```SourceSinkFlowAnalysis.flowsThrough```).  I am not exactly sure how it works yet, but stmts can either create flow or end it.  For instance, a backward analysis from sinks to sources could create flow if the stmt is a sink.  If we see a sink entry in the After (or Before?) set of a stmt that we tag as a source, then we have found an explicit flow.

## Target output format of source sink flows

(source-sink-flows
   [(file line instr source-category) (file line instr) ... (file line instr sink-category)]
   ...)

