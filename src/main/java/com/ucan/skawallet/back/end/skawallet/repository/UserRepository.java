/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.repository;

import com.ucan.skawallet.back.end.skawallet.model.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author azm
 */
@Transactional(readOnly = true)
@Repository
public interface UserRepository extends JpaRepository<Users, Long>
{

    Optional<Users> findByEmail (String email);

    Optional<Users> findByName (String username);

    Optional<Users> findByPhone (String phone);

    public Optional<Users> findByVerificationCode (String code);

}
