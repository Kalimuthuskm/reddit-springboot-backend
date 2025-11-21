package com.skm.redditclone.repository;

import com.skm.redditclone.model.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dsl;

    public User createUser(@NotNull User users) {
        return dsl.insertInto(table("users"))
                .columns(
                        field("username"),
                        field("password"),
                        field("created_at"))
                .values(users.getUsername(),
                        users.getPassword(),
                        Instant.now())
                .returning(field("username"),
                        field("password"),
                        field("created_at"))
                .fetchOneInto(User.class);
    }

    public Optional<User> getUser(String username) {
        User users = dsl
                .select(field("username")
                        , field("password"),
                        field("created_at"))
                .from(table("users"))
                .where(field("username").eq(username))
                .fetchOneInto(User.class);

        return Optional.ofNullable(users);
    }

    public Boolean existsByUsername(String username) {
        Integer count = dsl
                .selectCount()
                .from(table("users"))
                .where(field("username").eq(username))
                .fetchOne(0, Integer.class);

        return count != null && count > 0;
    }

    public Optional<User> findByUsername(String username) {
        User user = dsl
                .select(field("id"), field("username"), field("password"), field("created_at"))
                .from(table("users"))
                .where(field("username").eq(username))
                .fetchOneInto(User.class);

        return Optional.ofNullable(user);
    }


    public boolean updateUsername(String oldusername, String newusername) {
        int rows = dsl.update(table("users"))
                .set(field("username"), newusername)
                .where(field("username").eq(oldusername))
                .execute();
        return rows > 0;
    }

    public boolean updatePassword(String username, String newpassword) {
        int rows = dsl.update(table("users"))
                .set(field("password"), newpassword)
                .where(field("username").eq(username))
                .execute();
        return rows > 0;
    }

    public boolean deleteUser(String username) {
        int rows = dsl.deleteFrom(table("users"))
                .where(field("username").eq(username))
                .execute();
        return rows > 0;
    }

    public String findPasswordByUsername(String username) {
        return dsl.select(field("password"))
                .from(table("users"))
                .where(field("username").eq(username))
                .fetchOneInto(String.class);
    }
}
