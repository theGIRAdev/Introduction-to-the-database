CREATE TABLE car (
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    brand VARCHAR(100)   NOT NULL,
    model VARCHAR(100)   NOT NULL,
    price NUMERIC(12, 2) NOT NULL
);

CREATE TABLE person (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name               VARCHAR(100) NOT NULL,
    age                INT          NOT NULL,
    has_driver_license BOOLEAN      NOT NULL DEFAULT FALSE,
    car_id             BIGINT       NOT NULL,
    CONSTRAINT person_car_fk FOREIGN KEY (car_id) REFERENCES car (id)
);
