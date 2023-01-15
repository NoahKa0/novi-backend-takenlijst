INSERT INTO roles(rolename)
VALUES ('ADMIN'),
       ('TEAM_LEADER'),
       ('TEAM_MEMBER');

INSERT INTO users(username, password)
VALUES ('All', '$2a$12$9WupfKrabLivJLGmykWz5.94ulHjP8.B/5RJ7zG6MtLL.m0QSSpoe'),
       ('Admin', '$2a$12$9WupfKrabLivJLGmykWz5.94ulHjP8.B/5RJ7zG6MtLL.m0QSSpoe'),
       ('Leader', '$2a$12$9WupfKrabLivJLGmykWz5.94ulHjP8.B/5RJ7zG6MtLL.m0QSSpoe'),
       ('Member', '$2a$12$9WupfKrabLivJLGmykWz5.94ulHjP8.B/5RJ7zG6MtLL.m0QSSpoe');

INSERT INTO users_roles(users_username, roles_rolename)
VALUES ('All', 'ADMIN'),
       ('All', 'TEAM_LEADER'),
       ('All', 'TEAM_MEMBER');

INSERT INTO users_roles(users_username, roles_rolename)
VALUES ('Admin', 'ADMIN');

INSERT INTO users_roles(users_username, roles_rolename)
VALUES ('Leader', 'TEAM_LEADER');

INSERT INTO users_roles(users_username, roles_rolename)
VALUES ('Member', 'TEAM_MEMBER');