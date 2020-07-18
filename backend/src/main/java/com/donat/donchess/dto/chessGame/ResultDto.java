package com.donat.donchess.dto.chessGame;

import com.donat.donchess.domain.enums.Result;

public class ResultDto {

	private Result result;

	private String userOne;

	private String UserTwo;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getUserOne() {
		return userOne;
	}

	public void setUserOne(String userOne) {
		this.userOne = userOne;
	}

	public String getUserTwo() {
		return UserTwo;
	}

	public void setUserTwo(String userTwo) {
		UserTwo = userTwo;
	}
}
