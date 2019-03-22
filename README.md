liquibase-mssql
===============

MS SQL Server specific extension


This extension wraps insert and load\_data commands with "set identity\_insert tablename on/off" 
so that data can be inserted into auto increment columns when using MS SQL Server. 

