package vn.dk.BackendFoodApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.dto.response.voucher.VoucherResponse;
import vn.dk.BackendFoodApp.model.Voucher;
import vn.dk.BackendFoodApp.repository.VoucherRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoucherService {

    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    UserService userService;

    public List<VoucherResponse> getListVoucher(){
        List<Voucher> vouchers = voucherRepository.findByActiveTrueAndUserIsNull();

        return vouchers.stream()
                .map(VoucherResponse::fromVoucher).toList();
    }

    public List<VoucherResponse> getActiveVouchersByUserId(Long userId) {
        List<Voucher> vouchers = voucherRepository.findByActiveTrueAndUser_Id(userId);
        return vouchers.stream()
                .map(VoucherResponse::fromVoucher).toList();
    }
    public Voucher findById(Long id) {
        Optional<Voucher> voucher =  voucherRepository.findById(id);
        if(voucher.isEmpty()){
            return null;
        }
        return voucher.get();
    }
    public void updateVoucher(Voucher voucher){
        voucherRepository.save(voucher);
    }
}
