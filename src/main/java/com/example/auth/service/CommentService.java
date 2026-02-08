package com.example.auth.service;

import com.example.auth.dto.comment.CommentCreateRequest;
import com.example.auth.dto.comment.CommentResponse;
import com.example.auth.entity.Comment;
import com.example.auth.entity.Post;
import com.example.auth.entity.User;
import com.example.auth.exception.CommentNotFoundException;
import com.example.auth.exception.PostNotFoundException;
import com.example.auth.exception.UnauthorizedAccessException;
import com.example.auth.repository.CommentRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글에 대한 댓글 작성
     * */
    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CommentCreateRequest request){
        log.info("댓글 작성 - userid: {}, postId: {}", userId, postId);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UnauthorizedAccessException("사용자를 찾을수 없습니다"));

        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        // 댓글 엔티티 생성
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(request.getContent());

        comment = commentRepository.save( comment );

        // 댓글 개수 증가
        postRepository.incrementCommentCount(postId);

        return Comment.toCommentResponse(comment);
    }

    /**
     * 대댓글 작성
     * */
    @Transactional
    public CommentResponse createReply(Long userId, Long parentCommentId, CommentCreateRequest request){
        log.info("댓글 작성 - userid: {}, commentId: {}", userId, parentCommentId);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UnauthorizedAccessException("사용자를 찾을수 없습니다"));

        Comment comment = commentRepository.findByIdAndIsDeletedFalse(parentCommentId)
                .orElseThrow(()-> new CommentNotFoundException(parentCommentId));

        // 대대댓글은 지원하지 않음 !! 댓글의 댓글까지만 지원
        if(comment.getParent() != null){
            throw new IllegalArgumentException("대댓글에 댓긇을 작성할 수 없습니다.");
        }

        // 댓글 엔티티 생성
        Comment reply = new Comment();
        reply.setUser(user);
        reply.setPost(comment.getPost());
        reply.setParent(comment);
        reply.setContent(request.getContent());

        reply = commentRepository.save( reply );

        Long postId =  comment.getPost().getId();
        postRepository.incrementCommentCount(postId);

        return Comment.toCommentResponse(reply,0);
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentCreateRequest request){
        log.info("댓글 수정 요청 - userId:{}, commentId: {}", userId, commentId);

        Comment comment = commentRepository.findByIdWithUserAndPost(commentId)
                .orElseThrow(()->new CommentNotFoundException(commentId));

        // 수정은 본인만 가능하므로 권한 확인
        if (comment.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("댓글을 수정할 권합이 없습니다. ");
        }

        // 내용수정
        comment.setContent(request.getContent());

        comment = commentRepository.save(comment);

        Integer replyCount = commentRepository.countRepliesByParentId(commentId);

        return Comment.toCommentResponse(comment, replyCount);
    }


    /**
     * 댓글 삭제
     * */
    @Transactional
    public void deleteComment(Long userId, Long commentId){
        log.info("댓글 삭제 - userid: {}, commentId: {}", userId, commentId);

        // 댓글 조회
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(()->new CommentNotFoundException(commentId));

        // 댓글 삭제는 댓글 작성자 본인만 삭제 가능 , 게시물 작성자도 삭제 가능
        Long commentUser = comment.getUser().getId();
        Long postUser = comment.getPost().getUser().getId();

        if (!commentUser.equals(userId)){
            throw new UnauthorizedAccessException("댓글 삭제 권한이 없습니다");
        }

        // 여기서부터는 삭제 권한이 있는 경우
        comment.softDelete();
        commentRepository.save(comment);

        // Post 의 댓글 감소시킴
        postRepository.decrementCommentCount(comment.getPost().getId());
    }

    /**
     * 게시글의 댓글 목록 조회
     * */
    @Transactional(readOnly = true)
    public Page<CommentResponse> gwtCommentByPostId(Long userid, Long postId, Pageable pageable){
        log.info("게시글 댓글 목혹 조회 - postId: {}", postId);

        // 게시글 있는지 확인
        if (!postRepository.existsById(postId)){
            throw new PostNotFoundException(postId);
        }

        // 댓글 목록 추출
        Page<Comment> comments = commentRepository.findAllCommentsByPostId(postId, pageable);

        // 각 댓글들의 id를 추출하여 리스트로 저장
//        List<Long> commentIds = comments.getContent().stream()
//                .map(Comment::getId).toList();

        return comments.map(Comment::toCommentResponse);
    }

    /**
     * 특정 댓글의 대댓글 목록 조회
     * */
    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(Long userId, Long commentId){
        log.info("대댓글 목록 조회 - parentCommentId: {}", commentId);

        if (commentRepository.existsById(commentId)){
            throw new CommentNotFoundException(commentId);
        }

        List<Comment> comments = commentRepository.findRepliesByParentId(commentId);

        return comments.stream().map(Comment::toCommentResponse).toList();
    }


    /**
     * 댓글 단독 조회(상세 조회)
     * */
    @Transactional(readOnly = true)
    public CommentResponse getComment(Long userId, Long commentId){
        log.info("댓글 상세 조회 = commentId: {}", commentId);

        Comment comment = commentRepository.findByIdWithUserAndPost(commentId)
                .orElseThrow(()->new CommentNotFoundException(commentId));

        Integer replyCount = commentRepository.countRepliesByParentId(commentId);

        return Comment.toCommentResponse(comment, replyCount);
    }

}
