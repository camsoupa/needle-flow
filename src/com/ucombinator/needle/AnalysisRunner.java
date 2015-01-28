package com.ucombinator.needle;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

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

	// a beginning, incomplete attempt at using Soot FlowAnalysis
	private static void performAnalysis(Iterator<SootMethod> callers) {
		while(callers.hasNext()) {
			SootMethod m = callers.next();
			Body b = m.retrieveActiveBody();
			UnitGraph g = new BriefUnitGraph(b);
			SourceSinkFlowExprs exprs = new SourceSinkFlowAnalysis(g);
			System.out.println(m.getSignature());
			Iterator methodUnits = g.iterator();
			while(methodUnits.hasNext()) {
				Unit u = (Unit) methodUnits.next();
				List<Unit> before = exprs.getSourceSinkFlowExprsAfter(u);
				List<Unit> after = exprs.getSourceSinkFlowExprsBefore(u);
				System.out.println("Unit: " + u.toString());
				System.out.println("  Before: " + before.toString());
				System.out.println("  After: " + after.toString()); 
			}
			/* TODO store the results for each method and combine them to get full app data flow */
		}
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
