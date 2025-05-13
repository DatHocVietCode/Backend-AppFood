package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Voucher;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher,Long> {
    List<Voucher> findByActiveTrueAndUserIsNull();
    List<Voucher> findByActiveTrueAndUser_Id(Long userId);
}
