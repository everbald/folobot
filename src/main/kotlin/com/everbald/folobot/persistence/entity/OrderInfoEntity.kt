package com.everbald.folobot.persistence.entity


import com.everbald.folobot.service.folocoin.model.OrderInfoDto
import com.everbald.folobot.service.folocoin.model.OrderStatus
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment

@Entity
@Table(name = "order_info")
class OrderInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    val payment: SuccessfulPayment
)

fun OrderInfoEntity.toDto() = OrderInfoDto(id, userId, status, payment)