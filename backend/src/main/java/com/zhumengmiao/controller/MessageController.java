package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.dto.MessageRequest;
import com.zhumengmiao.dto.PageResult;
import com.zhumengmiao.entity.Message;
import com.zhumengmiao.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;

    @GetMapping("/public/messages")
    public ApiResponse<List<Message>> getMessages() {
        return ApiResponse.success(messageRepository.findAllByOrderByCreateTimeDesc());
    }

    @PostMapping("/public/messages")
    public ApiResponse<Message> createMessage(@RequestBody MessageRequest request) {
        Message message = Message.builder()
                .username(request.getUsername())
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return ApiResponse.success(messageRepository.save(message));
    }

    @GetMapping("/admin/messages")
    public ApiResponse<PageResult<Message>> getMessagesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<Message> messagePage;
        if (keyword != null && !keyword.isEmpty()) {
            messagePage = messageRepository.findByTitleContainingOrContentContainingOrUsernameContaining(
                    keyword, keyword, keyword,
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")));
        } else {
            messagePage = messageRepository.findAll(
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")));
        }

        PageResult<Message> result = PageResult.<Message>builder()
                .content(messagePage.getContent())
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .build();

        return ApiResponse.success(result);
    }

    @PutMapping("/admin/messages/{id}/reply")
    public ApiResponse<Message> replyMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("留言不存在"));
        message.setReply(body.get("reply"));
        message.setReplied(true);
        return ApiResponse.success(messageRepository.save(message));
    }

    @DeleteMapping("/admin/messages/{id}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
