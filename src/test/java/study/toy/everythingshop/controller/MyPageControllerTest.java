package study.toy.everythingshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import study.toy.everythingshop.dto.ProductOrderDTO;
import study.toy.everythingshop.dto.ProductSearchDTO;
import study.toy.everythingshop.entity.UserMEntity;
import study.toy.everythingshop.repository.UserDAO;
import study.toy.everythingshop.service.MyPageService;
import study.toy.everythingshop.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * fileName : myPageControllerTest
 * author   : pilming
 * date     : 2023-03-30
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional //계정 중복 생성을 막기위해 추가
public class MyPageControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserService userService;
    @MockBean
    MyPageService myPageService;
    @Autowired
    UserDAO userDAO;

    @Test
    @DisplayName("비로그인 마이페이지 접근시 리다이렉트")
    void test_1() throws Exception {
        mockMvc.perform(get("/myPage"))
                .andExpect(status().is3xxRedirection()) //상태코드가 3xx인지 체크
                .andExpect(redirectedUrlPattern("**/users/signIn")); //리다이렉트 경로 체크
    }

    @Test
    @DisplayName("로그인 사용자 마이페이지 접근")
    @WithMockUser
    void test_2() throws Exception {
        mockMvc.perform(get("/myPage"))
                .andExpect(status().isOk()) //상태코드가 OK(200)인지 체크
                .andExpect(view().name("myPage")); //화면 이름 체크
    }

    @Test
    @DisplayName("로그인 사용자 내정보 조회 접근")
    @WithUser(value = "admin") //@BeforeEach 와 @WithUserDetails 사용시 오류로 인해 커스텀 태그 생성
    void test_3() throws Exception{
        mockMvc.perform(get("/myPage/myInfo"))
                .andExpect(status().isOk()) //상태코드가 OK(200)인지 체크
                .andExpect(view().name("myInfo")) //화면 이름 체크
                .andExpect(model().attributeExists("userInfo")) //모델 객체 체크
                .andExpect(model().attribute("userInfo", hasProperty("userId", is("admin")))) //모델 객체의 userId 속성 체크
                .andExpect(model().attribute("userInfo", hasProperty("userNm", is("admin이름")))); //모델 객체의 userNm 속성 체크
    }

    @Test
    @DisplayName("로그인 사용자 내정보 수정 - 성공")
    @WithUser(value = "admin") //@BeforeEach 와 @WithUserDetails 사용시 오류로 인해 커스텀 태그 생성
    void test_4() throws Exception{
        String userId = "admin";
        String newUserName = "변경이름";

        //이름 수정 post요청
        mockMvc.perform(MockMvcRequestBuilders.post("/myPage/editMyInfo/{userId}", userId)
                        .param("userId", userId)
                        .param("userNm", newUserName))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/myPage/myInfo"));

        //todo dao와의 의존성 제거 필요
        UserMEntity userMEntity = userDAO.findByUserId(userId);
        assertThat(newUserName).isEqualTo(userMEntity.getUserNm());
    }

    @Test
    @DisplayName("로그인 사용자 내정보 수정 - 실패(변조시도)")
    @WithUser(value = "admin") //@BeforeEach 와 @WithUserDetails 사용시 오류로 인해 커스텀 태그 생성
    void test_5() throws Exception{
        String userId = "admin2"; //본인의 아이디가 아닌 다른아이디로 변조해서 시도 시
        String newUserName = "변경이름";

        //2023-04-02 현재는 예외에대한 처리가 없어서 Forbidden처리. 예외처리에대한 방법이 생기면 변경 필요
        mockMvc.perform(MockMvcRequestBuilders.post("/myPage/editMyInfo/{userId}", userId)
                        .param("userId", userId)
                        .param("userNm", newUserName))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 사용자 내정보 수정 - 실패(이름공백)")
    @WithUser(value = "admin") //@BeforeEach 와 @WithUserDetails 사용시 오류로 인해 커스텀 태그 생성
    void test_6() throws Exception{
        String userId = "admin";
        String newUserName = "";

        mockMvc.perform(MockMvcRequestBuilders.post("/myPage/editMyInfo/{userId}", userId)
                        .param("userId", userId)
                        .param("userNm", newUserName))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode("userInfo", "userNm", "NotBlank"));
    }

    @Test
    @DisplayName("나의 주문정보 목록 - 성공")
    @WithUser(value = "test")
    void myOrderList_noSearch() throws Exception {
        // given
        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        productSearchDTO.setUserNum(1L); // userNum 추가

        List<ProductOrderDTO> myOrderList = new ArrayList<>();
        myOrderList.add(new ProductOrderDTO());
        doReturn(myOrderList).when(myPageService).getMyOrderList(productSearchDTO);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/myPage/myOrderList")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("productSearchDTO", productSearchDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products")) // 모델에 products 속성이 존재하는지 확인
                .andExpect(model().attribute("productSearchDTO", productSearchDTO)) // 검색값이 제대로 모델에 추가되었는지 확인
                .andExpect(view().name("myOrderList")); // 이동할 뷰 확인

    }
    @Test
    @DisplayName("나의 주문정보 목록 - 주문목록 없음")
    @WithUser(value = "test")
    void myOrderList_noList() throws Exception {
        // given
        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        productSearchDTO.setUserNum(1L); // userNum 추가

        List<ProductOrderDTO> myOrderList = new ArrayList<>();
        doReturn(myOrderList).when(myPageService).getMyOrderList(productSearchDTO);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/myPage/myOrderList")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("productSearchDTO", productSearchDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("productSearchDTO", productSearchDTO)) // 검색값이 제대로 모델에 추가되었는지 확인
                .andExpect(view().name("myOrderList")); // 이동할 뷰 확인

    }
}
