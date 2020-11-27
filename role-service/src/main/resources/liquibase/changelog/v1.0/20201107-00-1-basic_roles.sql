INSERT INTO role_service.role(name, description)
VALUES ('RoleManager', 'Allows creation, modification and viewing all roles'),
       ('PositionManager', 'Allows creation, modification and viewing all positions'),
       ('TeaTypeManager', 'Allows creation, modification and viewing all tea types'),
       ('User', 'Basic user role'),
       ('ServiceUser', 'Service user for microservices only, although it wont do anything if user has it'),
       ('TeaDealer', 'Role for tea dealers')
;

DO
$$
    DECLARE
        id bigint;
    BEGIN
        FOR id in 1..10000
            LOOP
                INSERT INTO role_service.role(name, description)
                VALUES ('Role' || id, 'Role ' || id || ' description');
            end loop;
    end;
$$;
