package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 5;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) userRepo.findAll();
    }

    public Page<User> listByPage(int pageNum, String sortField,
            String sortDir) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        return userRepo.findAll(pageable);
    }

    public List<Role> listRows() {
        return (List<Role>) roleRepo.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);

        // for old users, to update
        if (isUpdatingUser) {
            User existingUser = userRepo.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                // no password change
                user.setPassword(existingUser.getPassword());
            } else {
                // password change
                encodePassword(user);
            }
        } else {
            // for new users
            encodePassword(user);
        }

        return userRepo.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.getUserByEmail(email);

        // no email is found with the given email, so email is unique
        if (userByEmail == null) {
            return true;
        }

        // first time user creation
        boolean isCreatingNew = (id == null);

        // if user is first timer and checking if the email is unique
        if (isCreatingNew) {
            if (userByEmail != null) {
                return false;
            }
        }
        // matching if the current user is having the same id
        else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException(
                    "Could not find any user with ID " + id);
        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException(
                    "Could not find any user with ID " + id);
        }

        userRepo.deleteById(id);
    }

    public void updateEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnabledStatus(id, enabled);
    }
}
