package com.donat.donchess.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {

	@Autowired
	private UserService userService;

	@Autowired
	private BotService botService;

	@Autowired
	private ChallengeService challengeService;

	//2 naponta törli a 2 napnál régebbi regisztrációkat
	@Scheduled(fixedDelay = 60000 * 60 * 24 * 2)
	private void deleteOldRegistrations() {
		userService.deleteOldRegistration();
	}

	@Scheduled(fixedDelay = 5000)
	private void botTurn() {
		challengeService.acceptAllChallengeForBots();
		botService.makeMove();
	}


}
