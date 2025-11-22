package com.skm.redditclone.repository;

import com.skm.Tables;
import com.skm.redditclone.model.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dsl;

    public User createUser(@NotNull User users) {
        return dsl.insertInto(Tables.USERS)
                .columns(
                        Tables.USERS.USERNAME,
                        Tables.USERS.PASSWORD,
                        Tables.USERS.CREATED_AT
                ).values(users.getUsername(),
                        users.getPassword(),
                        LocalDateTime.now())
                .returning(
                        Tables.USERS.USERNAME,
                        Tables.USERS.PASSWORD,
                        Tables.USERS.CREATED_AT
                )
                .fetchOneInto(User.class);
    }

    public Optional<User> getUser(String username) {
        User users = dsl
                .select(
                        Tables.USERS.USERNAME,
                        Tables.USERS.PASSWORD,
                        Tables.USERS.CREATED_AT)
                .from(Tables.USERS)
                .where(Tables.USERS.USERNAME.eq(username))
                .fetchOneInto(User.class);

        return Optional.ofNullable(users);
    }

    public Boolean existsByUsername(String username) {
        Integer count = dsl
                .selectCount()
                .from(Tables.USERS)
                .where(Tables.USERS.USERNAME.eq(username))
                .fetchOne(0, Integer.class);

        return count != null && count > 0;
    }

    public Optional<User> findByUsername(String username) {
        User user = dsl
                .select(Tables.USERS.ID,
                        Tables.USERS.USERNAME,
                        Tables.USERS.PASSWORD,
                        Tables.USERS.CREATED_AT)
                .from(Tables.USERS)
                .where(Tables.USERS.USERNAME.eq(username))
                .fetchOneInto(User.class);

        return Optional.ofNullable(user);
    }


    public boolean updateUsername(String oldusername, String newusername) {
        int rows = dsl.update(Tables.USERS)
                .set(Tables.USERS.USERNAME, newusername)
                .where(Tables.USERS.USERNAME.eq(oldusername))
                .execute();
        return rows > 0;
    }

    public boolean updatePassword(String username, String newpassword) {
        int rows = dsl.update(Tables.USERS)
                .set(Tables.USERS.PASSWORD, newpassword)
                .where(Tables.USERS.USERNAME.eq(username))
                .execute();
        return rows > 0;
    }

    public boolean deleteUser(String username) {
        int rows = dsl.deleteFrom(Tables.USERS)
                .where(Tables.USERS.USERNAME.eq(username))
                .execute();
        return rows > 0;
    }

    public String findPasswordByUsername(String username) {
        return dsl.select(Tables.USERS.PASSWORD)
                .from(Tables.USERS)
                .where(Tables.USERS.USERNAME.eq(username))
                .fetchOneInto(String.class);
    }
}
