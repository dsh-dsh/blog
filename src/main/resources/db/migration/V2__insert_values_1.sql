insert into global_settings
(code, name, value)
values
("MULTIUSER_MODE", "Многопользовательский режим", "YES"),
("POST_PREMODERATION", "Премодерация постов", "YES"),
("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "YES");

insert into users (name, email, password, is_moderator, reg_time)
values ('moderator', 'mod@email.com', '$2a$12$zWdJzVm3Rhjof9o9g/rnSOX96GHbAbdSLocunRYp0pXETKXSX1Gxq', 1, NOW());
insert into users (name, email, password, is_moderator, reg_time)
values ('user', 'user@email.com', '$2a$12$zWdJzVm3Rhjof9o9g/rnSOX96GHbAbdSLocunRYp0pXETKXSX1Gxq', 0, NOW());
insert into users (name, email, password, is_moderator, reg_time)
values ('user2', 'user2@email.com', '$2a$12$zWdJzVm3Rhjof9o9g/rnSOX96GHbAbdSLocunRYp0pXETKXSX1Gxq', 0, NOW());