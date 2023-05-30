package cart.domain;

import cart.exception.InvalidCartItemOwnerException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CartItemTest {

    @Test
    void 수량의_초기값은_1이다() {
        // given
        final Member member = new Member("pizza@pizza.com", "password");
        final Product product = new Product("pizza1", "pizza1.jpg", 8900L);
        final CartItem cartItem = new CartItem(member, product);

        // expect
        assertThat(cartItem.getQuantity()).isEqualTo(1);
    }

    @Test
    void 수량을_변경한다() {
        // given
        final Member member = new Member("pizza@pizza.com", "password");
        final Product product = new Product("pizza1", "pizza1.jpg", 8900L);
        final CartItem cartItem = new CartItem(member, product);

        // when
        cartItem.changeQuantity(2);

        // then
        assertThat(cartItem.getQuantity()).isEqualTo(2);
    }

    @Test
    void 소유주가_아니라면_예외를_던진다() {
        // given
        final Member member = new Member(1L, "pizza@pizza.com", "password");
        final Product product = new Product("pizza1", "pizza1.jpg", 8900L);
        final CartItem cartItem = new CartItem(member, product);

        // expect
        assertThatThrownBy(() -> cartItem.checkOwner(new Member(2L, "email", "password")))
                .isInstanceOf(InvalidCartItemOwnerException.class)
                .hasMessage("장바구니의 소유자가 아닙니다.");
    }

    @Test
    void 소유주가_맞다면_예외를_던지지_않는다() {
        // given
        final Member member = new Member(1L, "pizza@pizza.com", "password");
        final Product product = new Product("pizza1", "pizza1.jpg", 8900L);
        final CartItem cartItem = new CartItem(member, product);

        // expect
        assertThatNoException().isThrownBy(() -> cartItem.checkOwner(member));
    }
}
