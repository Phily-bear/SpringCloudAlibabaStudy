package com.test.service.impl;

import com.test.entity.Book;
import com.test.entity.Borrow;
import com.test.entity.User;
import com.test.entity.UserBorrowDetail;
import com.test.mapper.BorrowMapper;
import com.test.service.BorrowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowService {
    @Resource
    private BorrowMapper borrowMapper;

    @Override
    public UserBorrowDetail getUserBorrowDetailByUid(int uid) {
        final List<Borrow> borrows = borrowMapper.getBorrowsByUid(uid);
        // RestTemplate支持远程调用
        RestTemplate restTemplate = new RestTemplate();
        // 通过getForObject调用其他服务，并将结果进行自动封装
        // 获取User信息
        final User user = restTemplate.getForObject("http://localhost:8101/user/" + uid, User.class);
        final List<Book> books = borrows.stream().map(borrow
                        -> restTemplate.getForObject("http://localhost:8201/book/" + borrow.getBid(), Book.class))
                .collect(Collectors.toList());
        return new UserBorrowDetail(user,books);
    }
}
