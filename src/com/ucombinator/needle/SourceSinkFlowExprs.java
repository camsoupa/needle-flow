package com.ucombinator.needle;

import java.util.List;

import soot.Unit;

public interface SourceSinkFlowExprs {
	public List getSourceSinkFlowExprsBefore(Unit s);
	public List getSourceSinkFlowExprsAfter(Unit s);
}
