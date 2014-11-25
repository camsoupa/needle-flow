package com.ucombinator.needle;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

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
		System.out.println("ran from: " + System.getProperty("user.dir") + " with args: [" + String.join(", ", args) + "]");
		
		if (args.length < 2) {
			String cmd = "  java -classpath ... com.ucombinator.needle.AnalysisRunner";
			System.out.println("USAGE: ");
			System.out.println(cmd + "<path/to/android/platforms> <path/to/apk>");
			System.out.println("EXAMPLE: ");
			System.out.println(cmd + " ../android-sdk/platforms/ ../needle/data/apps/source/AppName/AppName.apk");
			System.out.println("(In eclipse, create a run configuration with arguments for convenience)");
			return;
		}
		
		String androidJars = args[0];
		String apk = args[1];
		Options.v().set_verbose(true);
		/* Soot options */
		Options.v().set_no_bodies_for_excluded(true);
		/* Set the apk to process */
		Options.v().set_process_dir(Collections.singletonList(apk));
		/* The source bytecode is an android apk */
		Options.v().set_src_prec(Options.src_prec_apk);
		/* we need to link instructions to source line for display */
		Options.v().set_keep_line_number(true);
		/* The android libs to compile against (it will choose the proper one from the AndroidManifest.xml */
		Options.v().set_android_jars(androidJars);
		/* We are not outputting a code transformation */
		Options.v().set_output_format(Options.output_format_none);
		/* For callgraph, we need whole program */
		Options.v().set_whole_program(true);
		/* Called methods without jar files or source are considered phantom */
		Options.v().set_allow_phantom_refs(true);
		String classPath = Scene.v().getAndroidJarPath(androidJars, apk);
		Options.v().set_soot_classpath(classPath);
		/* Apply these options */
		Main.v().autoSetOptions();
        /* This will parse the apk file into soot classes */
		Scene.v().loadNecessaryClasses();	
		
		/* The callgraph phase options */
        /* assume all methods are reachable to avoid setting explicit entry points */
        Options.v().setPhaseOption("cg", "all-reachable:true");
        
        /* run the callgraph builder */
        PackManager.v().getPack("cg").apply();

        /* get the in-app caller methods from the callgraph (excludes library callers ) */
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
			/* TODO store the results for each method and combine them to get full app data flow */
		}
	}
	
	public static Iterator<SootMethod> getCallers() {
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> sources = cg.listener();
		Iterator<Edge> appMethods = Iterators.filter(sources, new Predicate<Edge>() {
			public boolean apply(Edge e) {
				System.out.println(e.src());
				return !e.src().isJavaLibraryMethod() && !e.src().method().getDeclaringClass().isLibraryClass();
			}
		});
		
		Iterator<SootMethod> callers = new Sources(appMethods);
		return Sets.newHashSet(callers).iterator();
	}
}
