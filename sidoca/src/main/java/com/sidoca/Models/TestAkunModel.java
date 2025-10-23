package com.sidoca.Models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidoca.Models.DataBaseClass.Akun;

@Repository
public interface TestAkunModel extends JpaRepository<Akun, Integer>{
    // Method mencari akun
    @Query("SELECT a FROM Akun WHERE a.username = :identifier OR a.email = :identifier")
    Akun findByUsernameOrEmail(@Param("identifier") String identifier);

    Akun savedAkun = akunModel.save(akun);
    boolean success = savedAkun != null;
}
