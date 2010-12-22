CREATE TABLE ACT_GE_PROPERTY ( NAME_ varchar(64), VALUE_ varchar(300), REV_ integer, primary key (NAME_) ); 

INSERT INTO ACT_GE_PROPERTY values ('schema.version', '5.0', 1); 

INSERT INTO ACT_GE_PROPERTY values ('next.dbid', '1', 1); 

CREATE TABLE ACT_GE_BYTEARRAY ( ID_ varchar(64), REV_ integer, NAME_ varchar(255), DEPLOYMENT_ID_ varchar(64), BYTES_ longvarbinary, primary key (ID_) ); 

CREATE TABLE ACT_RE_DEPLOYMENT ( ID_ varchar(64), NAME_ varchar(255), DEPLOY_TIME_ timestamp, primary key (ID_) ); 

CREATE TABLE ACT_RU_EXECUTION ( ID_ varchar(64), REV_ integer, PROC_INST_ID_ varchar(64), BUSINESS_KEY_ varchar(255), PARENT_ID_ varchar(64), PROC_DEF_ID_ varchar(64), SUPER_EXEC_ varchar(64), ACT_ID_ varchar(255), IS_ACTIVE_ bit, IS_CONCURRENT_ bit, IS_SCOPE_ bit, primary key (ID_) ); 

CREATE TABLE ACT_RU_JOB ( ID_ varchar(64) NOT NULL, REV_ integer, TYPE_ varchar(255) NOT NULL, LOCK_EXP_TIME_ timestamp, LOCK_OWNER_ varchar(255), EXCLUSIVE_ boolean, EXECUTION_ID_ varchar(64), PROCESS_INSTANCE_ID_ varchar(64), RETRIES_ integer, EXCEPTION_STACK_ID_ varchar(64), EXCEPTION_MSG_ varchar(255), DUEDATE_ timestamp, REPEAT_ varchar(255), HANDLER_TYPE_ varchar(255), HANDLER_CFG_ varchar(255), primary key (ID_) ); 

CREATE TABLE ACT_ID_GROUP ( ID_ varchar(64), REV_ integer, NAME_ varchar(255), TYPE_ varchar(255), primary key (ID_) ); 

CREATE TABLE ACT_ID_MEMBERSHIP ( USER_ID_ varchar(64), GROUP_ID_ varchar(64), primary key (USER_ID_, GROUP_ID_) ); 

CREATE TABLE ACT_ID_USER ( ID_ varchar(64), REV_ integer, FIRST_ varchar(255), LAST_ varchar(255), EMAIL_ varchar(255), PWD_ varchar(255), primary key (ID_) ); 

CREATE TABLE ACT_RE_PROCDEF ( ID_ varchar(64), CATEGORY_ varchar(255), NAME_ varchar(255), KEY_ varchar(255), VERSION_ integer, DEPLOYMENT_ID_ varchar(64), RESOURCE_NAME_ varchar(255), DGRM_RESOURCE_NAME_ varchar(255), HAS_START_FORM_KEY_ bit, primary key (ID_) ); 

CREATE TABLE ACT_RU_TASK ( ID_ varchar(64), REV_ integer, EXECUTION_ID_ varchar(64), PROC_INST_ID_ varchar(64), PROC_DEF_ID_ varchar(64), NAME_ varchar(255), DESCRIPTION_ varchar(255), TASK_DEF_KEY_ varchar(255), ASSIGNEE_ varchar(64), PRIORITY_ integer, CREATE_TIME_ timestamp, primary key (ID_) ); 

CREATE TABLE ACT_RU_IDENTITYLINK ( ID_ varchar(64), REV_ integer, GROUP_ID_ varchar(64), TYPE_ varchar(255), USER_ID_ varchar(64), TASK_ID_ varchar(64), primary key (ID_) ); 

CREATE TABLE ACT_RU_VARIABLE ( ID_ varchar(64) not null, REV_ integer, TYPE_ varchar(255) not null, NAME_ varchar(255) not null, EXECUTION_ID_ varchar(64), PROC_INST_ID_ varchar(64), BYTEARRAY_ID_ varchar(64), DOUBLE_ double, LONG_ bigint, TEXT_ varchar(255), TEXT2_ varchar(255), primary key (ID_) ); 

CREATE TABLE ACT_HI_PROCINST ( ID_ varchar(64) not null, PROC_INST_ID_ varchar(64) not null, BUSINESS_KEY_ varchar(255), PROC_DEF_ID_ varchar(64) not null, START_TIME_ timestamp not null, END_TIME_ timestamp, DURATION_ bigint, START_USER_ID_ varchar(255), START_ACT_ID_ varchar(255), END_ACT_ID_ varchar(255), primary key (ID_), unique (PROC_INST_ID_) ); 

CREATE TABLE ACT_HI_ACTINST ( ID_ varchar(64) not null, PROC_DEF_ID_ varchar(64) not null, PROC_INST_ID_ varchar(64) not null, EXECUTION_ID_ varchar(64) not null, ACT_ID_ varchar(255) not null, ACT_NAME_ varchar(255), ACT_TYPE_ varchar(255) not null, ASSIGNEE_ varchar(64), START_TIME_ timestamp not null, END_TIME_ timestamp, DURATION_ bigint, primary key (ID_) ); 

CREATE TABLE ACT_HI_DETAIL ( ID_ varchar(64) not null, TYPE_ varchar(255) not null, PROC_INST_ID_ varchar(64) not null, EXECUTION_ID_ varchar(64) not null, ACT_INST_ID_ varchar(64), NAME_ varchar(255) not null, VAR_TYPE_ varchar(255), REV_ integer, TIME_ timestamp not null, BYTEARRAY_ID_ varchar(64), DOUBLE_ double, LONG_ bigint, TEXT_ varchar(255), TEXT2_ varchar(255), primary key (ID_) ); 

CREATE INDEX ACT_IDX_EXEC_BUSKEY on ACT_RU_EXECUTION(BUSINESS_KEY_); CREATE INDEX ACT_IDX_TASK_CREATE on ACT_RU_TASK(CREATE_TIME_); CREATE INDEX ACT_IDX_IDENT_LNK_USER on ACT_RU_IDENTITYLINK(USER_ID_); CREATE INDEX ACT_IDX_IDENT_LNK_GROUP on ACT_RU_IDENTITYLINK(GROUP_ID_); CREATE INDEX ACT_IDX_HI_PRO_INST_END on ACT_HI_PROCINST(END_TIME_); CREATE INDEX ACT_IDX_HI_PRO_I_BUSKEY on ACT_HI_PROCINST(BUSINESS_KEY_); CREATE INDEX ACT_IDX_HI_ACT_INST_START on ACT_HI_ACTINST(START_TIME_); CREATE INDEX ACT_IDX_HI_ACT_INST_END on ACT_HI_ACTINST(END_TIME_); CREATE INDEX ACT_IDX_HI_DETAIL_PROC_INST on ACT_HI_DETAIL(PROC_INST_ID_); CREATE INDEX ACT_IDX_HI_DETAIL_ACT_INST on ACT_HI_DETAIL(ACT_INST_ID_); CREATE INDEX ACT_IDX_HI_DETAIL_TIME on ACT_HI_DETAIL(TIME_); CREATE INDEX ACT_IDX_HI_DETAIL_NAME on ACT_HI_DETAIL(NAME_); 

ALTER TABLE ACT_GE_BYTEARRAY add constraint ACT_FK_BYTEARR_DEPL foreign key (DEPLOYMENT_ID_) references ACT_RE_DEPLOYMENT; 

ALTER TABLE ACT_RU_EXECUTION add constraint ACT_FK_EXE_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION; 

ALTER TABLE ACT_RU_EXECUTION add constraint ACT_FK_EXE_PARENT foreign key (PARENT_ID_) references ACT_RU_EXECUTION; ALTER TABLE ACT_RU_EXECUTION add constraint ACT_FK_EXE_SUPER foreign key (SUPER_EXEC_) references ACT_RU_EXECUTION; ALTER TABLE ACT_RU_EXECUTION add constraint ACT_UNIQ_RU_BUS_KEY unique(PROC_DEF_ID_, BUSINESS_KEY_); ALTER TABLE ACT_HI_PROCINST add constraint ACT_UNIQ_HI_BUS_KEY unique(PROC_DEF_ID_, BUSINESS_KEY_); 

ALTER TABLE ACT_ID_MEMBERSHIP add constraint ACT_FK_MEMB_GROUP foreign key (GROUP_ID_) references ACT_ID_GROUP; 

ALTER TABLE ACT_ID_MEMBERSHIP add constraint ACT_FK_MEMB_USER foreign key (USER_ID_) references ACT_ID_USER; 

ALTER TABLE ACT_RU_IDENTITYLINK add constraint ACT_FK_TSKASS_TASK foreign key (TASK_ID_) references ACT_RU_TASK; 

ALTER TABLE ACT_RU_TASK add constraint ACT_FK_TASK_EXE foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION; 

ALTER TABLE ACT_RU_TASK add constraint ACT_FK_TASK_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION; 

ALTER TABLE ACT_RU_TASK add constraint ACT_FK_TASK_PROCDEF foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF; 

ALTER TABLE ACT_RU_VARIABLE add constraint ACT_FK_VAR_EXE foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION; 

ALTER TABLE ACT_RU_VARIABLE add constraint ACT_FK_VAR_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION; 

ALTER TABLE ACT_RU_VARIABLE add constraint ACT_FK_VAR_BYTEARRAY foreign key (BYTEARRAY_ID_) references ACT_GE_BYTEARRAY; 

ALTER TABLE ACT_RU_JOB add constraint ACT_FK_JOB_EXCEPTION foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY; 

INSERT INTO OE_ROLE(ROLE_ID, DESCRIPTION, NAME) VALUES (1, 'Application Administrator', 'admin'), (2, 'Default User Role', 'user'), (3, NULL, 'Management'), (4, NULL, 'Manager'), (5, NULL, 'Developer'); 

INSERT INTO OE_SETTINGS(ID, MAIL_CONFIRMATION, USER_LOGIN, USER_REG, WELCOM_MESSAGE) VALUES (1, TRUE, TRUE, TRUE, 'You can login with username: admin and password: admin'); 

INSERT INTO PUBLIC.OE_USER(USER_ID, ACTIVATION_KEY, ACTIVE, CREATED_ON, EMAIL, FIRST_NAME, LANGUAGE, LAST_NAME, MIDDLE_NAME, PASSWORD, TEMPORARY_PASSWORD, USER_NAME) VALUES (1, NULL, TRUE, 1293017732034, 'admin@yourcompany.org', 'admin', 'se', 'admin', NULL, 'liiHgKcA1sEBisdWUN9fLEc2gBo=', FALSE, 'admin'), (2, NULL, TRUE, 1293017947350, 'support@openeye.nu', 'Demo', 'se', 'User', NULL, 'Uhv0KXNKlfyzxhEJThWrPtd/u8k=', FALSE, 'demo'); 

INSERT INTO PUBLIC.OE_USER_PREFERENCES(USERID, BIRTH_YEAR, PREFIX, START_PAGE, SUFFIX) VALUES (1, NULL, NULL, 'Assigned Tasks', NULL), (2, NULL, NULL, 'Assigned Tasks', NULL); 

INSERT INTO PUBLIC.OE_USER_ROLE(USER_ID, ROLE_ID) VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (2, 2), (2, 5); 
