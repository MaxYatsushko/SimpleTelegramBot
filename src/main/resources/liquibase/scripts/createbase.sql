-- liquibase formatted sql

-- changeset mIatsushko:1
CREATE TABLE notification_task (
    idTask BIGINT PRIMARY KEY,
    notificationDate TIMESTAMP,
    text TEXT,
    idChat BIGINT );

-- changeset mIatsushko:2
drop table notification_task;

-- changeset mIatsushko:3
CREATE TABLE notification_task (
                                   id_task BIGINT PRIMARY KEY,
                                   notification_date TIMESTAMP,
                                   text_message TEXT,
                                   id_chat BIGINT );