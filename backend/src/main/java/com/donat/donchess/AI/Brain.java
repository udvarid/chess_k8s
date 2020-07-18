package com.donat.donchess.AI;

import java.util.ArrayList;
import java.util.List;

public class Brain {

	private List<Evaluator>  evaluators = new ArrayList<>();

	public List<Evaluator> getEvaluators() {
		return evaluators;
	}

	public Brain addEvaluator(Evaluator evaluator) {
		this.evaluators.add(evaluator);
		return this;
	}
}
