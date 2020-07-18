insert into ROLES (id, role) values (1, 'ROLE_USER');
insert into ROLES (id, role) values (2, 'ROLE_ADMIN');
insert into ROLES (id, role) values (3, 'ROLE_BOT0');
insert into ROLES (id, role) values (4, 'ROLE_BOT1');

insert into USERS (id, email, password, fullname, enabled) values (1, 'udvarid@hotmail.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát - 1', true);
insert into USERS (id, email, password, fullname, enabled) values (2, 'udvari.donat@gmail.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát', true);
insert into USERS (id, email, password, fullname, enabled) values (3, 'bot@bot.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Stupid Bot', true);
insert into USERS (id, email, password, fullname, enabled) values (4, 'bot2@bot.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Plain Bot', true);
insert into USERS_ROLES (user_id, role_id) values (1, 2);
insert into USERS_ROLES (user_id, role_id) values (2, 1);
insert into USERS_ROLES (user_id, role_id) values (3, 3);
insert into USERS_ROLES (user_id, role_id) values (4, 4);

insert into CHESS_GAMES (id, chess_game_status, chess_game_type, last_move_id, next_move, result,
                         user_one_id, user_two_id) values(1, 'OPEN', 'NORMAL', 6, 'WHITE', 'OPEN', 1, 2);

insert into CHESS_MOVES (id, move_id, move_fromx, move_fromy, move_tox, move_toy,
                         promote_type, special_move_type, chess_game_id) values (1, 1, 5, 2, 5, 4, null, 'NORMAL',1);
insert into CHESS_GAMES_CHESS_MOVES (chess_game_id, chess_moves_id) values (1, 1);

insert into CHESS_MOVES (id, move_id, move_fromx, move_fromy, move_tox, move_toy,
                         promote_type, special_move_type, chess_game_id) values (2, 2, 5, 7, 5, 5, null, 'NORMAL',1);
insert into CHESS_GAMES_CHESS_MOVES (chess_game_id, chess_moves_id) values (1, 2);

insert into CHESS_MOVES (id, move_id, move_fromx, move_fromy, move_tox, move_toy,
                         promote_type, special_move_type, chess_game_id) values (3, 3, 4, 2, 4, 3, null, 'NORMAL',1);
insert into CHESS_GAMES_CHESS_MOVES (chess_game_id, chess_moves_id) values (1, 3);

insert into CHESS_MOVES (id, move_id, move_fromx, move_fromy, move_tox, move_toy,
                         promote_type, special_move_type, chess_game_id) values (4, 4, 2, 8, 3, 6, null, 'NORMAL',1);
insert into CHESS_GAMES_CHESS_MOVES (chess_game_id, chess_moves_id) values (1, 4);

insert into CHESS_MOVES (id, move_id, move_fromx, move_fromy, move_tox, move_toy,
                         promote_type, special_move_type, chess_game_id) values (5, 5, 3, 1, 7, 5, null, 'NORMAL',1);
insert into CHESS_GAMES_CHESS_MOVES (chess_game_id, chess_moves_id) values (1, 5);

insert into CHESS_MOVES (id, move_id, move_fromx, move_fromy, move_tox, move_toy,
                         promote_type, special_move_type, chess_game_id) values (6, 6, 8, 7, 8, 6, null, 'NORMAL',1);
insert into CHESS_GAMES_CHESS_MOVES (chess_game_id, chess_moves_id) values (1, 6);


