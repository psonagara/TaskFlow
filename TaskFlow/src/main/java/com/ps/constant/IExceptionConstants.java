package com.ps.constant;

public interface IExceptionConstants {

	String REGISTRATION_FAILED = "Registration Failed";
	String USER_ALREADY_EXIST = "User already exist for given email";
	String LOGIN_FAILED = "Login Failed";
	String PROJECT_EXIST = "Project Already Exists with given name";
	String PROJECT_CREATION_FAIL = "Failed To Create new Project";
	String PROJECT_MEMBER_CREATION_FAIL = "Failed To Create Project Member";
	String PROJECT_NOT_FOUND = "Project not found for given project id";
	String USER_NOT_FOUND_FOR_ASSIGN_ID = "User not found for given assignId";
	String TASK_CREATION_FAIL = "Failed To Create new Task";
	String TASK_NOT_FOUND = "Task not found for given task id";
	String TASK_STATUS_CHANGE_FAILED = "Failed to change task status";
	String COMMENT_FAIL = "Failed to comment";
	String SUBTASK_CREATION_FAIL = "Failed to create sub task";
	String SUBTASK_NOT_FOUND = "SubTask not found for given id";
	String SUBTASK_COMPLETED_CHANGE_FAILED = "Failed to change subtask completed status";
}
