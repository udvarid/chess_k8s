package com.donat.donchess.service;

import com.donat.donchess.domain.Challenge;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.QUser;
import com.donat.donchess.domain.Role;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.User.RegisterDto;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.dto.User.UserLoginDto;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.repository.ChallengeRepository;
import com.donat.donchess.repository.ChallengeSpecifications;
import com.donat.donchess.repository.ChessGameRepository;
import com.donat.donchess.repository.ChessGameSpecifications;
import com.donat.donchess.repository.RoleRepository;
import com.donat.donchess.repository.UserRepository;
import com.donat.donchess.repository.UserSpecifications;
import com.donat.donchess.security.UserDetailsImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private EmailService emailService;
	private RoleRepository roleRepository;
	private final SecurityService securityService;
	private final ChessGameRepository chessGameRepository;
	private final ChallengeRepository challengeRepository;
	private final AuthenticationProvider authenticationProvider;
	private Provider<EntityManager> entityManager;


	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
		EmailService emailService, RoleRepository roleRepository,
		SecurityService securityService, ChessGameRepository chessGameRepository,
		ChallengeRepository challengeRepository, AuthenticationProvider authenticationProvider,
		Provider<EntityManager> entityManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.roleRepository = roleRepository;
		this.securityService = securityService;
		this.chessGameRepository = chessGameRepository;
		this.challengeRepository = challengeRepository;
		this.authenticationProvider = authenticationProvider;
		this.entityManager = entityManager;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		User user = findByEmail(email);
		if (user == null || !user.isEnabled()) {
			throw new UsernameNotFoundException(email);
		}
		return new UserDetailsImpl(user);
	}


	public User findByEmail(String email) {
		JPAQueryFactory query = new JPAQueryFactory(entityManager);
		QUser userFromQ = QUser.user;

		return query.selectFrom(userFromQ)
			.where(userFromQ.email.eq(email))
			.fetchOne();
	}

	public void registerUser(RegisterDto registerDto) {
		if (findByEmail(registerDto.getEmail()) != null) {
			throw new InvalidException("Already registered user!");
		}
		if (registerDto.getPassword().isEmpty()) {
			throw new InvalidException("Not valid password!");
		}
		if (registerDto.getFullName().isEmpty()) {
			throw new InvalidException("Not filled full name!");
		}

		User newUser = new User();
		newUser.setEmail(registerDto.getEmail());
		newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		newUser.setFullname(registerDto.getFullName());

		Role role = roleRepository.findByRole("ROLE_USER");
		newUser.getRoles().add(role);
		newUser.setEnabled(false);
		newUser.setAuthenticationToken(RandomString.make(20));
		newUser.setTimeOfRegistration(LocalDateTime.now());

		emailService.sendAuthenticatonMail(newUser);

		userRepository.saveAndFlush(newUser);

	}

	public void confirmUserByToken(String token) {
		if (token.isEmpty()) {
			throw new InvalidException("Invalid token");
		}
		User user = userRepository.findByAuthenticationToken(token).orElseThrow(() -> new InvalidException("Invalid token"));

		user.setAuthenticationToken(null);
		user.setEnabled(true);

		userRepository.saveAndFlush(user);
	}

	//TODO rendszeresen tisztítani az aktiválatlan regisztrációkat - ehhez kell a regisztráció ideje is

	public Set<UserDto> prepareList() {
		JPAQueryFactory query = new JPAQueryFactory(entityManager);
		QUser userFromQ = QUser.user;

		List<User> users = query.selectFrom(userFromQ)
			.where(userFromQ.enabled.eq(true))
			.orderBy(userFromQ.fullname.asc())
			.fetch();

		Set<UserDto> userDtos = new HashSet<>();

		users.forEach(user -> {
			UserDto userDto = new UserDto();
			userDto.setFullName(user.getFullname());
			userDto.setId(user.getId());
			userDto.setRole(user.getRoles().toString());
			userDtos.add(userDto);
		});

		return userDtos;
	}

	public void login(UserLoginDto userLoginDto, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());
		authRequest.setDetails(new WebAuthenticationDetails(request));
		Authentication authentication = this.authenticationProvider.authenticate(authRequest);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}


	public UserDto getOneUser(String email) {
		User user = findByEmail(email);
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setFullName(user.getFullname());
		userDto.setRole(user.getRoles().toString());
		return userDto;
	}

	public Set<UserDto> prepareListOfFreeUsers() {
		JPAQueryFactory query = new JPAQueryFactory(entityManager);
		QUser userFromQ = QUser.user;

		User listRequester = securityService.getChallenger();
		if (listRequester == null) {
			throw new InvalidException("Not logged in");
		}
		Long id = listRequester.getId();

		List<User> users = query.selectFrom(userFromQ)
			.where(userFromQ.enabled.eq(true).and(userFromQ.id.ne(id)))
			.orderBy(userFromQ.fullname.asc())
			.fetch();

		Set<UserDto> userDtos = new HashSet<>();

		users
			.stream()
			.filter(user -> {
				List<Challenge> challenges = challengeRepository
					.findAll(ChallengeSpecifications.challenger(id).and(ChallengeSpecifications.challenged(user.getId()))
						.or(ChallengeSpecifications.challenged(id).and(ChallengeSpecifications.challenger(user.getId()))));

				List<ChessGame> games = chessGameRepository.findAll(ChessGameSpecifications.openGame())
					.stream()
					.filter(game -> game.getUserOne().getId().equals(id) && game.getUserTwo().getId().equals(user.getId())
						         || game.getUserTwo().getId().equals(id) && game.getUserOne().getId().equals(user.getId()))
					.collect(Collectors.toList());

				return challenges.isEmpty() && games.isEmpty();
			})
			.forEach(user -> {
				UserDto userDto = new UserDto();
				userDto.setFullName(user.getFullname());
				userDto.setId(user.getId());
				userDto.setRole(user.getRoles().toString());
				userDtos.add(userDto);
			});

		return userDtos;

	}

	public void deleteOldRegistration() {
		List<User> users = userRepository.findAll(UserSpecifications.unfinishedRegistration());
		Iterator<User> iterator = users.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (ChronoUnit.HOURS.between(user.getTimeOfRegistration(), LocalDateTime.now()) > 48) {
				userRepository.delete(user);
			}
		}
	}
}
