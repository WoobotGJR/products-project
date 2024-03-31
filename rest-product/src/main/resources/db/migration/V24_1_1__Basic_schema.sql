-- versioning of DBs with flywayDB
create schema if not exists catalogue;

-- t in t_product means table
-- c in c_title and c_details means, that its not foreign key
create table catalogue.t_product(
    id serial primary key,
    c_title varchar(50) not null check(length(trim(c_title)) >= 3), -- check if string has more than 3 non-space symbols
    c_details varchar(1000)
);

--In the context of databases, "versioning" refers to managing and tracking changes in the structure and content of a database over time.
--Database versioning includes:
--    Tracking changes: Recording all changes made to the database, including the creation, modification, and deletion of tables, columns, indexes, and other database objects.
--    Schema version management: Managing versions of the database schema to know what changes have been made to the database at different stages of its lifecycle.
--    Backup and restore: Ability to restore the database to previous versions if needed.
--    Data integrity: Ensuring that versioning does not compromise the integrity and reliability of the data in the database.
--    Collaboration and coordination: Ensuring that changes to the database are coordinated and available to all members of the development team.
--    Migration management: Applying schema changes to existing databases and ensuring consistent database version upgrades.
--Overall, database versioning helps organize and control changes in the database, ensuring stability, reliability, and data integrity throughout its lifecycle.