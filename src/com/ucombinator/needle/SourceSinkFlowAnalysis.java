package com.ucombinator.needle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import soot.Transformer;
import soot.Unit;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

public class SourceSinkFlowAnalysis extends BackwardFlowAnalysis<Unit, Set<Unit>> implements SourceSinkFlowExprs {

	public SourceSinkFlowAnalysis(DirectedGraph<Unit> graph) {
		super(graph);
		doAnalysis();
	}

	@Override
	protected void flowThrough(Set<Unit> in, Unit unit, Set<Unit> out) {
		System.out.println("    flowThru: " + unit.toString());
		System.out.println("      USES: " + unit.getUseBoxes());
		System.out.println("      DEFS: " + unit.getDefBoxes());

		/* passes flow thru */
		out.clear();
		out.addAll(in);
		
		endFlow(in, unit, out);
		generateFlow(out, unit);	
	}

	private void generateFlow(Set<Unit> out, Unit unit) {
		if (unit.branches()) {
			out.add(unit);
		}
	}

	private void endFlow(Set<Unit> in, Unit unit, Set<Unit> out) {
		
	}

	@Override
	protected Set<Unit> newInitialFlow() {
		return new HashSet<Unit>();
	}

	@Override
	protected Set<Unit> entryInitialFlow() {
		return new HashSet<Unit>();
	}

	@Override
	protected void merge(Set<Unit> in1, Set<Unit> in2, Set<Unit> out) {
		out.clear();
		out.addAll(Sets.intersection(in1, in2));
	}

	@Override
	protected void copy(Set<Unit> source, Set<Unit> dest) {
		dest.clear();
		dest.addAll(source);
	}

	@Override
	public List<Unit> getSourceSinkFlowExprsBefore(Unit unit) {
		List<Unit> beforeList = new ArrayList<Unit>();
		Set<Unit> beforeSet = super.getFlowBefore(unit);
		if(beforeSet != null) beforeList.addAll(beforeSet);
		return beforeList;
	}

	@Override
	public List<Unit> getSourceSinkFlowExprsAfter(Unit unit) {
		List<Unit> afterList = new ArrayList<Unit>();
		Set<Unit> afterSet = super.getFlowAfter(unit);
		if(afterSet != null) afterList.addAll(afterSet);
		return afterList;
	}

}
