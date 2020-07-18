package com.donat.donchess.repository;

import org.springframework.data.jpa.domain.Specification;
import com.donat.donchess.domain.Challenge;

public interface ChallengeSpecifications {

	static Specification<Challenge> challenger(Long id) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("challenger").get("id"), id);
	}

	static Specification<Challenge> challenged(Long id) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("challenged").get("id"), id);
	}

}
