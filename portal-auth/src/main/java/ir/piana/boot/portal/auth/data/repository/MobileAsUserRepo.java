package ir.piana.boot.portal.auth.data.repository;

import ir.piana.boot.portal.auth.data.entity.MobileAsUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MobileAsUserRepo extends JpaRepository<MobileAsUserEntity, String> {
    Optional<MobileAsUserEntity> findByMobile(String mobile);
}
