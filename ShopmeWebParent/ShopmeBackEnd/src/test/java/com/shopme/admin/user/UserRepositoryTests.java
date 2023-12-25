package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false) // changes made in this test case is permanent
public class UserRepositoryTests {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneRole() {

        Role roleAdmin = entityManager.find(Role.class, 1);
        User userSam = new User("sam@samtek.com", "passWord@123", "Sam",
                "Poddar");
        userSam.addRole(roleAdmin);

        User savedUser = repo.save(userSam);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRole() {

        User userRavi = new User("ravi@gmail.com", "ravi23@123", "Ravi",
                "Kumar");

        // hard-coding the values of roles from the db
        // the roles 'testcase' must be run @this file,
        // the data of the roles table should be
        // 1 Admin
        // 2 Salesperson
        // 3 Editor
        // 4 Shipper
        // 5 Assistant
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userRavi.addRole(roleEditor);
        userRavi.addRole(roleAssistant);

        User savedUser = repo.save(userRavi);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        User userSam = repo.findById(1).get();
        System.out.println(userSam);
        assertThat(userSam).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User userSam = repo.findById(1).get();
        userSam.setEnabled(true);
        userSam.setEmail("sam_poddar@ymail.com");

        repo.save(userSam);
    }

    @Test
    public void testUpdateUserRoles() {
        // remove 'editor' role and add 'salesperson' role
        User userRavi = repo.findById(2).get();

        Role roleEditor = new Role(3);
        Role roleSalesPerson = new Role(2);

        userRavi.getRoles().remove(roleEditor);
        userRavi.getRoles().add(roleSalesPerson);

        repo.save(userRavi);
    }

    @Test
    public void testDeleteUser() {
        Integer userId = 5;
        repo.deleteById(userId);

        assertThat(repo.findById(userId).isEmpty());
    }

    @Test
    public void testGetUserByEmail() {
        String email = "ravi@gmail.com";

        User user = repo.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long countById = repo.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 1;
        repo.updateEnabledStatus(id, false);
    }

    @Test
    public void testEnableUser() {
        Integer id = 6;
        repo.updateEnabledStatus(id, true);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 0; // page number is 0 based
        int pageSize = 4; // 4 elements per page

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);

        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));

        assertThat(listUsers.size()).isEqualTo(pageSize);

    }
}
