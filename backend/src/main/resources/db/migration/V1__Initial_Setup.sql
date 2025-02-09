CREATE TABLE customer(
    id SERIAL PRIMARY KEY ,
    name TEXT NOT NULL ,
    password TEXT NOT NULL ,
    date DATE NOT NULL ,
    gender TEXT NOT NULL
)