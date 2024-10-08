package jh.springboot.restapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import jh.springboot.restapi.config.auth.PrincipalDetails;
import jh.springboot.restapi.dto.BoardDto;
import jh.springboot.restapi.entity.User;
import jh.springboot.restapi.repository.UserRepository;
import jh.springboot.restapi.response.Response;
import jh.springboot.restapi.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;
    private final UserRepository userRepository;


    // 전체 게시글 조회
    @Operation(summary = "전체 게시글 보기", description = "전체 게시글을 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/boards")
    public Response getBoards() {
        return new Response("성공", "전체 게시물 리턴", boardService.getBoards());
    }


    // 개별 게시글 조회
    @Operation(summary = "개별 게시글 보기", description = "개별 게시글 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/boards/{id}")
    public Response getBoard(@PathVariable("id") Integer id) {
        return new Response("성공", "개별 게시물 리턴", boardService.getBoard(id));
    }


    // 게시글 작성
    @Operation(summary = "게시글 작성", description = "게시글을 작성한다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/boards/write")
    public Response write(@RequestBody BoardDto boardDto, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        return new Response("성공", "글 작성 성공", boardService.write(boardDto, user));
    }


    // 게시글 수정
    @Operation(summary = "게시글 수정", description = "게시글을 수정한다.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/boards/update/{id}")
    public Response edit(@RequestBody BoardDto boardDto, @PathVariable("id") Integer id, Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        if (user.getName().equals(boardService.getBoard(id).getWriter())) {
            // 로그인된 유저의 글이 맞다면
            return new Response("성공", "글 수정 성공", boardService.update(id, boardDto));
        } else {
            return new Response("실패", "본인 게시물만 수정할 수 있습니다.", null);

        }
    }


    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제한다.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/boards/delete/{id}")
    public Response delete(@PathVariable("id") Integer id, Authentication authentication) {


        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        if (user.getName().equals(boardService.getBoard(id).getWriter())) {
            // 로그인된 유저가 글 작성자와 같다면
            boardService.delete(id); // 이 메소드는 반환값이 없으므로 따로 삭제 수행해주고, 리턴에는 null을 넣어줌
            return new Response("성공", "글 삭제 성공", null);
        } else {
            return new Response("실패", "본인 게시물만 삭제할 수 있습니다.", null);
        }
    }
}
