package com.donat.donchess.repository;

import org.springframework.data.jpa.domain.Specification;
import com.donat.donchess.domain.User;

public interface UserSpecifications {

	static Specification<User> unfinishedRegistration() {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.isNotNull(root.get("authenticationToken"));
	}

}
