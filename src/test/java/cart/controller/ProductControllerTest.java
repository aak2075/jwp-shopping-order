package cart.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cart.dao.ProductDao;
import cart.domain.Product;
import cart.dto.ProductSaveRequest;
import cart.dto.ProductUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductDao productDao;

    @Test
    void 상품을_전체_조회한다() throws Exception {
        // given
        final Product product1 = new Product("허브티", "tea.jpg", 1000L);
        final Long id1 = productDao.saveAndGetId(product1);

        final Product product2 = new Product("우가티", "tea.jpg", 20000L);
        final Long id2 = productDao.saveAndGetId(product2);

        // when
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(id1.intValue())))
                .andExpect(jsonPath("$[0].name", is("허브티")))
                .andExpect(jsonPath("$[0].imageUrl", is("tea.jpg")))
                .andExpect(jsonPath("$[0].price", is(1000)))
                .andExpect(jsonPath("$[1].id", is(id2.intValue())))
                .andExpect(jsonPath("$[1].name", is("우가티")))
                .andExpect(jsonPath("$[1].imageUrl", is("tea.jpg")))
                .andExpect(jsonPath("$[1].price", is(20000)))
                .andDo(print());
    }

    @Test
    void 상품을_단일_조회한다() throws Exception {
        // given
        final Product product = new Product("허브티", "tea.jpg", 1000L);
        final Long id = productDao.saveAndGetId(product);

        // when
        mockMvc.perform(get("/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is("허브티")))
                .andExpect(jsonPath("$.imageUrl", is("tea.jpg")))
                .andExpect(jsonPath("$.price", is(1000)))
                .andDo(print());
    }

    @Test
    void 상품을_저장한다() throws Exception {
        // given
        final ProductSaveRequest dto = new ProductSaveRequest("허브티", "tea.jpg", 1000L);
        final String request = objectMapper.writeValueAsString(dto);

        // when
        final MvcResult mvcResult = mockMvc.perform(post("/products")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        // then
        final String location = mvcResult.getResponse().getHeader("Location");
        final Long id = Long.parseLong(location.substring(10));
        final Product result = productDao.findById(id).orElseThrow();
        assertAll(
                () -> assertThat(result.getName()).isEqualTo("허브티"),
                () -> assertThat(result.getImage()).isEqualTo("tea.jpg"),
                () -> assertThat(result.getPrice()).isEqualTo(1000L),
                () -> assertThat(location).isEqualTo("/products/" + result.getId())
        );
    }

    @Test
    void 상품을_수정한다() throws Exception {
        // given
        final Product product = new Product("허브티", "tea.jpg", 1000L);
        final Long id = productDao.saveAndGetId(product);
        final ProductUpdateRequest updateRequestDto = new ProductUpdateRequest("고양이", "cat.jpg", 1000000L);
        final String request = objectMapper.writeValueAsString(updateRequestDto);

        // when
        mockMvc.perform(put("/products/" + id)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        final Product result = productDao.findById(id).orElseThrow();
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(id),
                () -> assertThat(result.getName()).isEqualTo("고양이"),
                () -> assertThat(result.getImage()).isEqualTo("cat.jpg"),
                () -> assertThat(result.getPrice()).isEqualTo(1000000L)
        );
    }

    @Test
    void 상품을_삭제한다() throws Exception {
        // given
        final Product product = new Product("허브티", "tea.jpg", 1000L);
        final Long id = productDao.saveAndGetId(product);

        // when
        mockMvc.perform(delete("/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        // then
        assertThat(productDao.findById(id)).isNotPresent();
    }

    @Test
    void 이름이_100자_이상인_상품_등록을_요청하면_400_BadRequest_를_응답한다() throws Exception {
        // given
        final ProductSaveRequest dto = new ProductSaveRequest("허".repeat(101), "tea.jpg", 1000L);
        final String request = objectMapper.writeValueAsString(dto);

        // expect
        mockMvc.perform(post("/products")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 가격이_음수인_상품_등록을_요청하면_400_BadRequest_를_응답한다() throws Exception {
        // given
        final ProductSaveRequest dto = new ProductSaveRequest("허브티", "tea.jpg", -1L);
        final String request = objectMapper.writeValueAsString(dto);

        // expect
        mockMvc.perform(post("/products")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 등록되지_않은_상품_수정을_요청하면_404_BadRequest_를_응답한다() throws Exception {
        // given
        final ProductUpdateRequest updateRequestDto = new ProductUpdateRequest("고양이", "cat.jpg", 1000000L);
        final String request = objectMapper.writeValueAsString(updateRequestDto);

        // expect
        mockMvc.perform(put("/products/" + Long.MAX_VALUE)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 등록되지_않은_상품_삭제를_요청하면_404_BadRequest_를_응답한다() throws Exception {
        // expect
        mockMvc.perform(delete("/products/" + Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}

