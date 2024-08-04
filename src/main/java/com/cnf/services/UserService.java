package com.cnf.services;

import com.cnf.entity.PasswordResetToken;
import com.cnf.entity.User;
import com.cnf.repository.IPasswordResetTokenRepository;
import com.cnf.repository.IRoleRepository;
import com.cnf.repository.IUserRepository;
import com.cnf.constants.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    IPasswordResetTokenRepository passwordResetTokenRepository;
    public Optional<User> findByEmail(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        if (result.isPresent()){
            return result;
        }
        else return null;
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public Page<User> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.userRepository.findAll(pageable);
    }
    public User getUserbyUserName(String username){
        return  userRepository.findByUsername(username);
    }
    public User getUserByUserID(Long userID){
        return  userRepository.getUserByUserID(userID);
    }
    public void save(User user) {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));
        user.setProvider(Provider.LOCAL.value);
        user.setImg("/client_assets/img/user.png");
        userRepository.save(user);

        Long userId = userRepository.getUserIdByUsername(user.getUsername());
        Long roleId = roleRepository.getRoleIdByName("USER");
        if(roleId != 0 && userId != 0){
            userRepository.addRoleToUser(userId,roleId);
        }
    }
    public void saveOauthUser(String email, String username, String fullName) {
        User usert = userRepository.findByUsername(username);
        if (usert != null) {
            return;
        }
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFull_name(fullName);
        user.setImg("/client_assets/img/user.png");
        user.setPassword(new BCryptPasswordEncoder().encode(username));
        user.setProvider(Provider.GOOGLE.value);
        userRepository.save(user);

        Long userId = userRepository.getUserIdByUsername(user.getUsername());
        Long roleId = roleRepository.getRoleIdByName("USER");
        if (roleId != 0 && userId != 0) {
            userRepository.addRoleToUser(userId, roleId);
        }
    }
    public void updateUser(User user){
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        existingUser.setFull_name(user.getFull_name());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        userRepository.save(existingUser);
    }

    public Optional<User> findByUsername1(String username) {
        return userRepository.findByUsername1(username);
    }

    public Optional<User> findByUserid1(Long userId) {
        return userRepository.findById(userId);
    }

    public void updatePassword(User user, String newPassword) {
        // Cập nhật mật khẩu mới cho đối tượng User
        user.setPassword(new BCryptPasswordEncoder()
                .encode(newPassword));

        // Lưu thay đổi vào cơ sở dữ liệu
        userRepository.save(user);
    }




}
