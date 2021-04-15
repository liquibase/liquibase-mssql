liquibase-mssql [![Build and Test Extension](https://github.com/liquibase/liquibase-mssql/actions/workflows/build.yml/badge.svg)](https://github.com/liquibase/liquibase-mssql/actions/workflows/build.yml)
===============

MS SQL Server specific extension


This extension wraps insert and load\_data commands with "set identity\_insert tablename on/off" 
so that data can be inserted into auto increment columns when using MS SQL Server. 

For the Command Line Interface (CLI)\
In order to use this extension, copy the liquibase-mssql-<version>.jar file into the lib directory of your liquibase install\
For instance if you installed your code into\
  /opt/liquibase \
  Copy this file into /opt/liquibase/lib

Then run your liquibase update commands

 

Example Changelog 
```
<databaseChangeLog
	xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
						http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.2.xsd
						http://www.liquibase.org/xml/ns/dbchangelog-ext
						http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd">
	<changeSet author="molivasdat" id="123">
        <createTable tableName="rank">
            <column autoIncrement="true" computed="false" name="ID" type="numeric(18, 0)">
                <constraints nullable="false"/>
            </column>
            <column computed="false" name="Name" type="char(50)"/>
        </createTable>
    </changeSet>
    <changeSet id="124" author="mikeo">
        <insert tableName="rank">
            <column name="ID" value="3"/>
            <column name="Name" value="Nota Person"/>
        </insert>
    </changeSet>
    <!--<changeSet author="molivasdat" id="loadData-tbl_rank">
		<loadData tableName="rank" file="mydata_table.csv" relativeToChangelogFile="true" separator=";" quotchar="&quot;">
			<column header="ID" name="ID" type="NUMERIC"/>
			<column header="Name" name="Name" type="STRING"/>
		</loadData>
	</changeSet>-->
</databaseChangeLog>
```

loaddata is commented out since it currently does not work with this extension

As an alternative to use loaddata until this works \
You might use this as an example
```
<changeSet author="molivasdat" id="loadData-tbl_rank">
    <sql>SET IDENTITY_INSERT rank ON</sql>
		<loadData tableName="rank" file="mydata_table.csv" relativeToChangelogFile="true" separator=";" quotchar="&quot;">
			<column header="ID" name="ID" type="NUMERIC"/>
			<column header="Name" name="Name" type="STRING"/>
		</loadData>
    <sql>SET IDENTITY_INSERT rank OFF</sql>
</changeSet>
```

Or 
as 3 separate changeSets and remove 2 of them later
```
<changeSet author="molivasdat" id="removelater-1">
    <sql>SET IDENTITY_INSERT rank ON</sql>
</changeSet>
<changeSet author="molivasdat" id="loadData-tbl_rank">
		<loadData tableName="rank" file="mydata_table.csv" relativeToChangelogFile="true" separator=";" quotchar="&quot;">
			<column header="ID" name="ID" type="NUMERIC"/>
			<column header="Name" name="Name" type="STRING"/>
		</loadData>
</changeSet>
<changeSet author="molivasdat" id="removelater-2">
    <sql>SET IDENTITY_INSERT rank OFF</sql>
</changeSet>
```
