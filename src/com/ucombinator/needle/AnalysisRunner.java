package com.ucombinator.needle;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import soot.Body;
import soot.Main;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Sources;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class AnalysisRunner {
	public static void main(String[] args) {
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		//PackManager.v().getPack("wjtp").add(new Transform("SourceSinkFlowAnalysis",
		//	SourceSinkFlowAnalysis.instance()));
		System.out.println(args);
		//String apk = args[1];
		String androidJars = "/home/cam/dev/android-platforms";
		String apk = "Memotis.apk";
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_process_dir(Collections.singletonList(apk));
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_android_jars(androidJars);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_whole_program(true);
		Options.v().set_allow_phantom_refs(true);
		String classPath = /*"/usr/lib/jvm/java-8-oracle/jre/lib/rt.jar" +
						":/usr/lib/jvm/java-8-oracle/jre/lib/jce.jar:" + */
						Scene.v().getAndroidJarPath(androidJars, apk);
		Options.v().set_soot_classpath(classPath);
		Main.v().autoSetOptions();

		Scene.v().loadNecessaryClasses();	
		
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg.spark", "verbose:true");
        Options.v().setPhaseOption("cg", "all-reachable:true");

		Transform transform = new Transform("wjtp.Callgraph", new SceneTransformer() {
			@Override
			protected void internalTransform(String phaseName, Map options) {
				System.out.println("INTERNAL_TRANSFORM");
				getCallers();
			}
		});
		//PackManager.v().getPack("wjtp").add(transform);
		PackManager.v().getPack("cg").apply();
		//PackManager.v().getPack("wjtp").apply();

		Iterator<SootMethod> callers = getCallers();
		
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
		}
		/*
		// Retrieve the method and its body

		// Build the CFG and run the analysis

		// Iterate over the results
		Iterator i = g.iterator();
		while (i.hasNext()) {
			Unit u = (Unit)i.next();
			List IN = exprs.getSourceSinkFlowExprsBefore(u);
			List OUT = exprs.getSourceSinkFlowExprsAfter(u);
			// Do something clever with the results
		}*/

	}
	
	public static Iterator<SootMethod> getCallers() {
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> sources = cg.listener();
		Iterator<Edge> appMethods = Iterators.filter(sources, new Predicate<Edge>() {
			public boolean apply(Edge e) {
				return !e.src().isJavaLibraryMethod() && !e.src().method().getDeclaringClass().isLibraryClass();
			}
		});
		
		Iterator<SootMethod> callers = new Sources(appMethods);
		return callers;
		
	}
}
