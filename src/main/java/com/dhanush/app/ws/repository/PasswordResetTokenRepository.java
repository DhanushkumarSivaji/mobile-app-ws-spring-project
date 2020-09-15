package com.dhanush.app.ws.repository;

import org.springframework.data.repository.CrudRepository;

import com.dhanush.app.ws.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
	PasswordResetTokenEntity findByToken(String token);
}
