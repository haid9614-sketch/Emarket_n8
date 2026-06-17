package com.n8.emarket.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address")
    private Long idAddress;
    @Column(name = "house_number")
    private String houseNumber;
    @Column(name = "ward", nullable = false)
    private String ward;
    @Column(name = "district", nullable = false)
    private String district;
    @Column(name = "city", nullable = false)
    private String city;
    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_delete")
    private Integer isDelete = 0;
    @Column(name = "name")
    private String name;
    @Column(name = "sdt")
    private String sdt;
}
