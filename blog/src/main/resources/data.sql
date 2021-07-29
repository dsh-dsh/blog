insert into global_settings
(code, name, value)
values
("MULTIUSER_MODE", "Многопользовательский режим", "NO"),
("POST_PREMODERATION", "Премодерация постов", "YES"),
("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "YES");

insert into users (name, email, password, is_moderator, reg_time)
values ('moderator', 'mod@email.com', '123', 1, NOW());
insert into users (name, email, password, is_moderator, reg_time)
values ('user', 'user@email.com', '222', 0, NOW());
insert into users (name, email, password, is_moderator, reg_time)
values ('user2', 'user2@email.com', '333', 0, NOW());

insert into posts
(is_active, moderation_status, text, time, title, view_count, user_id, moderator_id)
values
(1, 'NEW', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-20 07:55:45', "111111111111111111111", 1, 2, null),
(1, 'ACCEPTED', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-21 07:55:45', "22222222222222222", 1, 3, 1),
(1, 'ACCEPTED', 'как можно написать регулярное выражение для нахождения открывающих и закрывающих html-тегов. Мои косячат, а причину понять не могу', '2021-07-15 16:55:00', "3333333333333333", 1, 2, 1),
(1, 'ACCEPTED', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-12 07:55:45', "44444444444444", 1, 3, 1),
(1, 'ACCEPTED', 'We can modify the state of the database also with a native query. We just need to add the @Modifying annotation:', '2021-07-21 12:50:00', "55555555555555555", 1, 2, 1),
(1, 'ACCEPTED', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-19 13:23:15', "666666666666666666666", 1, 1, 1),
(1, 'ACCEPTED', 'Often, well encounter the need for building SQL statements based on conditions or data sets whose values are only known at runtime. And in those cases, we cant just use a static query.', '2021-07-14 19:27:00', "7777777777777", 1, 2, 1),
(1, 'ACCEPTED', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-22 07:55:45', "8888888888888888", 1, 3, 1),
(1, 'ACCEPTED', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-11 11:43:15', "999999999999999", 1, 1, 1),
(1, 'NEW', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-10 01:15:55', "10 10 10", 1, 2, null),
(1, 'NEW', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-07 01:15:55', "11 11 11 11", 1, 3, null),
(1, 'NEW', '111111111111111111 11111111111 1 1 1111111 1111 1111111111 111 11111 1111 11 1 1 11 111 1 1 1 1111111', '2021-07-05 01:15:55', "12 12 12 12 12", 1, 1, null);

insert into post_votes
(user_id, post_id, time, value)
values
(1, 2, '2021-07-23 18:15:55', 1),
(2, 2, '2021-07-23 18:15:55', 1),
(3, 2, '2021-07-23 18:15:55', 1),
(1, 5, '2021-07-23 18:15:55', 1),
(2, 6, '2021-07-23 18:15:55', 1),
(2, 5, '2021-07-23 18:15:55', 1),
(1, 7, '2021-07-23 18:15:55', 1),
(1, 8, '2021-07-23 18:15:55', 1),
(2, 3, '2021-07-23 18:15:55', 1),
(1, 4, '2021-07-23 18:15:55', 1),
(2, 4, '2021-07-23 18:15:55', 1),
(3, 5, '2021-07-23 18:15:55', 1),
(1, 3, '2021-07-23 18:15:55', 1);

insert into post_comments
(user_id, post_id, time, text)
values
(1, 4, '2021-07-23 18:15:55', "комментарий1"),
(1, 4, '2021-07-23 18:15:55', "комментарий2"),
(1, 4, '2021-07-23 18:15:55', "комментарий3"),
(1, 4, '2021-07-23 18:15:55', "комментарий4"),
(1, 4, '2021-07-23 18:15:55', "комментарий5");

insert into tags
(name)
values
("java"),
("hibernate"),
("mySql"),
("spring");

insert into tag2post
(tag_id, post_id)
values
(1, 4),
(2, 4),
(4, 4),
(1, 7),
(3, 7),
(1, 5),
(3, 5),
(4, 5);