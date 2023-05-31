package cart.domain.order;

import cart.domain.CartItem;
import cart.domain.Money;
import cart.domain.member.MemberCoupon;
import cart.domain.member.MemberValidator;
import cart.exception.InvalidOrderOwnerException;
import cart.exception.InvalidOrderSizeException;

import java.util.List;

public class Order {

    private final Long id;
    private final OrderItems orderItems;
    private final Money deliveryFee;
    private final MemberCoupon memberCoupon;
    private final Long memberId;

    public Order(final OrderItems orderItems, final Money deliveryFee, final MemberCoupon memberCoupon, final Long memberId) {
        this(null, orderItems, deliveryFee, memberCoupon, memberId);
    }

    public Order(final Long id, final OrderItems orderItems, final Money deliveryFee, final MemberCoupon memberCoupon, final Long memberId) {
        this.id = id;
        this.orderItems = orderItems;
        this.deliveryFee = deliveryFee;
        this.memberCoupon = memberCoupon;
        this.memberId = memberId;
    }

    public static Order createFromCartItems(final List<CartItem> cartItems, final Money deliveryFee, final MemberCoupon memberCoupon, final Long memberId) {
        final OrderItems orderItems = OrderItems.from(cartItems);
        validateSize(orderItems);
        return new Order(orderItems, deliveryFee, memberCoupon, memberId);
    }

    private static void validateSize(final OrderItems orderItems) {
        if (orderItems.size() < 1) {
            throw new InvalidOrderSizeException();
        }
    }

    public void validateMember(final MemberValidator memberValidator) {
        if (!memberValidator.isOwner(memberId)) {
            throw new InvalidOrderOwnerException();
        }
    }

    public Money discountOrderPrice() {
        return memberCoupon.discount(orderItems.sumPrice());
    }

    public Money discountDeliveryFee() {
        return memberCoupon.discount(deliveryFee);
    }

    public Money getOrderPrice() {
        return orderItems.sumPrice();
    }

    public Money getDeliveryFee() {
        return deliveryFee;
    }

    public Long getId() {
        return id;
    }

    public OrderItems getOrderItems() {
        return orderItems;
    }

    public Long getMemberId() {
        return memberId;
    }

    public MemberCoupon getMemberCoupon() {
        return memberCoupon;
    }
}
