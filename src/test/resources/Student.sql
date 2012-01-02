create table IF NOT EXISTS Student(
	rollNumber integer identity primary key ,
	firstName 	varchar(50),
	lastName	varchar(50),
	gender		varchar(1),
	dateOfBirth	date,
	lastUpdated	timestamp
);
create table IF NOT EXISTS StudentReadStatus (
	rollNumber integer,	
	readAt timestamp
);
